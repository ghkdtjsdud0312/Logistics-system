import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { CodeName, WarehouseNode } from '@/types/warehouse';

export const getWarehouseTree = () => unwrap<WarehouseNode[]>(apiClient.get('/warehouses/tree'));

export const createWarehouse = (body: CodeName) =>
  unwrap<number>(apiClient.post('/warehouses', body));

export const createZone = (warehouseId: number, body: CodeName) =>
  unwrap<number>(apiClient.post(`/warehouses/${warehouseId}/zones`, body));

export const createLocation = (zoneId: number, code: string) =>
  unwrap<number>(apiClient.post(`/zones/${zoneId}/locations`, { code }));

export const renameWarehouse = (id: number, name: string) =>
  unwrap<number>(apiClient.put(`/warehouses/${id}`, { name }));

export const deleteWarehouse = (id: number) => unwrap<null>(apiClient.delete(`/warehouses/${id}`));

export const renameZone = (id: number, name: string) =>
  unwrap<number>(apiClient.put(`/zones/${id}`, { name }));

export const deleteZone = (id: number) => unwrap<null>(apiClient.delete(`/zones/${id}`));

export const deleteLocation = (id: number) => unwrap<null>(apiClient.delete(`/locations/${id}`));
