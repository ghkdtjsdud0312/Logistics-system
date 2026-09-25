import { WorkStatus } from '@/types/work';
import { Tone } from '@/types/ui';

export const WORK_STATUS_LABEL: Record<WorkStatus, string> = {
  WAITING: '대기',
  IN_PROGRESS: '진행중',
  COMPLETED: '완료',
};

export const WORK_STATUS_TONE: Record<WorkStatus, Tone> = {
  WAITING: 'gray',
  IN_PROGRESS: 'blue',
  COMPLETED: 'green',
};
