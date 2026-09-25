export type ShipmentStatus = 'LOADED' | 'DISPATCHED' | 'IN_DELIVERY' | 'DELIVERED' | 'FAILED';

export type FailReason = 'CUSTOMER_ABSENT' | 'ADDRESS_ERROR' | 'REFUSED' | 'DAMAGED' | 'OTHER';

export interface Shipment {
  id: number;
  orderId: number;
  orderNo: string;
  customerName: string;
  address: string;
  quantity: number;
  totalWeightKg: number;
  status: ShipmentStatus;
  dispatchId: number | null;
}
