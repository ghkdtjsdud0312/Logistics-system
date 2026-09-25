import { Tone } from '@/types/ui';

export type ScheduleKind = 'DISPATCH' | 'INBOUND';

/** 달력에 표시되는 일정 한 건 */
export interface ScheduleEvent {
  id: string;
  /** 'YYYY-MM-DD' */
  date: string;
  /** 'HH:mm' 또는 'HH:mm~HH:mm', 시각이 없으면 null */
  time: string | null;
  title: string;
  subtitle: string;
  kind: ScheduleKind;
  tone: Tone;
  to: string;
}

export interface CalendarDay {
  key: string;
  day: number;
  inMonth: boolean;
}
