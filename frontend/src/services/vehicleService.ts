import apiClient from '@/config/axios';
import { ApiResponse } from '@/types/common';
import { Vehicle, VehicleCreateRequest } from '@/types/vehicle';

export const getVehicles = () =>
  apiClient.get<ApiResponse<Vehicle[]>>('/vehicles').then((res) => res.data.data);

export const createVehicle = (request: VehicleCreateRequest) =>
  apiClient.post<ApiResponse<Vehicle>>('/vehicles', request).then((res) => res.data.data);
