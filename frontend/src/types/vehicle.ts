/**
 * 차량 도메인 타입
 */
export type VehicleStatus = 'AVAILABLE' | 'MAINTENANCE' | 'INACTIVE';

export interface Vehicle {
  id: number;
  vehicleNumber: string;
  vehicleType: string;
  maxWeightKg: number;
  maxVolumeM3: number;
  status: VehicleStatus;
}

export interface VehicleCreateRequest {
  vehicleNumber: string;
  vehicleType: string;
  maxWeightKg: number;
  maxVolumeM3: number;
  hubDistanceKm: number;
}
