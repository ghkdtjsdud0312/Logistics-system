import { FailReason } from '@/types/shipment';

export type ReturnStatus =
  'REQUESTED' | 'COLLECTING' | 'COLLECTED' | 'RETURN_RECEIVED' | 'COMPLETED';

export interface ReturnOrder {
  id: number;
  returnNo: string;
  orderNo: string;
  customerName: string;
  items: string;
  reason: FailReason;
  quantity: number;
  status: ReturnStatus;
  locationId: number | null;
}
