import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import VehicleForm from '@/components/master/VehicleForm';
import VehicleTable from '@/components/master/VehicleTable';
import { useFetch } from '@/hooks/useFetch';
import { getVehicles } from '@/services/vehicleService';

/** 차량 목록 */
function VehiclesPage() {
  const { data, loading, reload } = useFetch(getVehicles);

  return (
    <div className="flex h-full flex-col">
      <PageHeader title="차량 등록 및 목록" description="차량 기준정보를 관리합니다." />
      <FormCard title="차량 등록">
        <VehicleForm onCreated={reload} />
      </FormCard>
      <VehicleTable vehicles={data ?? []} loading={loading} onChanged={reload} />
    </div>
  );
}

export default VehiclesPage;
