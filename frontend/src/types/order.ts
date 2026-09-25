export type OrderStatus =
  | 'RECEIVED'
  | 'OUTBOUND_WAITING'
  | 'PICKING'
  | 'PICKED'
  | 'PACKED'
  | 'LOADED'
  | 'DISPATCHED'
  | 'IN_DELIVERY'
  | 'DELIVERED'
  | 'FAILED';

export interface OrderListItem {
  id: number;
  orderNo: string;
  customerName: string;
  productSummary: string;
  quantity: number;
  orderedAt: string;
  status: OrderStatus;
  deliveryStatus: string | null;
}

export type OrderSearch = {
  orderNo: string;
  customerName: string;
  status: string;
  from: string;
  to: string;
};

export interface OrderLineInput {
  productId: string;
  quantity: string;
}

export interface OrderCreate {
  customerName: string;
  address: string;
  phone: string;
  items: { productId: number; quantity: number }[];
}

export interface OrderItemRow {
  productCode: string;
  productName: string;
  orderedQty: number;
  pickedQty: number;
  loadedQty: number;
  deliveredQty: number;
}

export interface TimelineStep {
  step: OrderStatus;
  done: boolean;
  at: string | null;
}

export interface OrderDeliveryInfo {
  vehicleNumber: string;
  driverName: string;
  plannedStartAt: string | null;
  startedAt: string | null;
}

export interface OrderEvent {
  at: string;
  description: string;
}

export interface OrderDetail {
  id: number;
  orderNo: string;
  status: OrderStatus;
  customerName: string;
  address: string;
  phone: string;
  orderedAt: string;
  items: OrderItemRow[];
  timeline: TimelineStep[];
  delivery: OrderDeliveryInfo | null;
  events: OrderEvent[];
}
