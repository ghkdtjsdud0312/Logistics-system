import { DISPATCH_STATUS_TONE } from '@/constants/dispatchStatus';
import { INBOUND_STATUS_TONE } from '@/constants/inboundStatus';
import { ROUTES } from '@/constants/routes';
import { Dispatch } from '@/types/dispatch';
import { Inbound } from '@/types/inbound';
import { ScheduleEvent } from '@/types/schedule';

const timeOf = (value: string | null) => (value ? value.slice(11, 16) : '');

/** 배차: 출발 예정일에 '출발~도착' 시각과 함께 표시한다. 취소된 배차는 제외한다. */
export function dispatchEvents(dispatches: Dispatch[]): ScheduleEvent[] {
  return dispatches
    .filter((d) => d.status !== 'CANCELLED' && d.plannedStartAt)
    .map((d) => ({
      id: `dispatch-${d.id}`,
      date: (d.plannedStartAt as string).slice(0, 10),
      time: d.plannedArrivalAt
        ? `${timeOf(d.plannedStartAt)}~${timeOf(d.plannedArrivalAt)}`
        : timeOf(d.plannedStartAt),
      title: `${d.dispatchNo} ${d.vehicleNumber}`,
      subtitle: `${d.driverName} · ${d.shipmentCount}건`,
      kind: 'DISPATCH' as const,
      tone: DISPATCH_STATUS_TONE[d.status],
      to: ROUTES.DISPATCH,
    }));
}

/** 입고: 입고일에 표시한다. */
export function inboundEvents(inbounds: Inbound[]): ScheduleEvent[] {
  return inbounds.map((i) => ({
    id: `inbound-${i.id}`,
    date: i.inboundDate,
    time: null,
    title: `${i.inboundNo} ${i.productName}`,
    subtitle: `${i.partnerName} · ${i.quantity}개`,
    kind: 'INBOUND' as const,
    tone: INBOUND_STATUS_TONE[i.status],
    to: `/warehouse/inbounds/${i.id}`,
  }));
}
