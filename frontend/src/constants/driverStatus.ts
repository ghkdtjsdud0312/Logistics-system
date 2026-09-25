import { DriverStatus } from '@/types/driver';
import { Tone } from '@/types/ui';

export const DRIVER_STATUS_LABEL: Record<DriverStatus, string> = {
  AVAILABLE: '운행가능',
  DELIVERING: '배송중',
  OFF: '휴무',
};

export const DRIVER_STATUS_TONE: Record<DriverStatus, Tone> = {
  AVAILABLE: 'green',
  DELIVERING: 'blue',
  OFF: 'gray',
};
