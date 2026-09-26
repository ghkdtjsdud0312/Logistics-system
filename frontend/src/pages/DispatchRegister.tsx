import { useState } from 'react';
import CheckList from '@/components/common/CheckList';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT, PAGE_PREV } from '@/constants/nextStep';
import Section from '@/components/common/Section';
import DispatchForm from '@/components/shipping/DispatchForm';
import DispatchTable from '@/components/shipping/DispatchTable';
import { useFetch } from '@/hooks/useFetch';
import { getDispatches } from '@/services/dispatchService';
import { getDrivers } from '@/services/driverService';
import { getShipments } from '@/services/loadingService';
import { getVehicles } from '@/services/vehicleService';
import { toggleId } from '@/utils/selection';
import { sumWeight } from '@/utils/weight';

/** 배차관리: 상차된 배송을 차량·기사에 배정 */
function DispatchRegisterPage() {
  const [selected, setSelected] = useState<number[]>([]);
  const shipments = useFetch(() => getShipments('LOADED'));
  const vehicles = useFetch(getVehicles);
  const drivers = useFetch(getDrivers);
  const dispatches = useFetch(getDispatches);
  const list = shipments.data ?? [];
  const done = () => {
    setSelected([]);
    shipments.reload();
    dispatches.reload();
  };

  return (
    <>
      <PageHeader
        title="배차관리"
        prev={PAGE_PREV.DISPATCH}
        next={PAGE_NEXT.DISPATCH}
        description="상차가 끝난 물량을 차량·기사와 연결합니다."
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
      <Section title="배차 등록">
        <DispatchForm
          vehicles={(vehicles.data ?? []).filter((v) => v.status === 'AVAILABLE')}
          drivers={(drivers.data ?? []).filter((d) => d.status === 'AVAILABLE')}
          shipmentIds={selected}
          totalWeightKg={sumWeight(list, selected)}
          onDone={done}
        />
      </Section>
      <Section title="배차 목록">
        <DispatchTable
          dispatches={dispatches.data ?? []}
          loading={dispatches.loading}
          onChanged={() => {
            done();
            vehicles.reload();
            drivers.reload();
          }}
        />
      </Section>
    </>
  );
}

export default DispatchRegisterPage;
