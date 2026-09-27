const pad = (n: number) => String(n).padStart(2, '0');

/** 'MM-DD HH:mm' 형식. 값이 없으면 '-' */
export function formatDateTime(value: string | null): string {
  if (!value) return '-';
  const d = new Date(value);
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

/** '방금 전' / 'N분 전' / 'N시간 전' / 'N일 전' */
export function formatRelativeTime(value: string, now = Date.now()): string {
  const minutes = Math.max(0, Math.floor((now - new Date(value).getTime()) / 60000));
  if (minutes < 1) return '방금 전';
  if (minutes < 60) return `${minutes}분 전`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}시간 전`;
  return `${Math.floor(hours / 24)}일 전`;
}
