import { DISPATCH_STATUS_LABEL } from '@/constants/dispatchStatus';
import { INBOUND_STATUS_LABEL } from '@/constants/inboundStatus';
import { ORDER_STATUS_LABEL } from '@/constants/orderStatus';
import { RETURN_STATUS_LABEL } from '@/constants/returnStatus';
import { WORK_STATUS_LABEL } from '@/constants/workStatus';

const LABELS: Record<string, Record<string, string>> = {
  ORDER: ORDER_STATUS_LABEL,
  INBOUND: INBOUND_STATUS_LABEL,
  DISPATCH: DISPATCH_STATUS_LABEL,
  RETURN: RETURN_STATUS_LABEL,
  PICKING_TASK: WORK_STATUS_LABEL,
  PACKING_TASK: WORK_STATUS_LABEL,
};

/** 대상 유형에 맞는 한글 상태명. 모르는 값은 코드를 그대로 보여 준다. */
export function statusLabel(targetType: string, code: string | null): string {
  if (!code) return '-';
  return LABELS[targetType]?.[code] ?? code;
}
