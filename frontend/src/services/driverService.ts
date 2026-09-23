import apiClient from '@/config/axios';
import { ApiResponse } from '@/types/common';
import { Driver, DriverCreateRequest } from '@/types/driver';

export const getDrivers = () =>
  apiClient.get<ApiResponse<Driver[]>>('/drivers').then((res) => res.data.data);

export const createDriver = (request: DriverCreateRequest) =>
  apiClient.post<ApiResponse<Driver>>('/drivers', request).then((res) => res.data.data);
