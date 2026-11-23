import { useEffect, useState } from 'react';
import { api } from './api';

const pad = (n) => String(n).padStart(2, '0');

function today() {
  const d = new Date();
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

// hourly kWh for one device on one day, as a bar chart
export default function ConsumptionChart({ devices }) {
  const [deviceId, setDeviceId] = useState('');
  const [date, setDate] = useState(today());
  const [hours, setHours] = useState(null);
  const [error, setError] = useState(null);

  // fall back to the first device if none is picked yet or the picked one was deleted
  const selected = devices.some((d) => d.id === deviceId) ? deviceId : devices[0]?.id;

  useEffect(() => {
    if (!selected || !date) return;
    let cancelled = false;
    api.getConsumption(selected, date)
      .then((data) => { if (!cancelled) { setHours(data); setError(null); } })
      .catch((err) => { if (!cancelled) { setHours(null); setError(err.message); } });
    return () => { cancelled = true; };
  }, [selected, date]);

  if (!selected) return null;

  const max = hours ? Math.max(...hours.map((h) => h.kwh)) : 0;
  const total = hours ? hours.reduce((sum, h) => sum + h.kwh, 0) : 0;
  const peak = hours?.find((h) => h.kwh === max);

  return (
    <div className="panel chart-panel">
      <div className="chart-head">
        <h2>Hourly consumption</h2>
        <div className="chart-controls">
          <label className="field">
            <span>Device</span>
            <select value={selected} onChange={(e) => setDeviceId(e.target.value)}>
              {devices.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
            </select>
          </label>
          <label className="field">
            <span>Day</span>
            <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          </label>
        </div>
      </div>

      {error && <p className="chart-error">{error}</p>}

      {hours && (
        <>
          <p className="muted">
            {total > 0
              ? `${total.toFixed(2)} kWh in total, peak ${max.toFixed(2)} kWh at ${pad(peak.hour)}:00`
              : 'No readings for this day.'}
          </p>
          <div className="chart" role="img" aria-label={`Hourly consumption on ${date}`}>
            {hours.map((h) => (
              <div key={h.hour} className="chart-col" title={`${pad(h.hour)}:00 – ${h.kwh.toFixed(2)} kWh`}>
                <div className="chart-bar" style={{ height: max > 0 ? `${(h.kwh / max) * 100}%` : 0 }} />
              </div>
            ))}
          </div>
          <div className="chart-labels" aria-hidden="true">
            {hours.map((h) => <span key={h.hour}>{h.hour % 3 === 0 ? pad(h.hour) : ''}</span>)}
          </div>
          <p className="chart-axis muted">Hour of day · kWh per hour</p>
        </>
      )}
    </div>
  );
}
