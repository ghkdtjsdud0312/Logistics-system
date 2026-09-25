import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { DashboardEvent, DashboardSummary } from '@/types/dashboard';
import { DeliveryBoard } from '@/types/dispatch';

export const getDashboardSummary = () =>
  unwrap<DashboardSummary>(apiClient.get('/dashboard/summary'));

export const getDashboardVehicles = () =>
  unwrap<DeliveryBoard[]>(apiClient.get('/dashboard/vehicles'));

export const getDashboardEvents = (limit = 10) =>
  unwrap<DashboardEvent[]>(apiClient.get('/dashboard/events', { params: { limit } }));
