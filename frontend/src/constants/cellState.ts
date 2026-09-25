import { CellState } from '@/types/warehouse3d';

export const CELL_STATE_LABEL: Record<CellState, string> = {
  EMPTY: '재고 없음',
  AVAILABLE: '가용 재고',
  RESERVED: '예약 있음',
  SOLD_OUT: '소진(가용 0)',
};

/** three.js 색상(0xRRGGBB) */
export const CELL_STATE_COLOR: Record<CellState, number> = {
  EMPTY: 0xcbd5e1,
  AVAILABLE: 0x22c55e,
  RESERVED: 0xf59e0b,
  SOLD_OUT: 0xef4444,
};

export const toCssColor = (hex: number) => `#${hex.toString(16).padStart(6, '0')}`;
