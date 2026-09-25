/** 3D 장면에 놓이는 위치(선반 칸) 하나 */
export interface LocationBox {
  locationId: number;
  code: string;
  zoneCode: string;
  warehouseName: string;
  x: number;
  y: number;
  z: number;
}

/** 구역 바닥판 */
export interface ZoneSlab {
  label: string;
  x: number;
  z: number;
  width: number;
  depth: number;
}

export interface WarehouseLayout {
  boxes: LocationBox[];
  slabs: ZoneSlab[];
}

export interface SceneBounds {
  center: { x: number; y: number; z: number };
  size: number;
}

/** 위치별 재고 합계와 상품별 내역 */
export interface StockCell {
  onHand: number;
  reserved: number;
  available: number;
  products: { name: string; onHand: number; reserved: number; available: number }[];
}

export type CellState = 'EMPTY' | 'AVAILABLE' | 'RESERVED' | 'SOLD_OUT';

export interface HoverInfo {
  code: string;
  x: number;
  y: number;
}
