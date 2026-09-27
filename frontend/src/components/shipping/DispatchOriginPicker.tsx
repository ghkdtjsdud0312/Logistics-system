import { lazy, Suspense, useState } from 'react';
import SelectField from '@/components/common/SelectField';
import { changeDispatchWarehouse } from '@/services/dispatchService';
import { Dispatch } from '@/types/dispatch';
import { WarehouseNode } from '@/types/warehouse';

// leaflet은 용량이 커서 지도를 열 때만 불러온다.
const DispatchRouteMap = lazy(() => import('@/components/shipping/DispatchRouteMap'));

interface DispatchOriginPickerProps {
  dispatch: Dispatch;
  warehouses: WarehouseNode[];
  submitting: boolean;
  onChanged: (action: () => Promise<unknown>, message: string) => Promise<void>;
}

/** 출발지 선택과, 선택해서 여는 경로 지도(직선거리 기준) */
function DispatchOriginPicker({
  dispatch,
  warehouses,
  submitting,
  onChanged,
}: DispatchOriginPickerProps) {
  const [showMap, setShowMap] = useState(false);

  return (
    <div className="space-y-2">
      <div className="flex flex-wrap items-center gap-2">
        <SelectField
          label="출발지"
          value={dispatch.warehouseId ? String(dispatch.warehouseId) : ''}
          onChange={(v) =>
            v &&
            onChanged(
              () => changeDispatchWarehouse(dispatch.id, Number(v)),
              '출발지를 지정했습니다.',
            )
          }
          options={warehouses.map((w) => ({ value: String(w.id), label: w.name }))}
          placeholder="출발지 선택"
        />
        {!dispatch.origin && (
          <span className="text-xs text-gray-400">
            출발지를 선택하면 경로를 최적화할 수 있습니다.
          </span>
        )}
        <button
          type="button"
          className="ml-auto text-xs text-primary underline"
          disabled={submitting}
          onClick={() => setShowMap((v) => !v)}
        >
          {showMap ? '지도 닫기' : '지도 보기'}
        </button>
      </div>
      {showMap && (
        <Suspense fallback={<p className="text-xs text-gray-400">지도를 불러오는 중...</p>}>
          <DispatchRouteMap origin={dispatch.origin} stops={dispatch.stops} />
        </Suspense>
      )}
    </div>
  );
}

export default DispatchOriginPicker;
