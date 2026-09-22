/**
 * 입고 도메인 타입
 */
export type InboundStatus = 'REQUESTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface Inbound {
  id: number;
  itemName: string;
  quantity: number;
  warehouseLocation: string;
  status: InboundStatus;
  inspectedQuantity: number | null;
  createdAt: string;
}

export interface InboundCreateRequest {
  itemName: string;
  quantity: number;
  warehouseLocation: string;
}

export interface InboundCompleteRequest {
  inspectedQuantity: number;
}
