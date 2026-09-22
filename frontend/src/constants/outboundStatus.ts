import { OutboundStatus } from '@/types/outbound';

export const OUTBOUND_STATUS_LABEL: Record<OutboundStatus, string> = {
  REQUESTED: '출고 계획',
  PICKING: '피킹중',
  SHIPPED: '출고 완료',
  CANCELLED: '취소됨',
};
