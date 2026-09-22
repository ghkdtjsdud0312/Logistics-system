/**
 * 출고 도메인 타입
 */
export type OutboundStatus = 'REQUESTED' | 'PICKING' | 'SHIPPED' | 'CANCELLED';

export interface OutboundItem {
  inboundId: number;
  quantity: number;
}

export interface Outbound {
  id: number;
  destination: string;
  status: OutboundStatus;
  items: OutboundItem[];
  totalQuantity: number;
  createdAt: string;
}

export interface OutboundCreateRequest {
  destination: string;
  items: OutboundItem[];
}

/** 출고 대상 선정 목록에 노출되는, 검수 완료된 가용 입고 건 */
export interface AvailableInbound {
  inboundId: number;
  itemName: string;
  warehouseLocation: string;
  inspectedQuantity: number;
  availableQuantity: number;
}
