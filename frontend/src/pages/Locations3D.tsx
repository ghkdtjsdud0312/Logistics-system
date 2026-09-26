import { lazy, Suspense } from 'react';
import PageHeader from '@/components/common/PageHeader';
import { useFetch } from '@/hooks/useFetch';
import { getWarehouseTree } from '@/services/warehouseService';

// three.js는 용량이 커서 이 화면을 열 때만 불러온다.
const Warehouse3DView = lazy(() => import('@/components/warehouse3d/Warehouse3DView'));

/** 창고·위치관리 - 3D 보기: 3D 화면만 보여 준다. */
function Locations3DPage() {
  const { data } = useFetch(getWarehouseTree);

  return (
    <>
      <PageHeader title="창고 구조 3D 보기" description="창고를 3D로 확인합니다." />
      <Suspense fallback={<p className="text-sm text-gray-400">3D 창고를 불러오는 중...</p>}>
        <Warehouse3DView tree={data ?? []} />
      </Suspense>
    </>
  );
}

export default Locations3DPage;
