import apiClient from '@/config/axios';
import { ApiResponse } from '@/types/common';
import { Inbound, InboundCompleteRequest, InboundCreateRequest } from '@/types/inbound';

export const getInboundList = () =>
  apiClient.get<ApiResponse<Inbound[]>>('/inbounds').then((res) => res.data.data);

export const createInbound = (request: InboundCreateRequest) =>
  apiClient.post<ApiResponse<Inbound>>('/inbounds', request).then((res) => res.data.data);

export const startInbound = (id: number) =>
  apiClient.patch<ApiResponse<Inbound>>(`/inbounds/${id}/start`).then((res) => res.data.data);

export const completeInbound = (id: number, request: InboundCompleteRequest) =>
  apiClient
    .patch<ApiResponse<Inbound>>(`/inbounds/${id}/complete`, request)
    .then((res) => res.data.data);
