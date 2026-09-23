import { useVehicleRegistry } from '@/hooks/useVehicleRegistry';
import { useDriverRegistry } from '@/hooks/useDriverRegistry';
import VehicleForm from '@/components/fleet/VehicleForm';
import VehicleList from '@/components/fleet/VehicleList';
import DriverForm from '@/components/fleet/DriverForm';
import DriverList from '@/components/fleet/DriverList';

/** 차량·기사 관리 페이지: 배차에 사용할 차량/기사를 등록하고 조회한다 */
function FleetPage() {
  const { vehicles, register: registerVehicle } = useVehicleRegistry();
  const { drivers, register: registerDriver } = useDriverRegistry();

  return (
    <div className="space-y-8">
      <h1 className="text-xl font-semibold text-gray-900">차량·기사 관리</h1>

      <section className="space-y-3">
        <h2 className="text-sm font-semibold text-gray-700">차량</h2>
        <VehicleForm onSubmit={registerVehicle} />
        <VehicleList vehicles={vehicles} />
      </section>

      <section className="space-y-3">
        <h2 className="text-sm font-semibold text-gray-700">기사</h2>
        <DriverForm onSubmit={registerDriver} />
        <DriverList drivers={drivers} />
      </section>
    </div>
  );
}

export default FleetPage;
