import { toast } from 'sonner';
import ProgressBar from '@/components/common/ProgressBar';
import StatusBadge from '@/components/common/StatusBadge';
import DispatchOriginPicker from '@/components/shipping/DispatchOriginPicker';
import LoadingDispatchActions from '@/components/shipping/LoadingDispatchActions';
import RouteStopList from '@/components/shipping/RouteStopList';
import { DISPATCH_STATUS_LABEL, DISPATCH_STATUS_TONE } from '@/constants/dispatchStatus';
import { useSubmit } from '@/hooks/useSubmit';
import {
  optimizeDispatchRoute,
  removeDispatchShipment,
  reorderDispatchRoute,
} from '@/services/dispatchService';
import { Dispatch } from '@/types/dispatch';
import { Shipment } from '@/types/shipment';
import { WarehouseNode } from '@/types/warehouse';
import { moveItem } from '@/utils/route';
import { loadPercent, sumWeight } from '@/utils/weight';

interface LoadingDispatchCardProps {
  dispatch: Dispatch;
  assigned: Shipment[];
  waiting: Shipment[];
  selected: number[];
  warehouses: WarehouseNode[];
  onChanged: () => void;
}

/** 적재중 배차: 적재율을 보면서 화물을 담고 빼고, 마감하거나 취소한다. */
function LoadingDispatchCard({
  dispatch,
  assigned,
  waiting,
  selected,
  warehouses,
  onChanged,
}: LoadingDispatchCardProps) {
  const { submitting, run } = useSubmit();
  const percent = loadPercent(dispatch.totalWeightKg, dispatch.capacityKg);
  const pending = sumWeight(waiting, selected);
  const overAfterAdd = dispatch.totalWeightKg + pending > dispatch.capacityKg;

  const handle = async (action: () => Promise<unknown>, message: string) => {
    if (await run(action, message)) onChanged();
  };

  const optimize = async () => {
    let lost = 0;
    const ok = await run(async () => {
      lost = (await optimizeDispatchRoute(dispatch.id)).unlocatedShipmentIds.length;
    }, '경로를 최적화했습니다.');
    if (!ok) return;
    if (lost > 0) toast.warning(`좌표를 찾지 못한 ${lost}건은 순서 맨 뒤에 두었습니다.`);
    onChanged();
  };

  return (
    <div className="space-y-2 rounded-md border border-gray-200 bg-white p-3 text-sm">
      <div className="flex flex-wrap items-center gap-3">
        <span className="font-semibold">{dispatch.dispatchNo}</span>
        <span>{dispatch.vehicleNumber}</span>
        <span className="text-gray-500">{dispatch.driverName}</span>
        <StatusBadge
          label={DISPATCH_STATUS_LABEL[dispatch.status]}
          tone={DISPATCH_STATUS_TONE[dispatch.status]}
        />
        <span className="ml-auto text-gray-600">
          적재율 {percent}% ({dispatch.totalWeightKg}/{dispatch.capacityKg}kg)
          {dispatch.totalDistanceKm !== null && ` · 총 ${dispatch.totalDistanceKm}km(직선거리)`}
        </span>
      </div>
      <ProgressBar value={dispatch.totalWeightKg} total={dispatch.capacityKg} />
      <DispatchOriginPicker
        dispatch={dispatch}
        warehouses={warehouses}
        submitting={submitting}
        onChanged={handle}
      />
      <RouteStopList
        stops={assigned}
        disabled={submitting}
        onMove={(index, offset) =>
          handle(
            () =>
              reorderDispatchRoute(
                dispatch.id,
                moveItem(
                  assigned.map((s) => s.id),
                  index,
                  offset,
                ),
              ),
            '방문 순서를 바꿨습니다.',
          )
        }
        onRemove={(shipmentId) =>
          handle(() => removeDispatchShipment(dispatch.id, shipmentId), '배송을 제외했습니다.')
        }
      />
      <LoadingDispatchActions
        dispatchId={dispatch.id}
        assignedCount={assigned.length}
        hasOrigin={dispatch.origin !== null}
        selected={selected}
        overAfterAdd={overAfterAdd}
        submitting={submitting}
        handle={handle}
        onOptimize={optimize}
      />
    </div>
  );
}

export default LoadingDispatchCard;
