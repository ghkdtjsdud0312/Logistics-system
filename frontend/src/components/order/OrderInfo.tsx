import StatusBadge from '@/components/common/StatusBadge';
import { ORDER_STATUS_LABEL, ORDER_STATUS_TONE } from '@/constants/orderStatus';
import { OrderDetail } from '@/types/order';
import { formatDateTime } from '@/utils/format';

/** 주문 상세 상단: 주문번호, 상태, 고객 정보 */
function OrderInfo({ order }: { order: OrderDetail }) {
  return (
    <div className="mb-4 rounded-md border border-gray-200 bg-white p-4 text-sm">
      <div className="mb-2 flex items-center gap-3">
        <span className="text-lg font-semibold">{order.orderNo}</span>
        <StatusBadge
          label={ORDER_STATUS_LABEL[order.status]}
          tone={ORDER_STATUS_TONE[order.status]}
        />
      </div>
      <dl className="grid grid-cols-2 gap-2 text-gray-600 md:grid-cols-4">
        <div>
          <dt className="text-xs text-gray-400">주문일시</dt>
          <dd>{formatDateTime(order.orderedAt)}</dd>
        </div>
        <div>
          <dt className="text-xs text-gray-400">고객명</dt>
          <dd>{order.customerName}</dd>
        </div>
        <div>
          <dt className="text-xs text-gray-400">배송주소</dt>
          <dd>{order.address}</dd>
        </div>
        <div>
          <dt className="text-xs text-gray-400">연락처</dt>
          <dd>{order.phone}</dd>
        </div>
      </dl>
    </div>
  );
}

export default OrderInfo;
