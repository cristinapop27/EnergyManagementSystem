import { useState } from 'react';
import { api } from './api';
import Alert from './Alert';
import { useNotice } from './notice';

const empty = { name: '', username: '', password: '' };

export default function LoginPage({ onLogin }) {
  const [mode, setMode] = useState('login');          // 'login' or 'register'
  const [form, setForm] = useState(empty);
  const [busy, setBusy] = useState(false);
  const { notice, showError, showSuccess, clear } = useNotice();

  const isLogin = mode === 'login';

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    clear();
    try {
      if (!isLogin) {
        await api.register(form);                       // always creates a CLIENT
        setMode('login');
        setForm({ ...empty, username: form.username });
        showSuccess('Account created. Log in with your new password.');
        return;
      }
      const res = await api.login(form.username, form.password);
      onLogin({ token: res.token, userId: res.userId, role: res.role, username: form.username });
    } catch (err) {
      showError(err);
    } finally {
      setBusy(false);
    }
  };

  const switchMode = () => {
    setMode(isLogin ? 'register' : 'login');
    clear();
  };

  return (
    <div className="login-screen">
      <div className="login-panel">
        <div className="login-brand">
          <h1>Energy Management</h1>
          <p className="muted">Manage smart energy meters and see the devices assigned to you.</p>
        </div>

        <h2>{isLogin ? 'Log in' : 'Create an account'}</h2>
        <Alert notice={notice} onClose={clear} />

        <form className="stack" onSubmit={submit}>
          {!isLogin && (
            <label className="field">
              <span>Full name</span>
              <input value={form.name} autoComplete="name"
                     onChange={(e) => setForm({ ...form, name: e.target.value })} />
            </label>
          )}
          <label className="field">
            <span>Username</span>
            <input value={form.username} autoComplete="username"
                   onChange={(e) => setForm({ ...form, username: e.target.value })} />
          </label>
          <label className="field">
            <span>Password</span>
            <input type="password" value={form.password}
                   autoComplete={isLogin ? 'current-password' : 'new-password'}
                   onChange={(e) => setForm({ ...form, password: e.target.value })} />
            {!isLogin && <small className="hint">At least 4 characters.</small>}
          </label>

          <button type="submit" className="btn btn-primary btn-wide" disabled={busy}>
            {busy ? 'Please wait…' : isLogin ? 'Log in' : 'Create account'}
          </button>
        </form>

        <p className="login-switch">
          {isLogin ? 'New here?' : 'Already have an account?'}{' '}
          <button type="button" className="link" onClick={switchMode}>
            {isLogin ? 'Create an account' : 'Log in instead'}
          </button>
        </p>
      </div>
    </div>
  );
}
