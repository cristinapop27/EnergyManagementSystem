import { useEffect, useState } from 'react';

export function useNotice() {
  const [notice, setNotice] = useState(null); 

  useEffect(() => {
    if (notice?.kind !== 'success') return undefined;
    const timer = setTimeout(() => setNotice(null), 3500);
    return () => clearTimeout(timer);
  }, [notice]);

  return {
    notice,
    showError: (e) => setNotice({ kind: 'error', title: e.message, details: e.details ?? [] }),
    showSuccess: (title) => setNotice({ kind: 'success', title, details: [] }),
    clear: () => setNotice(null),
  };
}
