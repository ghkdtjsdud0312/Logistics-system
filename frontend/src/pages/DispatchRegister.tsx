import { useState } from 'react';
import CheckList from '@/components/common/CheckList';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT, PAGE_PREV } from '@/constants/nextStep';
import Section from '@/components/common/Section';
import DispatchForm from '@/components/shipping/DispatchForm';
import DispatchTable from '@/components/shipping/DispatchTable';
import LoadingDispatchCard from '@/components/shipping/LoadingDispatchCard';
import { useFetch } from '@/hooks/useFetch';
import { getDispatches } from '@/services/dispatchService';
import { getDrivers } from '@/services/driverService';
import { getShipments } from '@/services/loadingService';
import { getVehicles } from '@/services/vehicleService';
import { getWarehouseTree } from '@/services/warehouseService';
import { toggleId } from '@/utils/selection';
import { sumWeight } from '@/utils/weight';

/** 배차관리: 상차된 배송을 차량·기사에 배정 */
function DispatchRegisterPage() {
  const [selected, setSelected] = useState<number[]>([]);
  const shipments = useFetch(() => getShipments());
  const vehicles = useFetch(getVehicles);
  const drivers = useFetch(getDrivers);
  const warehouses = useFetch(getWarehouseTree);
  const dispatches = useFetch(getDispatches);
  const all = shipments.data ?? [];
  const list = all.filter((s) => s.status === 'LOADED');
  const loading = (dispatches.data ?? []).filter((d) => d.status === 'LOADING');
  const done = () => {
    setSelected([]);
    shipments.reload();
    dispatches.reload();
  };

  const changed = () => {
    done();
    vehicles.reload();
    drivers.reload();
  };

  return (
    <>
      <PageHeader
        title="배차관리"
        prev={PAGE_PREV.DISPATCH}
        next={PAGE_NEXT.DISPATCH}
        description="차량을 먼저 배정해 두고 상차된 물량이 모이면 마감해 출발시킵니다."
      />
      <Section title="배차 대기">
        <CheckList
          emptyText="배차 대기 중인 배송이 없습니다."
          selected={selected}
          onToggle={(id) => setSelected(toggleId(selected, id))}
          items={list.map((s) => ({
            id: s.id,
            title: s.orderNo,
            detail: `${s.address} · ${s.quantity}개 · ${s.totalWeightKg}kg`,
          }))}
        />
      </Section>
      <Section title="배차 생성">
        <DispatchForm
          vehicles={(vehicles.data ?? []).filter((v) => v.status === 'AVAILABLE')}
          drivers={(drivers.data ?? []).filter((d) => d.status === 'AVAILABLE')}
          warehouses={warehouses.data ?? []}
          shipmentIds={selected}
          totalWeightKg={sumWeight(list, selected)}
          onDone={done}
        />
      </Section>
      <Section title="적재 중 배차">
        {loading.length === 0 && (
          <p className="rounded-md border border-gray-200 bg-white p-4 text-sm text-gray-400">
            적재 중인 배차가 없습니다.
          </p>
        )}
        <div className="space-y-3">
          {loading.map((d) => (
            <LoadingDispatchCard
              key={d.id}
              dispatch={d}
              assigned={all.filter((s) => s.dispatchId === d.id)}
              waiting={list}
              warehouses={warehouses.data ?? []}
              selected={selected}
              onChanged={changed}
            />
          ))}
        </div>
      </Section>
      <Section title="배차 목록">
        <DispatchTable
          dispatches={(dispatches.data ?? []).filter((d) => d.status !== 'LOADING')}
          loading={dispatches.loading}
          onChanged={changed}
        />
      </Section>
    </>
  );
}

export default DispatchRegisterPage;
