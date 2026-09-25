import { ShipmentStatus } from '@/types/shipment';
import { Tone } from '@/types/ui';

export const SHIPMENT_STATUS_LABEL: Record<ShipmentStatus, string> = {
  LOADED: '배차대기',
  DISPATCHED: '배차완료',
  IN_DELIVERY: '배송중',
  DELIVERED: '배송완료',
  FAILED: '배송실패',
};

export const SHIPMENT_STATUS_TONE: Record<ShipmentStatus, Tone> = {
  LOADED: 'gray',
  DISPATCHED: 'blue',
  IN_DELIVERY: 'green',
  DELIVERED: 'green',
  FAILED: 'red',
};
