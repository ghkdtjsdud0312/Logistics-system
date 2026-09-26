import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import DriverForm from '@/components/master/DriverForm';
import DriverTable from '@/components/master/DriverTable';
import VehicleForm from '@/components/master/VehicleForm';
import VehicleTable from '@/components/master/VehicleTable';
import { useFetch } from '@/hooks/useFetch';
import { getDrivers } from '@/services/driverService';
import { getVehicles } from '@/services/vehicleService';

/** 차량·기사관리 */
function FleetPage() {
  const vehicles = useFetch(getVehicles);
  const drivers = useFetch(getDrivers);

  return (
    <>
      <PageHeader title="차량·기사관리" description="차량과 기사 기준정보를 관리합니다." />
      <FormCard title="차량 등록">
        <VehicleForm onCreated={vehicles.reload} />
      </FormCard>
      <Section title="차량 목록">
        <VehicleTable vehicles={vehicles.data ?? []} loading={vehicles.loading} />
      </Section>
      <FormCard title="기사 등록">
        <DriverForm onCreated={drivers.reload} />
      </FormCard>
      <Section title="기사 목록">
        <DriverTable drivers={drivers.data ?? []} loading={drivers.loading} />
      </Section>
    </>
  );
}

export default FleetPage;
