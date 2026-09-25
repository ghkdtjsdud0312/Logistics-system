import { ScheduleKind } from '@/types/schedule';

export const SCHEDULE_KIND_LABEL: Record<ScheduleKind, string> = {
  DISPATCH: '배차',
  INBOUND: '입고',
};

export const SCHEDULE_KIND_ICON: Record<ScheduleKind, string> = {
  DISPATCH: '🚚',
  INBOUND: '📥',
};

export const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
