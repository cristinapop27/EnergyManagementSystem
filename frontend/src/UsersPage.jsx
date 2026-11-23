import { useEffect, useState } from 'react';
import { api } from './api';
import Alert from './Alert';
import { useNotice } from './notice';

const empty = { name: '', username: '', password: '', role: 'CLIENT' };

export default function UsersPage({ currentUserId }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [busy, setBusy] = useState(false);
  const { notice, showError, showSuccess, clear } = useNotice();

  const load = () =>
    api.getUsers()
      .then(setUsers)
      .catch(showError)
      .finally(() => setLoading(false));

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { load(); }, []);

  const resetForm = () => {
    setForm(empty);
    setEditingId(null);
  };

  const save = async (e) => {
    e.preventDefault();
    setBusy(true);
    clear();
    try {
      if (editingId) {
        // profile only; the password can't be changed here
        await api.updateUser(editingId, { name: form.name, username: form.username, role: form.role });
        showSuccess('Changes saved.');
      } else {
        // auth-service creates profile + credentials
        await api.createUserByAdmin(form);
        showSuccess(`User ${form.username} added.`);
      }
      resetForm();
      load();
    } catch (err) {
      showError(err);
    } finally {
      setBusy(false);
    }
  };

  const remove = async (u) => {
    if (!window.confirm(`Delete user ${u.username}? Their devices will be deleted too.`)) return;
    clear();
    try {
      await api.deleteUser(u.id);
      if (editingId === u.id) resetForm();
      showSuccess(`User ${u.username} deleted.`);
      load();
    } catch (err) {
      showError(err);
    }
  };

  const edit = (u) => {
    clear();
    setEditingId(u.id);
    setForm({ name: u.name, username: u.username, password: '', role: u.role });
  };

  return (
    <section>
      <div className="page-head">
        <h1>Users</h1>
        {!loading && <span className="count">{users.length} {users.length === 1 ? 'user' : 'users'}</span>}
      </div>

      <Alert notice={notice} onClose={clear} />

      <form className={`panel${editingId ? ' is-editing' : ''}`} onSubmit={save}>
        <h2>{editingId ? `Edit ${form.username || 'user'}` : 'Add a user'}</h2>
        <div className="fields">
          <label className="field">
            <span>Full name</span>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          </label>
          <label className="field">
            <span>Username</span>
            <input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
          </label>
          {!editingId && (
            <label className="field">
              <span>Password</span>
              <input type="password" value={form.password} autoComplete="new-password"
                     onChange={(e) => setForm({ ...form, password: e.target.value })} />
            </label>
          )}
          <label className="field field-narrow">
            <span>Role</span>
            <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              <option value="CLIENT">Client</option>
              <option value="ADMIN">Administrator</option>
            </select>
          </label>
        </div>
        <div className="actions">
          <button type="submit" className="btn btn-primary" disabled={busy}>
            {editingId ? 'Save changes' : 'Add user'}
          </button>
          {editingId && <button type="button" className="btn" onClick={resetForm}>Cancel</button>}
        </div>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Username</th><th>Role</th><th className="col-actions"><span className="sr-only">Actions</span></th></tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan="4" className="table-empty">Loading users…</td></tr>}
            {!loading && users.length === 0 && (
              <tr><td colSpan="4" className="table-empty">No users yet. Add the first one above.</td></tr>
            )}
            {users.map((u) => (
              <tr key={u.id} className={editingId === u.id ? 'is-editing' : undefined}>
                <td>{u.name}</td>
                <td>{u.username}{u.id === currentUserId && <span className="muted"> (you)</span>}</td>
                <td>
                  <span className={`role-tag role-${u.role.toLowerCase()}`}>
                    {u.role === 'ADMIN' ? 'Administrator' : 'Client'}
                  </span>
                </td>
                <td className="col-actions">
                  <button type="button" className="btn btn-small" onClick={() => edit(u)}>Edit</button>
                  <button type="button" className="btn btn-small btn-danger"
                          onClick={() => remove(u)}
                          disabled={u.id === currentUserId}
                          title={u.id === currentUserId ? "You can't delete your own account" : undefined}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
