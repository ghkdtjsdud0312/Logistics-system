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
