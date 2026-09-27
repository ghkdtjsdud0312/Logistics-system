import { DispatchStatus } from '@/types/dispatch';
import { Tone } from '@/types/ui';

export const DISPATCH_STATUS_LABEL: Record<DispatchStatus, string> = {
  LOADING: '적재중',
  REGISTERED: '출발대기',
  IN_TRANSIT: '배송중',
  COMPLETED: '배송종료',
  CANCELLED: '취소',
};

export const DISPATCH_STATUS_TONE: Record<DispatchStatus, Tone> = {
  LOADING: 'yellow',
  REGISTERED: 'blue',
  IN_TRANSIT: 'green',
  COMPLETED: 'gray',
  CANCELLED: 'red',
};
