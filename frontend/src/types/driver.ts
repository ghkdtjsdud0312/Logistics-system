export type DriverStatus = 'AVAILABLE' | 'DELIVERING' | 'OFF';

export interface Driver {
  id: number;
  driverCode: string;
  name: string;
  phone: string | null;
  status: DriverStatus;
}

export interface DriverCreate {
  driverCode: string;
  name: string;
  phone: string;
}
