import apiClient from '@/config/axios';
import { ApiResponse } from '@/types/common';
import { AvailableInbound, Outbound, OutboundCreateRequest } from '@/types/outbound';

export const getOutboundList = () =>
  apiClient.get<ApiResponse<Outbound[]>>('/outbounds').then((res) => res.data.data);

export const getAvailableInbounds = () =>
  apiClient
    .get<ApiResponse<AvailableInbound[]>>('/outbounds/available-inbounds')
    .then((res) => res.data.data);

export const createOutbound = (request: OutboundCreateRequest) =>
  apiClient.post<ApiResponse<Outbound>>('/outbounds', request).then((res) => res.data.data);

export const pickOutbound = (id: number) =>
  apiClient.patch<ApiResponse<Outbound>>(`/outbounds/${id}/pick`).then((res) => res.data.data);

export const shipOutbound = (id: number) =>
  apiClient.patch<ApiResponse<Outbound>>(`/outbounds/${id}/ship`).then((res) => res.data.data);
