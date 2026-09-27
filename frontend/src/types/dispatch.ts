import { ShipmentStatus } from '@/types/shipment';

export interface RouteOrigin {
  name: string;
  latitude: number | null;
  longitude: number | null;
}

export interface RouteStop {
  shipmentId: number;
  orderNo: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
}

export type DispatchStatus = 'LOADING' | 'REGISTERED' | 'IN_TRANSIT' | 'COMPLETED' | 'CANCELLED';

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
  capacityKg: number;
  warehouseId: number | null;
  origin: RouteOrigin | null;
  /** 방문 순서대로 */
  stops: RouteStop[];
  /** 출발지→방문 순서 직선거리(km). 좌표가 부족하면 null */
  totalDistanceKm: number | null;
  shipmentCount: number;
  shipmentIds: number[];
}

export interface DispatchRegister {
  vehicleId: number;
  driverId: number;
  warehouseId?: number;
  plannedStartAt: string;
  plannedArrivalAt: string;
  shipmentIds?: number[];
}

export interface RouteOptimizeResult {
  dispatch: Dispatch;
  unlocatedShipmentIds: number[];
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
