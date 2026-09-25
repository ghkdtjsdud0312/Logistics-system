import ProgressBar from '@/components/common/ProgressBar';
import StatusBadge from '@/components/common/StatusBadge';
import { DISPATCH_STATUS_LABEL, DISPATCH_STATUS_TONE } from '@/constants/dispatchStatus';
import { SHIPMENT_STATUS_LABEL, SHIPMENT_STATUS_TONE } from '@/constants/shipmentStatus';
import { DeliveryBoard } from '@/types/dispatch';
import { formatDateTime } from '@/utils/format';

/** 차량 중심 배송현황 카드 */
function DeliveryCard({ board }: { board: DeliveryBoard }) {
  return (
    <div className="rounded-md border border-gray-200 bg-white p-4 text-sm">
      <div className="mb-1 flex items-center justify-between">
        <span className="font-semibold">🚚 {board.vehicleNumber}</span>
        <StatusBadge
          label={DISPATCH_STATUS_LABEL[board.status]}
          tone={DISPATCH_STATUS_TONE[board.status]}
        />
      </div>
      <p className="text-gray-500">
        기사 : {board.driverName} · 출발 : {formatDateTime(board.startedAt)}
      </p>
      <p className="my-2">
        배송 완료 {board.completed} / {board.total}
        {board.failed > 0 && ` (실패 ${board.failed})`}
      </p>
      <ProgressBar value={board.completed + board.failed} total={board.total} />
      <ul className="mt-3 space-y-1">
        {board.orders.map((o) => (
          <li key={o.shipmentId} className="flex justify-between">
            <span>
              {o.orderNo} {o.customerName}
            </span>
            <StatusBadge
              label={SHIPMENT_STATUS_LABEL[o.status]}
              tone={SHIPMENT_STATUS_TONE[o.status]}
            />
          </li>
        ))}
      </ul>
    </div>
  );
}

export default DeliveryCard;
