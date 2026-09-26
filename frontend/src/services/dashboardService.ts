import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { DashboardEvent, DashboardSummary } from '@/types/dashboard';
import { DeliveryBoard } from '@/types/dispatch';
import { OrderListItem } from '@/types/order';

/** date(YYYY-MM-DD)를 생략하면 오늘 현황이다. */
export const getDashboardSummary = (date?: string) =>
  unwrap<DashboardSummary>(
    apiClient.get('/dashboard/summary', { params: { date: date || undefined } }),
  );

export const getDashboardVehicles = () =>
  unwrap<DeliveryBoard[]>(apiClient.get('/dashboard/vehicles'));

export const getDashboardEvents = (limit = 10, date?: string) =>
  unwrap<DashboardEvent[]>(
    apiClient.get('/dashboard/events', { params: { limit, date: date || undefined } }),
  );

/** 진행 현황 단계(ORDERS·PICKING·PACKING·LOADING·DELIVERY)에 속한 주문 목록 */
export const getProgressOrders = (stage: string, date?: string) =>
  unwrap<OrderListItem[]>(
    apiClient.get('/dashboard/progress/orders', { params: { stage, date: date || undefined } }),
  );
