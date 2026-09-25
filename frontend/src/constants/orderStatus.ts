import { OrderStatus } from '@/types/order';
import { Tone } from '@/types/ui';

export const ORDER_STATUS_LABEL: Record<OrderStatus, string> = {
  RECEIVED: '주문접수',
  OUTBOUND_WAITING: '출고대기',
  PICKING: '피킹중',
  PICKED: '피킹완료',
  PACKED: '포장완료',
  LOADED: '상차완료',
  DISPATCHED: '배차완료',
  IN_DELIVERY: '배송중',
  DELIVERED: '배송완료',
  FAILED: '배송실패',
};

export const ORDER_STATUS_TONE: Record<OrderStatus, Tone> = {
  RECEIVED: 'gray',
  OUTBOUND_WAITING: 'gray',
  PICKING: 'yellow',
  PICKED: 'yellow',
  PACKED: 'blue',
  LOADED: 'blue',
  DISPATCHED: 'blue',
  IN_DELIVERY: 'green',
  DELIVERED: 'green',
  FAILED: 'red',
};
