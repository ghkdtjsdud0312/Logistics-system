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
  zoneId: number;
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
  /** 이 칸의 재고 중 가장 최근 변경 시각 */
  updatedAt: string;
  products: { name: string; onHand: number; reserved: number; available: number }[];
}

/** 위치 코드 → 진행 중인 피킹 작업(있으면) */
export interface ActivePicking {
  taskNo: string;
  orderNo: string;
  productName: string;
}

export interface WarehouseSummary {
  totalCells: number;
  emptyCells: number;
  soldOutCells: number;
  reservedCells: number;
  availableCells: number;
}

export type CellState = 'EMPTY' | 'AVAILABLE' | 'RESERVED' | 'SOLD_OUT';

export interface HoverInfo {
  code: string;
  x: number;
  y: number;
}

/** 창고 하나를 나타내는 방. 실제 구역·위치가 이 방 안(x·z 중심)에 그대로 놓인다. */
export interface WorldRoom {
  warehouseId: number;
  number: number;
  name: string;
  x: number;
  z: number;
  /** 방 바닥(격자 배치용) 크기 */
  size: number;
  /** 이 창고의 실제 내용만 딱 맞게 볼 때 쓰는 카메라 범위 */
  closeup: SceneBounds;
}

/** 모든 창고를 한 장면에 배치한 결과: 방(개요)과 그 안의 실제 구역·위치 */
export interface WorldLayout {
  rooms: WorldRoom[];
  boxes: LocationBox[];
  slabs: ZoneSlab[];
}

/** 구역 하나의 재고 요약(가용·예약 등) */
export interface ZoneSummary {
  zoneCode: string;
  locationCount: number;
  onHand: number;
  reserved: number;
  available: number;
  soldOutCount: number;
  emptyCount: number;
}
