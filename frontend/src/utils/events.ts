import { DashboardEvent } from '@/types/dashboard';

/** SSE로 받은 이벤트를 앞에 붙이고 중복을 제거해 최대 limit개만 남긴다. */
export function mergeEvents(
  live: DashboardEvent[],
  fetched: DashboardEvent[],
  limit = 10,
): DashboardEvent[] {
  const seen = new Set<string>();
  return [...live, ...fetched]
    .filter((e) => {
      const key = `${e.at}|${e.description}`;
      if (seen.has(key)) return false;
      seen.add(key);
      return true;
    })
    .slice(0, limit);
}
