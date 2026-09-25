import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { OrderListItem } from '@/types/order';
import { Shipment } from '@/types/shipment';

export const getWaitingOrders = () =>
  unwrap<OrderListItem[]>(apiClient.get('/loadings/waiting-orders'));

export const loadOrders = (orderIds: number[]) =>
  unwrap<Shipment[]>(apiClient.post('/loadings', { orderIds }));

export const getShipments = (status?: string) =>
  unwrap<Shipment[]>(apiClient.get('/shipments', { params: { status } }));
