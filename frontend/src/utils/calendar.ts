import { CalendarDay, ScheduleEvent } from '@/types/schedule';

const pad = (n: number) => String(n).padStart(2, '0');

/** 로컬 날짜를 'YYYY-MM-DD'로 만든다(UTC 변환으로 하루가 밀리지 않게 직접 조립). */
export const dateKey = (d: Date) =>
  `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;

/** month는 0~11. 일요일 시작 주 단위 달력 격자를 만든다(해당 월이 끝나면 남는 주는 생략). */
export function monthGrid(year: number, month: number): CalendarDay[][] {
  const first = new Date(year, month, 1);
  const weeks: CalendarDay[][] = [];
  for (let w = 0; w < 6; w += 1) {
    const week: CalendarDay[] = [];
    for (let d = 0; d < 7; d += 1) {
      const date = new Date(year, month, 1 - first.getDay() + w * 7 + d);
      week.push({ key: dateKey(date), day: date.getDate(), inMonth: date.getMonth() === month });
    }
    if (w > 0 && !week.some((day) => day.inMonth)) break;
    weeks.push(week);
  }
  return weeks;
}

export function addMonths(year: number, month: number, delta: number) {
  const moved = new Date(year, month + delta, 1);
  return { year: moved.getFullYear(), month: moved.getMonth() };
}

/** 날짜별로 묶고 같은 날에서는 시각, 제목 순으로 정렬한다. */
export function groupByDate(events: ScheduleEvent[]): Record<string, ScheduleEvent[]> {
  const grouped: Record<string, ScheduleEvent[]> = {};
  events.forEach((e) => (grouped[e.date] ??= []).push(e));
  Object.values(grouped).forEach((list) =>
    list.sort(
      (a, b) => (a.time ?? '99').localeCompare(b.time ?? '99') || a.title.localeCompare(b.title),
    ),
  );
  return grouped;
}
