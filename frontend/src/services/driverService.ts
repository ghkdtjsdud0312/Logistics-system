import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Driver, DriverCreate, DriverStatus } from '@/types/driver';

export const getDrivers = () => unwrap<Driver[]>(apiClient.get('/drivers'));

export const createDriver = (body: DriverCreate) =>
  unwrap<Driver>(apiClient.post('/drivers', body));

export const changeDriverStatus = (id: number, status: DriverStatus) =>
  unwrap<Driver>(apiClient.patch(`/drivers/${id}/status`, { status }));
