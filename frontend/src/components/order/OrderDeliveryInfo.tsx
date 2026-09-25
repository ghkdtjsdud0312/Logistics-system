import { OrderDeliveryInfo as DeliveryInfo } from '@/types/order';
import { formatDateTime } from '@/utils/format';

/** 배송 정보: 차량, 기사, 배차/배송 시작 시각 */
function OrderDeliveryInfo({ delivery }: { delivery: DeliveryInfo | null }) {
  if (!delivery) {
    return <p className="text-sm text-gray-400">배차 전입니다.</p>;
  }
  return (
    <dl className="grid grid-cols-2 gap-2 rounded-md border border-gray-200 bg-white p-4 text-sm md:grid-cols-4">
      <div>
        <dt className="text-xs text-gray-400">차량</dt>
        <dd>{delivery.vehicleNumber}</dd>
      </div>
      <div>
        <dt className="text-xs text-gray-400">기사</dt>
        <dd>{delivery.driverName}</dd>
      </div>
      <div>
        <dt className="text-xs text-gray-400">배차시간</dt>
        <dd>{formatDateTime(delivery.plannedStartAt)}</dd>
      </div>
      <div>
        <dt className="text-xs text-gray-400">배송시작</dt>
        <dd>{formatDateTime(delivery.startedAt)}</dd>
      </div>
    </dl>
  );
}

export default OrderDeliveryInfo;
