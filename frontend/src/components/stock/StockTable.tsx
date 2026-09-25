import DataTable from '@/components/common/DataTable';
import { StockRow } from '@/types/stock';
import { Column } from '@/types/ui';

const COLUMNS: Column<StockRow>[] = [
  { header: '창고', render: (s) => s.warehouseName },
  { header: '구역', render: (s) => s.zoneCode },
  { header: '위치', render: (s) => s.locationCode },
  { header: '상품코드', render: (s) => s.productCode },
  { header: '상품명', render: (s) => s.productName },
  { header: '현재재고', align: 'right', render: (s) => s.onHand },
  { header: '예약재고', align: 'right', render: (s) => s.reserved },
  { header: '가용재고', align: 'right', render: (s) => <b>{s.available}</b> },
];

function StockTable({ rows, loading }: { rows: StockRow[]; loading: boolean }) {
  return (
    <DataTable
      columns={COLUMNS}
      rows={rows}
      rowKey={(s) => `${s.locationCode}-${s.productCode}`}
      loading={loading}
    />
  );
}

export default StockTable;
