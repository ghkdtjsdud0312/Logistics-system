export type StockStatus = 'AVAILABLE' | 'SOLD_OUT';

export interface StockRow {
  warehouseName: string;
  zoneCode: string;
  locationCode: string;
  productCode: string;
  productName: string;
  onHand: number;
  reserved: number;
  available: number;
}

export interface StockFilter {
  warehouseId?: string;
  zoneId?: string;
  keyword?: string;
  stockStatus?: string;
}
