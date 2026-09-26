import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import DriverForm from '@/components/master/DriverForm';
import DriverTable from '@/components/master/DriverTable';
import { useFetch } from '@/hooks/useFetch';
import { getDrivers } from '@/services/driverService';

/** 기사 목록 */
function DriversPage() {
  const { data, loading, reload } = useFetch(getDrivers);

  return (
    <div className="flex h-full flex-col">
      <PageHeader title="기사 등록 및 목록" description="기사 기준정보를 관리합니다." />
      <FormCard title="기사 등록">
        <DriverForm onCreated={reload} />
      </FormCard>
      <DriverTable drivers={data ?? []} loading={loading} onChanged={reload} />
    </div>
  );
}

export default DriversPage;
