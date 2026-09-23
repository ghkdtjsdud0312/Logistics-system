import apiClient from '@/config/axios';
import { ApiResponse } from '@/types/common';
import {
  Dispatch,
  DispatchCandidateRequest,
  DispatchCandidateResult,
  DispatchConfirmRequest,
  DispatchDetail,
  DispatchStatusChangeRequest,
  RouteOptimizeResult,
  RouteStop,
  RouteStopStatus,
} from '@/types/dispatch';

export const getDispatchList = () =>
  apiClient.get<ApiResponse<Dispatch[]>>('/dispatches').then((res) => res.data.data);

export const getDispatchDetail = (id: number) =>
  apiClient.get<ApiResponse<DispatchDetail>>(`/dispatches/${id}`).then((res) => res.data.data);

export const findDispatchCandidates = (request: DispatchCandidateRequest) =>
  apiClient
    .post<ApiResponse<DispatchCandidateResult>>('/dispatches/candidates', request)
    .then((res) => res.data.data);

export const confirmDispatch = (request: DispatchConfirmRequest) =>
  apiClient.post<ApiResponse<Dispatch>>('/dispatches', request).then((res) => res.data.data);

export const optimizeRoute = (id: number) =>
  apiClient
    .post<ApiResponse<RouteOptimizeResult>>(`/dispatches/${id}/route/optimize`)
    .then((res) => res.data.data);

export const changeDispatchStatus = (id: number, request: DispatchStatusChangeRequest) =>
  apiClient.patch<ApiResponse<Dispatch>>(`/dispatches/${id}/status`, request).then((res) => res.data.data);

export const changeStopStatus = (dispatchId: number, stopId: number, status: RouteStopStatus) =>
  apiClient
    .patch<ApiResponse<RouteStop>>(`/dispatches/${dispatchId}/stops/${stopId}`, { status })
    .then((res) => res.data.data);
