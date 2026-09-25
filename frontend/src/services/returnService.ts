import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { ReturnOrder } from '@/types/return';

export const getReturns = () => unwrap<ReturnOrder[]>(apiClient.get('/returns'));

export const collectReturn = (id: number) =>
  unwrap<ReturnOrder>(apiClient.patch(`/returns/${id}/collect`));

export const collectedReturn = (id: number) =>
  unwrap<ReturnOrder>(apiClient.patch(`/returns/${id}/collected`));

export const receiveReturn = (id: number, locationId?: number) =>
  unwrap<ReturnOrder>(apiClient.patch(`/returns/${id}/receive`, { locationId }));

export const completeReturn = (id: number) =>
  unwrap<ReturnOrder>(apiClient.patch(`/returns/${id}/complete`));
