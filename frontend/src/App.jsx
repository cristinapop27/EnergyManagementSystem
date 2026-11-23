import { useState } from 'react';
import { getAuth, saveAuth, clearAuth } from './api';
import LoginPage from './LoginPage';
import UsersPage from './UsersPage';
import DevicesPage from './DevicesPage';
import MyDevicesPage from './MyDevicesPage';

function BrandMark() {
  return (
    <svg className="brand-mark" viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" strokeWidth="2" />
      <path d="M13 5 8 13h4l-1 6 5-8h-4z" fill="currentColor" />
    </svg>
  );
}

export default function App() {
  const [auth, setAuth] = useState(getAuth());   // null = not logged in
  const [tab, setTab] = useState('devices');

  const handleLogin = (newAuth) => {
    saveAuth(newAuth);
    setAuth(newAuth);
  };

  const logout = () => {
    clearAuth();
    setAuth(null);
  };

  if (!auth) {
    return <LoginPage onLogin={handleLogin} />;
  }

  const isAdmin = auth.role === 'ADMIN';

  return (
    <div className="app">
      <header className="topbar">
        <div className="topbar-inner">
          <div className="brand">
            <BrandMark />
            <span>Energy Management</span>
          </div>
          <div className="session">
            <span className="session-user">{auth.username}</span>
            <span className={`role-tag role-${auth.role.toLowerCase()}`}>
              {isAdmin ? 'Administrator' : 'Client'}
            </span>
            <button type="button" className="btn btn-quiet" onClick={logout}>Log out</button>
          </div>
        </div>
      </header>

      {isAdmin && (
        <nav className="tabs" aria-label="Sections">
          <div className="tabs-inner">
            <button type="button" className={`tab${tab === 'devices' ? ' is-active' : ''}`}
                    aria-current={tab === 'devices' ? 'page' : undefined}
                    onClick={() => setTab('devices')}>
              Devices
            </button>
            <button type="button" className={`tab${tab === 'users' ? ' is-active' : ''}`}
                    aria-current={tab === 'users' ? 'page' : undefined}
                    onClick={() => setTab('users')}>
              Users
            </button>
          </div>
        </nav>
      )}

      <main className="content">
        {isAdmin
          ? (tab === 'users' ? <UsersPage currentUserId={auth.userId} /> : <DevicesPage />)
          : <MyDevicesPage userId={auth.userId} />}
      </main>
    </div>
  );
}
