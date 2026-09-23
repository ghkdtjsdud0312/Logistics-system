import { DispatchStatus } from '@/types/dispatch';

export const DISPATCH_STATUS_LABEL: Record<DispatchStatus, string> = {
  CONFIRMED: '확정됨',
  LOADED: '상차 완료',
  IN_TRANSIT: '운행중',
  COMPLETED: '배차 완료',
};
