import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import { DRIVER_STATUS_LABEL, DRIVER_STATUS_TONE } from '@/constants/driverStatus';
import { Driver } from '@/types/driver';
import { Column } from '@/types/ui';

const COLUMNS: Column<Driver>[] = [
  { header: '기사ID', render: (d) => d.driverCode },
  { header: '이름', render: (d) => d.name },
  { header: '연락처', render: (d) => d.phone ?? '-' },
  {
    header: '상태',
    render: (d) => (
      <StatusBadge label={DRIVER_STATUS_LABEL[d.status]} tone={DRIVER_STATUS_TONE[d.status]} />
    ),
  },
];

function DriverTable({ drivers, loading }: { drivers: Driver[]; loading: boolean }) {
  return <DataTable columns={COLUMNS} rows={drivers} rowKey={(d) => d.id} loading={loading} />;
}

export default DriverTable;
