/**
 * 기사 도메인 타입
 */
export type DriverStatus = 'AVAILABLE' | 'OFF';

export interface Driver {
  id: number;
  name: string;
  status: DriverStatus;
}

export interface DriverCreateRequest {
  name: string;
}
