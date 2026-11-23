import { useEffect, useState } from 'react';
import { api } from './api';
import Alert from './Alert';
import { useNotice } from './notice';
import ConsumptionChart from './ConsumptionChart';
import { EnergyClassBadge, ENERGY_CLASSES } from './EnergyClass';

const empty = { name: '', description: '', maxConsumption: '', energyClass: 'A' };

export default function DevicesPage() {
  const [devices, setDevices] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);
  const [busy, setBusy] = useState(false);
  const { notice, showError, showSuccess, clear } = useNotice();

  const load = () =>
    Promise.all([api.getDevices(), api.getUsers()])
      .then(([d, u]) => { setDevices(d); setUsers(u); })
      .catch(showError)
      .finally(() => setLoading(false));

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { load(); }, []);

  // run an API call, show a confirmation, reload; or show the error
  const run = async (action, successMessage) => {
    clear();
    try {
      await action();
      if (successMessage) showSuccess(successMessage);
      load();
    } catch (err) {
      showError(err);
    }
  };

  const resetForm = () => {
    setForm(empty);
    setEditingId(null);
  };

  const save = async (e) => {
    e.preventDefault();
    setBusy(true);
    const body = {
      ...form,
      maxConsumption: form.maxConsumption === '' ? null : Number(form.maxConsumption),
    };
    await run(async () => {
      if (editingId) await api.updateDevice(editingId, body);
      else await api.createDevice(body);
      resetForm();
    }, editingId ? 'Changes saved.' : `Device ${form.name} added.`);
    setBusy(false);
  };

  const edit = (d) => {
    clear();
    setEditingId(d.id);
    setForm({ name: d.name, description: d.description ?? '',
              maxConsumption: d.maxConsumption, energyClass: d.energyClass ?? 'A' });
  };

  const remove = (d) => {
    if (!window.confirm(`Delete device ${d.name}?`)) return;
    if (editingId === d.id) resetForm();
    run(() => api.deleteDevice(d.id), `Device ${d.name} deleted.`);
  };

  const changeOwner = (d, userId) => {
    if (userId) {
      const name = users.find((u) => u.id === userId)?.username;
      run(() => api.assign(d.id, userId), `${d.name} assigned to ${name}.`);
    } else {
      run(() => api.unassign(d.id), `${d.name} is no longer assigned.`);
    }
  };

  const clients = users.filter((u) => u.role === 'CLIENT');

  return (
    <section>
      <div className="page-head">
        <h1>Devices</h1>
        {!loading && <span className="count">{devices.length} {devices.length === 1 ? 'device' : 'devices'}</span>}
      </div>

      <Alert notice={notice} onClose={clear} />

      <form className={`panel${editingId ? ' is-editing' : ''}`} onSubmit={save}>
        <h2>{editingId ? `Edit ${form.name || 'device'}` : 'Add a device'}</h2>
        <div className="fields">
          <label className="field">
            <span>Name</span>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          </label>
          <label className="field field-wide">
            <span>Description</span>
            <input value={form.description} placeholder="Where it is installed, model…"
                   onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </label>
          <label className="field field-narrow">
            <span>Max consumption (kW)</span>
            <input type="number" step="0.1" min="0" value={form.maxConsumption}
                   onChange={(e) => setForm({ ...form, maxConsumption: e.target.value })} />
          </label>
          <label className="field field-narrow">
            <span>Energy class</span>
            <select value={form.energyClass} onChange={(e) => setForm({ ...form, energyClass: e.target.value })}>
              {ENERGY_CLASSES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </label>
        </div>
        <div className="actions">
          <button type="submit" className="btn btn-primary" disabled={busy}>
            {editingId ? 'Save changes' : 'Add device'}
          </button>
          {editingId && <button type="button" className="btn" onClick={resetForm}>Cancel</button>}
        </div>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Device</th>
              <th className="num">Max consumption</th>
              <th>Energy class</th>
              <th>Assigned to</th>
              <th className="col-actions"><span className="sr-only">Actions</span></th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan="5" className="table-empty">Loading devices…</td></tr>}
            {!loading && devices.length === 0 && (
              <tr><td colSpan="5" className="table-empty">No devices yet. Add the first one above.</td></tr>
            )}
            {devices.map((d) => (
              <tr key={d.id} className={editingId === d.id ? 'is-editing' : undefined}>
                <td>
                  <div className="cell-title">{d.name}</div>
                  {d.description && <div className="cell-sub">{d.description}</div>}
                </td>
                <td className="num">{Number(d.maxConsumption).toFixed(1)} kW</td>
                <td><EnergyClassBadge value={d.energyClass} /></td>
                <td>
                  <select className="owner-select" aria-label={`Assign ${d.name}`}
                          value={d.userId ?? ''}
                          onChange={(e) => changeOwner(d, e.target.value)}>
                    <option value="">Not assigned</option>
                    {clients.map((u) => <option key={u.id} value={u.id}>{u.username}</option>)}
                  </select>
                </td>
                <td className="col-actions">
                  <button type="button" className="btn btn-small" onClick={() => edit(d)}>Edit</button>
                  <button type="button" className="btn btn-small btn-danger" onClick={() => remove(d)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {devices.length > 0 && <ConsumptionChart devices={devices} />}
    </section>
  );
}
