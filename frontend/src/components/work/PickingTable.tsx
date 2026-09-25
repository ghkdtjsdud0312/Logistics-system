import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import PickingActions from '@/components/work/PickingActions';
import { WORK_STATUS_LABEL, WORK_STATUS_TONE } from '@/constants/workStatus';
import { Column } from '@/types/ui';
import { PickingTask } from '@/types/work';

interface PickingTableProps {
  tasks: PickingTask[];
  loading: boolean;
  onChanged: () => void;
}

/** 피킹 작업 목록 */
function PickingTable({ tasks, loading, onChanged }: PickingTableProps) {
  const columns: Column<PickingTask>[] = [
    { header: '작업번호', render: (t) => t.taskNo },
    { header: '주문번호', render: (t) => t.orderNo },
    { header: '위치', render: (t) => t.locationCode },
    { header: '상품', render: (t) => t.productName },
    { header: '요청수량', render: (t) => t.requestedQty },
    { header: '피킹수량', render: (t) => t.pickedQty },
    {
      header: '상태',
      render: (t) => (
        <StatusBadge label={WORK_STATUS_LABEL[t.status]} tone={WORK_STATUS_TONE[t.status]} />
      ),
    },
    { header: '작업', render: (t) => <PickingActions task={t} onChanged={onChanged} /> },
  ];
  return <DataTable columns={columns} rows={tasks} rowKey={(t) => t.id} loading={loading} />;
}

export default PickingTable;
