import { DispatchStatus, RouteStopStatus } from '@/types/dispatch';

const DISPATCH_ORDER: DispatchStatus[] = ['CONFIRMED', 'LOADED', 'IN_TRANSIT', 'COMPLETED'];
const STOP_ORDER: RouteStopStatus[] = ['PENDING', 'ARRIVED', 'DELIVERED'];

/** 다음 배차 상태. 마지막 단계(COMPLETED)면 null */
export function nextDispatchStatus(current: DispatchStatus): DispatchStatus | null {
  const index = DISPATCH_ORDER.indexOf(current);
  return index === DISPATCH_ORDER.length - 1 ? null : DISPATCH_ORDER[index + 1];
}

/** 다음 경유지 상태. 마지막 단계(DELIVERED)면 null */
export function nextStopStatus(current: RouteStopStatus): RouteStopStatus | null {
  const index = STOP_ORDER.indexOf(current);
  return index === STOP_ORDER.length - 1 ? null : STOP_ORDER[index + 1];
}
