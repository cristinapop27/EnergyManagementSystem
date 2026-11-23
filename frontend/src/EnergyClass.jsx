export const ENERGY_CLASSES = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];

export function EnergyClassBadge({ value }) {
  if (!value) return <span className="muted">Not set</span>;
  return <span className="energy-letter">{value}</span>;
}
