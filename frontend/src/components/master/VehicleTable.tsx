import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import { VEHICLE_STATUS_LABEL, VEHICLE_STATUS_TONE } from '@/constants/vehicleStatus';
import { Column } from '@/types/ui';
import { Vehicle } from '@/types/vehicle';

const COLUMNS: Column<Vehicle>[] = [
  { header: '차량번호', render: (v) => v.vehicleNumber },
  { header: '차량종류', render: (v) => v.vehicleType },
  { header: '적재량', align: 'right', render: (v) => `${v.capacityKg}kg` },
  {
    header: '상태',
    render: (v) => (
      <StatusBadge label={VEHICLE_STATUS_LABEL[v.status]} tone={VEHICLE_STATUS_TONE[v.status]} />
    ),
  },
];

function VehicleTable({ vehicles, loading }: { vehicles: Vehicle[]; loading: boolean }) {
  return <DataTable columns={COLUMNS} rows={vehicles} rowKey={(v) => v.id} loading={loading} />;
}

export default VehicleTable;
