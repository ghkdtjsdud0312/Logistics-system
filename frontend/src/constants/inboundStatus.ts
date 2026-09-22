import { InboundStatus } from '@/types/inbound';

export const INBOUND_STATUS_LABEL: Record<InboundStatus, string> = {
  REQUESTED: '입고 요청',
  IN_PROGRESS: '입고 처리중',
  COMPLETED: '검수 완료',
  CANCELLED: '취소됨',
};
