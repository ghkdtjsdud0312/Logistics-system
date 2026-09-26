import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Vehicle, VehicleCreate, VehicleStatus, VehicleUpdate } from '@/types/vehicle';

export const getVehicles = () => unwrap<Vehicle[]>(apiClient.get('/vehicles'));

export const createVehicle = (body: VehicleCreate) =>
  unwrap<Vehicle>(apiClient.post('/vehicles', body));

export const changeVehicleStatus = (id: number, status: VehicleStatus) =>
  unwrap<Vehicle>(apiClient.patch(`/vehicles/${id}/status`, { status }));

export const updateVehicle = (id: number, body: VehicleUpdate) =>
  unwrap<Vehicle>(apiClient.put(`/vehicles/${id}`, body));

export const deleteVehicle = (id: number) => unwrap<null>(apiClient.delete(`/vehicles/${id}`));
