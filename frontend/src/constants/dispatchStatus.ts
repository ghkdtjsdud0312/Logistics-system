import { DispatchStatus } from '@/types/dispatch';
import { Tone } from '@/types/ui';

export const DISPATCH_STATUS_LABEL: Record<DispatchStatus, string> = {
  REGISTERED: '배차완료',
  IN_TRANSIT: '배송중',
  COMPLETED: '배송종료',
  CANCELLED: '취소',
};

export const DISPATCH_STATUS_TONE: Record<DispatchStatus, Tone> = {
  REGISTERED: 'blue',
  IN_TRANSIT: 'green',
  COMPLETED: 'gray',
  CANCELLED: 'red',
};
