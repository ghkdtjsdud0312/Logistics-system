import { VehicleStatus } from '@/types/vehicle';
import { Tone } from '@/types/ui';

export const VEHICLE_STATUS_LABEL: Record<VehicleStatus, string> = {
  AVAILABLE: '운행가능',
  IN_OPERATION: '운행중',
  MAINTENANCE: '정비중',
  INACTIVE: '비활성',
};

export const VEHICLE_STATUS_TONE: Record<VehicleStatus, Tone> = {
  AVAILABLE: 'green',
  IN_OPERATION: 'blue',
  MAINTENANCE: 'yellow',
  INACTIVE: 'gray',
};
