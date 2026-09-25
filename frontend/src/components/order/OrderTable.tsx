import { Link } from 'react-router-dom';
import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import { ORDER_STATUS_LABEL, ORDER_STATUS_TONE } from '@/constants/orderStatus';
import { SHIPMENT_STATUS_LABEL } from '@/constants/shipmentStatus';
import { OrderListItem } from '@/types/order';
import { ShipmentStatus } from '@/types/shipment';
import { Column } from '@/types/ui';
import { formatDateTime } from '@/utils/format';

const COLUMNS: Column<OrderListItem>[] = [
  { header: '주문번호', render: (o) => o.orderNo },
  { header: '고객명', render: (o) => o.customerName },
  { header: '상품', render: (o) => o.productSummary },
  { header: '수량', render: (o) => o.quantity },
  { header: '주문일', render: (o) => formatDateTime(o.orderedAt) },
  {
    header: '현재상태',
    render: (o) => (
      <StatusBadge label={ORDER_STATUS_LABEL[o.status]} tone={ORDER_STATUS_TONE[o.status]} />
    ),
  },
  {
    header: '배송상태',
    render: (o) =>
      o.deliveryStatus ? SHIPMENT_STATUS_LABEL[o.deliveryStatus as ShipmentStatus] : '-',
  },
  {
    header: '관리',
    render: (o) => (
      <Link className="text-primary" to={`/orders/${o.id}`}>
        상세
      </Link>
    ),
  },
];

function OrderTable({ orders, loading }: { orders: OrderListItem[]; loading: boolean }) {
  return <DataTable columns={COLUMNS} rows={orders} rowKey={(o) => o.id} loading={loading} />;
}

export default OrderTable;
