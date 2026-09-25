import { lazy, Suspense, useState } from 'react';
import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import Tabs from '@/components/common/Tabs';
import CodeNameForm from '@/components/master/CodeNameForm';
import LocationForm from '@/components/master/LocationForm';
import WarehouseTree from '@/components/master/WarehouseTree';
import ZoneForm from '@/components/master/ZoneForm';
import { useFetch } from '@/hooks/useFetch';
import { createWarehouse, getWarehouseTree } from '@/services/warehouseService';

// three.js는 용량이 커서 3D 탭을 열 때만 불러온다.
const Warehouse3DView = lazy(() => import('@/components/warehouse3d/Warehouse3DView'));

const TABS = [
  { value: 'tree', label: '구조(트리)' },
  { value: '3d', label: '3D 보기' },
];

/** 창고·위치관리: 트리와 3D 창고 */
function LocationsPage() {
  const [tab, setTab] = useState('tree');
  const { data, reload } = useFetch(getWarehouseTree);
  const tree = data ?? [];

  return (
    <>
      <PageHeader title="창고·위치관리" description="창고, 구역, 위치를 관리합니다." />
      <Tabs tabs={TABS} active={tab} onChange={setTab} />
      {tab === 'tree' ? (
        <Section title="창고 구조">
          <WarehouseTree tree={tree} />
        </Section>
      ) : (
        <Suspense fallback={<p className="text-sm text-gray-400">3D 창고를 불러오는 중...</p>}>
          <Warehouse3DView tree={tree} />
        </Suspense>
      )}
      <div className="mt-4">
        <FormCard title="창고 등록">
          <CodeNameForm buttonLabel="창고등록" onSubmit={createWarehouse} onCreated={reload} />
        </FormCard>
        <FormCard title="구역 등록">
          <ZoneForm tree={tree} onCreated={reload} />
        </FormCard>
        <FormCard title="위치 등록">
          <LocationForm tree={tree} onCreated={reload} />
        </FormCard>
      </div>
    </>
  );
}

export default LocationsPage;
