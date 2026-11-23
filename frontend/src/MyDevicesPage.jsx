import { useEffect, useState } from 'react';
import { api } from './api';
import Alert from './Alert';
import { useNotice } from './notice';
import ConsumptionChart from './ConsumptionChart';
import { EnergyClassBadge } from './EnergyClass';

export default function MyDevicesPage({ userId }) {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const { notice, showError, clear } = useNotice();

  useEffect(() => {
    api.getMyDevices(userId)
      .then(setDevices)
      .catch(showError)
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const total = devices.reduce((sum, d) => sum + (Number(d.maxConsumption) || 0), 0);

  return (
    <section>
      <div className="page-head">
        <h1>My devices</h1>
        {!loading && devices.length > 0 && (
          <span className="count">
            {devices.length} {devices.length === 1 ? 'device' : 'devices'}, up to {total.toFixed(1)} kW together
          </span>
        )}
      </div>

      <Alert notice={notice} onClose={clear} />

      {loading && <p className="muted">Loading your devices…</p>}

      {!loading && !notice && devices.length === 0 && (
        <div className="panel empty">
          <h2>No devices assigned to you yet</h2>
          <p className="muted">When an administrator assigns a meter to your account, it will appear here.</p>
        </div>
      )}

      {devices.length > 0 && (
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Device</th><th className="num">Max consumption</th><th>Energy class</th></tr>
            </thead>
            <tbody>
              {devices.map((d) => (
                <tr key={d.id}>
                  <td>
                    <div className="cell-title">{d.name}</div>
                    {d.description && <div className="cell-sub">{d.description}</div>}
                  </td>
                  <td className="num">{Number(d.maxConsumption).toFixed(1)} kW</td>
                  <td><EnergyClassBadge value={d.energyClass} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {devices.length > 0 && <ConsumptionChart devices={devices} />}
    </section>
  );
}
