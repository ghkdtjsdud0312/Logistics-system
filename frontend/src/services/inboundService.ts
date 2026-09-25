import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Inbound, InboundCreate } from '@/types/inbound';

export const getInbounds = (status?: string) =>
  unwrap<Inbound[]>(apiClient.get('/inbounds', { params: { status: status || undefined } }));

export const getInbound = (id: number) => unwrap<Inbound>(apiClient.get(`/inbounds/${id}`));

export const createInbound = (body: InboundCreate) =>
  unwrap<Inbound>(apiClient.post('/inbounds', body));

export const receiveInbound = (id: number) =>
  unwrap<Inbound>(apiClient.patch(`/inbounds/${id}/receive`));

export const putawayInbound = (id: number, locationId: number) =>
  unwrap<Inbound>(apiClient.patch(`/inbounds/${id}/putaway`, { locationId }));
