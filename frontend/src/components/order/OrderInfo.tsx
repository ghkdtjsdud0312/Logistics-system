import StatusBadge from '@/components/common/StatusBadge';
import OrderNextAction from '@/components/order/OrderNextAction';
import { ORDER_STATUS_LABEL, ORDER_STATUS_TONE } from '@/constants/orderStatus';
import { OrderDetail } from '@/types/order';
import { formatDateTime } from '@/utils/format';

const FIELDS = (order: OrderDetail): [string, string][] => [
  ['주문일시', formatDateTime(order.orderedAt)],
  ['고객명', order.customerName],
  ['배송주소', order.address],
  ['연락처', order.phone],
];

/** 주문 상세 상단: 주문번호 + 큰 상태 Badge + 다음 처리 버튼을 한 줄에, 고객 정보는 아래에 */
function OrderInfo({ order, onChanged }: { order: OrderDetail; onChanged: () => void }) {
  return (
    <div className="mb-4 rounded-md border border-gray-200 bg-white p-4 text-sm">
      <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="text-2xl font-bold text-gray-900">{order.orderNo}</span>
          <StatusBadge
            size="lg"
            label={ORDER_STATUS_LABEL[order.status]}
            tone={ORDER_STATUS_TONE[order.status]}
          />
        </div>
        <OrderNextAction orderId={order.id} status={order.status} onDone={onChanged} />
      </div>
      <dl className="grid grid-cols-2 gap-3 text-gray-700 md:grid-cols-4">
        {FIELDS(order).map(([label, value]) => (
          <div key={label}>
            <dt className="text-xs text-gray-400">{label}</dt>
            <dd>{value}</dd>
          </div>
        ))}
      </dl>
    </div>
  );
}

export default OrderInfo;
