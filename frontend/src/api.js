// ---- login state (kept in localStorage so a page refresh doesn't log you out) ----
let auth = JSON.parse(localStorage.getItem('auth') || 'null');   // { token, userId, role, username }

export function getAuth() {
  return auth;
}

export function saveAuth(newAuth) {
  auth = newAuth;
  localStorage.setItem('auth', JSON.stringify(newAuth));
}

export function clearAuth() {
  auth = null;
  localStorage.removeItem('auth');
}

// ---- errors: turn HTTP failures into readable messages ----
export class ApiError extends Error {
  constructor(status, message, details = []) {
    super(message);
    this.status = status;     // HTTP status, 0 = server not reachable
    this.details = details;   // list of field problems, e.g. "Name: must not be blank"
  }
}

const FIELD_LABELS = {
  name: 'Name',
  username: 'Username',
  password: 'Password',
  role: 'Role',
  description: 'Description',
  maxConsumption: 'Max consumption',
  energyClass: 'Energy class',
};

// backend validation details look like "maxConsumption:must be greater than 0"
function formatDetail(detail) {
  const text = String(detail);
  const colon = text.indexOf(':');
  if (colon <= 0) return text;
  const field = text.slice(0, colon).trim();
  const problem = text.slice(colon + 1).trim();
  return `${FIELD_LABELS[field] ?? field}: ${problem}`;
}

// data = parsed JSON body from RestExceptionHandler: { status, error, message, resource, details }
function describe(status, data, details) {
  const resource = data?.resource;
  switch (status) {
    case 400:
      if (details.length > 0) return 'Some fields need attention.';
      if (resource && resource !== 'Credentials') return `${resource}.`;   // e.g. "Devices can only be assigned to clients"
      return data?.detail || 'The request was not valid. Check the values and try again.';
    case 401:
      return 'Wrong username or password.';
    case 403:
      return "You don't have permission to do this.";
    case 404:
      return resource ? `${resource} was not found.` : 'This item no longer exists. Refresh the page.';
    case 409:
      return resource ? `${resource} already exists.` : 'This already exists.';
    case 502:
    case 503:
    case 504:
      return 'A service is unavailable, so nothing was changed. Try again in a moment.';
    default:
      return data?.message || `Something went wrong (error ${status}).`;
  }
}

async function toApiError(res) {
  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    // body was not JSON (e.g. an empty 403 from the gateway)
  }
  const details = Array.isArray(data?.details) ? data.details.map(formatDetail) : [];
  return new ApiError(res.status, describe(res.status, data, details), details);
}

// ---- one place for every HTTP call ----
async function request(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth) headers.Authorization = 'Bearer ' + auth.token;

  let res;
  try {
    res = await fetch('/api' + path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, "Can't reach the server. Check that the containers are running.");
  }

  // logged in but got 401 → token expired or invalid → back to the login page
  if (res.status === 401 && auth) {
    clearAuth();
    window.location.reload();
    throw new ApiError(401, 'Your session has expired. Log in again.');
  }
  if (!res.ok) throw await toApiError(res);
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  // auth
  login:             (username, password) => request('POST', '/auth/login', { username, password }),
  register:          (u)        => request('POST', '/auth/register', u),
  createUserByAdmin: (u)        => request('POST', '/auth/admin/users', u),

  // users (admin)
  getUsers:     ()         => request('GET', '/users'),
  updateUser:   (id, u)    => request('PUT', `/users/${id}`, u),
  deleteUser:   (id)       => request('DELETE', `/users/${id}`),

  // devices
  getDevices:   ()         => request('GET', '/devices'),
  getMyDevices: (userId)   => request('GET', `/devices/user/${userId}`),
  createDevice: (d)        => request('POST', '/devices', d),
  updateDevice: (id, d)    => request('PUT', `/devices/${id}`, d),
  deleteDevice: (id)       => request('DELETE', `/devices/${id}`),
  assign:       (dId, uId) => request('PUT', `/devices/${dId}/user/${uId}`),
  unassign:     (dId)      => request('DELETE', `/devices/${dId}/user`),

  // monitoring
  getConsumption: (dId, date) => request('GET', `/monitoring/devices/${dId}/consumption?date=${date}`),
};
