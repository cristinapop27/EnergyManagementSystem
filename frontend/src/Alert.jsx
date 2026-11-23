export default function Alert({ notice, onClose }) {
  if (!notice) return null;
  const { kind, title, details } = notice;

  return (
    <div className={`alert alert-${kind}`} role={kind === 'error' ? 'alert' : 'status'}>
      <div className="alert-body">
        <p className="alert-title">{title}</p>
        {details.length > 0 && (
          <ul className="alert-details">
            {details.map((d) => <li key={d}>{d}</li>)}
          </ul>
        )}
      </div>
      {onClose && (
        <button type="button" className="alert-close" onClick={onClose} aria-label="Dismiss message">
          ×
        </button>
      )}
    </div>
  );
}
