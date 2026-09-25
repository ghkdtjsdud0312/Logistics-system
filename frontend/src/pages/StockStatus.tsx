import { useState } from 'react';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT } from '@/constants/nextStep';
import StockFilterBar from '@/components/stock/StockFilterBar';
import StockTable from '@/components/stock/StockTable';
import { useFetch } from '@/hooks/useFetch';
import { getStocks } from '@/services/stockService';
import { getWarehouseTree } from '@/services/warehouseService';
import { StockFilter } from '@/types/stock';

/** 재고 현황: 현재/예약/가용 재고 */
function StockStatusPage() {
  const [filter, setFilter] = useState<StockFilter>({});
  const tree = useFetch(getWarehouseTree);
  const stocks = useFetch(() => getStocks(filter), JSON.stringify(filter));

  return (
    <>
      <PageHeader
        title="재고 현황"
        next={PAGE_NEXT.STOCKS}
        description="창고 위치별 현재·예약·가용 재고를 확인합니다. 가용재고 = 현재재고 - 예약재고"
      />
      <StockFilterBar tree={tree.data ?? []} onSearch={setFilter} />
      <StockTable rows={stocks.data ?? []} loading={stocks.loading} />
    </>
  );
}

export default StockStatusPage;
