/**
 * 출고 도메인 타입
 */
export type OutboundStatus = 'REQUESTED' | 'PICKING' | 'SHIPPED' | 'CANCELLED';

export interface OutboundItem {
  inboundId: number;
  quantity: number;
  weightKg: number;
  volumeM3: number;
}

export interface Outbound {
  id: number;
  destination: string;
  latitude: number;
  longitude: number;
  status: OutboundStatus;
  items: OutboundItem[];
  totalQuantity: number;
  totalWeightKg: number;
  totalVolumeM3: number;
  createdAt: string;
}

export interface OutboundCreateRequest {
  destination: string;
  latitude: number;
  longitude: number;
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

/** 출고 등록 폼에서 입고건별로 입력 중인 값 (수량 0이면 미선택으로 취급) */
export interface SelectedOutboundLine {
  quantity: number;
  weightKg: number;
  volumeM3: number;
}
