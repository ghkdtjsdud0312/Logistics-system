import { FailReason } from '@/types/shipment';

export const FAIL_REASON_LABEL: Record<FailReason, string> = {
  CUSTOMER_ABSENT: '고객 부재',
  ADDRESS_ERROR: '주소 오류',
  REFUSED: '수취 거부',
  DAMAGED: '상품 파손',
  OTHER: '기타',
};
