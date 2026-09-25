import { ShipmentStatus } from '@/types/shipment';

export type DispatchStatus = 'REGISTERED' | 'IN_TRANSIT' | 'COMPLETED' | 'CANCELLED';

export interface Dispatch {
  id: number;
  dispatchNo: string;
  status: DispatchStatus;
  vehicleNumber: string;
  driverName: string;
  plannedStartAt: string | null;
  plannedArrivalAt: string | null;
  startedAt: string | null;
  totalWeightKg: number;
  shipmentCount: number;
  shipmentIds: number[];
}

export interface DispatchRegister {
  vehicleId: number;
  driverId: number;
  plannedStartAt: string;
  plannedArrivalAt: string;
  shipmentIds: number[];
}

export interface BoardOrder {
  shipmentId: number;
  orderNo: string;
  customerName: string;
  status: ShipmentStatus;
}

export interface DeliveryBoard {
  dispatchId: number;
  dispatchNo: string;
  vehicleNumber: string;
  driverName: string;
  status: DispatchStatus;
  completed: number;
  failed: number;
  total: number;
  startedAt: string | null;
  orders: BoardOrder[];
}
