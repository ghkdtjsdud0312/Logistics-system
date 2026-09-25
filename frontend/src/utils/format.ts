const pad = (n: number) => String(n).padStart(2, '0');

/** 'MM-DD HH:mm' 형식. 값이 없으면 '-' */
export function formatDateTime(value: string | null): string {
  if (!value) return '-';
  const d = new Date(value);
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
