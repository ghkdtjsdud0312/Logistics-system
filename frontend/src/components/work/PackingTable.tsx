import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import PackingActions from '@/components/work/PackingActions';
import { WORK_STATUS_LABEL, WORK_STATUS_TONE } from '@/constants/workStatus';
import { Column } from '@/types/ui';
import { PackingTask } from '@/types/work';

interface PackingTableProps {
  tasks: PackingTask[];
  loading: boolean;
  onChanged: () => void;
}

/** 포장 작업 목록 */
function PackingTable({ tasks, loading, onChanged }: PackingTableProps) {
  const columns: Column<PackingTask>[] = [
    { header: '포장번호', render: (t) => t.taskNo },
    { header: '주문번호', render: (t) => t.orderNo },
    { header: '상품', render: (t) => t.items },
    {
      header: '상태',
      render: (t) => (
        <StatusBadge label={WORK_STATUS_LABEL[t.status]} tone={WORK_STATUS_TONE[t.status]} />
      ),
    },
    { header: '박스/작업', render: (t) => <PackingActions task={t} onChanged={onChanged} /> },
  ];
  return <DataTable columns={columns} rows={tasks} rowKey={(t) => t.id} loading={loading} />;
}

export default PackingTable;
