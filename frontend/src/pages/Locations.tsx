import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import CodeNameForm from '@/components/master/CodeNameForm';
import LocationForm from '@/components/master/LocationForm';
import WarehouseTree from '@/components/master/WarehouseTree';
import ZoneForm from '@/components/master/ZoneForm';
import { useFetch } from '@/hooks/useFetch';
import { createWarehouse, getWarehouseTree } from '@/services/warehouseService';

/** 창고·위치관리 */
function LocationsPage() {
  const { data, reload } = useFetch(getWarehouseTree);
  const tree = data ?? [];

  return (
    <>
      <PageHeader title="창고·위치관리" description="창고, 구역, 위치를 관리합니다." />
      <Section title="창고 구조">
        <WarehouseTree tree={tree} />
      </Section>
      <FormCard title="창고 등록">
        <CodeNameForm buttonLabel="창고등록" onSubmit={createWarehouse} onCreated={reload} />
      </FormCard>
      <FormCard title="구역 등록">
        <ZoneForm tree={tree} onCreated={reload} />
      </FormCard>
      <FormCard title="위치 등록">
        <LocationForm tree={tree} onCreated={reload} />
      </FormCard>
    </>
  );
}

export default LocationsPage;
