import DataTable from '@/components/common/DataTable';
import { OrderItemRow } from '@/types/order';
import { Column } from '@/types/ui';

const COLUMNS: Column<OrderItemRow>[] = [
  { header: '상품코드', render: (i) => i.productCode },
  { header: '상품명', render: (i) => i.productName },
  { header: '주문수량', render: (i) => i.orderedQty },
  { header: '피킹수량', render: (i) => i.pickedQty },
  { header: '상차수량', render: (i) => i.loadedQty },
  { header: '배송수량', render: (i) => i.deliveredQty },
];

function OrderItemsTable({ items }: { items: OrderItemRow[] }) {
  return <DataTable columns={COLUMNS} rows={items} rowKey={(i) => i.productCode} />;
}

export default OrderItemsTable;
