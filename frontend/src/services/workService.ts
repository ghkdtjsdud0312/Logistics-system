import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { PackingTask, PickingTask, WorkStatus } from '@/types/work';

export const getPickingTasks = (status?: WorkStatus) =>
  unwrap<PickingTask[]>(apiClient.get('/picking-tasks', { params: { status } }));

export const startPicking = (id: number) =>
  unwrap<unknown>(apiClient.patch(`/picking-tasks/${id}/start`));

export const completePicking = (id: number, pickedQty: number) =>
  unwrap<unknown>(apiClient.patch(`/picking-tasks/${id}/complete`, { pickedQty }));

export const getPackingTasks = () => unwrap<PackingTask[]>(apiClient.get('/packing-tasks'));

export const completePacking = (id: number, boxCode: string) =>
  unwrap<unknown>(apiClient.patch(`/packing-tasks/${id}/complete`, { boxCode }));
