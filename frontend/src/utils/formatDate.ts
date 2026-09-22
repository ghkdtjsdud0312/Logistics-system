/**
 * 공통 유틸: 날짜 포맷 함수 (예시)
 */
export function formatDate(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toISOString().split('T')[0];
}
