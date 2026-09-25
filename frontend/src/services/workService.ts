import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { PackingTask, PickingTask } from '@/types/work';

export const getPickingTasks = () => unwrap<PickingTask[]>(apiClient.get('/picking-tasks'));

export const startPicking = (id: number) =>
  unwrap<unknown>(apiClient.patch(`/picking-tasks/${id}/start`));

export const completePicking = (id: number, pickedQty: number) =>
  unwrap<unknown>(apiClient.patch(`/picking-tasks/${id}/complete`, { pickedQty }));

export const getPackingTasks = () => unwrap<PackingTask[]>(apiClient.get('/packing-tasks'));

export const completePacking = (id: number, boxCode: string) =>
  unwrap<unknown>(apiClient.patch(`/packing-tasks/${id}/complete`, { boxCode }));
