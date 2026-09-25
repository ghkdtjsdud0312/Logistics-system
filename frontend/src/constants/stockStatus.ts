import { StockStatus } from '@/types/stock';
import { Tone } from '@/types/ui';

export const STOCK_STATUS_LABEL: Record<StockStatus, string> = {
  AVAILABLE: '가용',
  SOLD_OUT: '소진',
};

export const STOCK_STATUS_TONE: Record<StockStatus, Tone> = {
  AVAILABLE: 'green',
  SOLD_OUT: 'red',
};
