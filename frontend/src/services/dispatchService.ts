import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Dispatch, DispatchRegister, RouteOptimizeResult } from '@/types/dispatch';

export const getDispatches = () => unwrap<Dispatch[]>(apiClient.get('/dispatches'));

export const registerDispatch = (body: DispatchRegister) =>
  unwrap<Dispatch>(apiClient.post('/dispatches', body));

export const startDispatch = (id: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/start`));

export const cancelDispatch = (id: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/cancel`));

export const addDispatchShipments = (id: number, shipmentIds: number[]) =>
  unwrap<Dispatch>(apiClient.post(`/dispatches/${id}/shipments`, { shipmentIds }));

export const removeDispatchShipment = (id: number, shipmentId: number) =>
  unwrap<Dispatch>(apiClient.delete(`/dispatches/${id}/shipments/${shipmentId}`));

export const closeDispatch = (id: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/close`));

export const reorderDispatchRoute = (id: number, shipmentIds: number[]) =>
  unwrap<Dispatch>(apiClient.put(`/dispatches/${id}/route`, { shipmentIds }));

export const optimizeDispatchRoute = (id: number) =>
  unwrap<RouteOptimizeResult>(apiClient.post(`/dispatches/${id}/route/optimize`));

export const changeDispatchWarehouse = (id: number, warehouseId: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/warehouse`, { warehouseId }));
