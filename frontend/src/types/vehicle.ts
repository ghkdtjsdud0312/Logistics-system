export type VehicleStatus = 'AVAILABLE' | 'IN_OPERATION' | 'MAINTENANCE' | 'INACTIVE';

export interface Vehicle {
  id: number;
  vehicleNumber: string;
  vehicleType: string;
  capacityKg: number;
  status: VehicleStatus;
}

export interface VehicleCreate {
  vehicleNumber: string;
  vehicleType: string;
  capacityKg: number;
}

export type VehicleUpdate = Omit<VehicleCreate, 'vehicleNumber'>;
