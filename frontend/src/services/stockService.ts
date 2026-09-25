import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { StockFilter, StockRow } from '@/types/stock';

const clean = (v?: string) => v || undefined;

export const getStocks = (filter: StockFilter) =>
  unwrap<StockRow[]>(
    apiClient.get('/stocks', {
      params: {
        warehouseId: clean(filter.warehouseId),
        zoneId: clean(filter.zoneId),
        keyword: clean(filter.keyword),
        stockStatus: clean(filter.stockStatus),
      },
    }),
  );
