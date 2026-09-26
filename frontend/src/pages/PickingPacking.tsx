import { useState } from 'react';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT, PAGE_PREV } from '@/constants/nextStep';
import Tabs from '@/components/common/Tabs';
import PackingTable from '@/components/work/PackingTable';
import PickingTable from '@/components/work/PickingTable';
import { useFetch } from '@/hooks/useFetch';
import { getPackingTasks, getPickingTasks } from '@/services/workService';

const TABS = [
  { value: 'picking', label: '피킹 작업' },
  { value: 'packing', label: '포장 작업' },
];

/** 피킹·포장 */
function PickingPackingPage() {
  const [tab, setTab] = useState('picking');
  const picking = useFetch(getPickingTasks);
  const packing = useFetch(getPackingTasks);
  const reloadAll = () => {
    picking.reload();
    packing.reload();
  };

  return (
    <>
      <PageHeader
        title="피킹·포장"
        prev={PAGE_PREV.WAREHOUSE_WORK}
        next={PAGE_NEXT.WAREHOUSE_WORK}
        description="출고 지시된 주문의 피킹 작업과 포장 작업을 처리합니다."
      />
      <Tabs tabs={TABS} active={tab} onChange={setTab} />
      {tab === 'picking' ? (
        <PickingTable tasks={picking.data ?? []} loading={picking.loading} onChanged={reloadAll} />
      ) : (
        <PackingTable tasks={packing.data ?? []} loading={packing.loading} onChanged={reloadAll} />
      )}
    </>
  );
}

export default PickingPackingPage;
