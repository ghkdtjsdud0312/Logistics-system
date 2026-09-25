import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import DispatchActions from '@/components/shipping/DispatchActions';
import { DISPATCH_STATUS_LABEL, DISPATCH_STATUS_TONE } from '@/constants/dispatchStatus';
import { Dispatch } from '@/types/dispatch';
import { Column } from '@/types/ui';
import { formatDateTime } from '@/utils/format';

interface DispatchTableProps {
  dispatches: Dispatch[];
  loading: boolean;
  onChanged: () => void;
}

/** 배차 목록 */
function DispatchTable({ dispatches, loading, onChanged }: DispatchTableProps) {
  const columns: Column<Dispatch>[] = [
    { header: '배차번호', render: (d) => d.dispatchNo },
    { header: '차량', render: (d) => d.vehicleNumber },
    { header: '기사', render: (d) => d.driverName },
    { header: '출발예정', render: (d) => formatDateTime(d.plannedStartAt) },
    { header: '배송건수', align: 'right', render: (d) => d.shipmentCount },
    { header: '총 중량', align: 'right', render: (d) => `${d.totalWeightKg}kg` },
    {
      header: '상태',
      render: (d) => (
        <StatusBadge
          label={DISPATCH_STATUS_LABEL[d.status]}
          tone={DISPATCH_STATUS_TONE[d.status]}
        />
      ),
    },
    { header: '처리', render: (d) => <DispatchActions dispatch={d} onChanged={onChanged} /> },
  ];
  return <DataTable columns={columns} rows={dispatches} rowKey={(d) => d.id} loading={loading} />;
}

export default DispatchTable;
