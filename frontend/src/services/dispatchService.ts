import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Dispatch, DispatchRegister } from '@/types/dispatch';

export const getDispatches = () => unwrap<Dispatch[]>(apiClient.get('/dispatches'));

export const registerDispatch = (body: DispatchRegister) =>
  unwrap<Dispatch>(apiClient.post('/dispatches', body));

export const startDispatch = (id: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/start`));

export const cancelDispatch = (id: number) =>
  unwrap<Dispatch>(apiClient.patch(`/dispatches/${id}/cancel`));
