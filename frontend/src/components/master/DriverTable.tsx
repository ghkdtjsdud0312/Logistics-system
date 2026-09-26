import { actionsColumn } from '@/components/common/actionsColumn';
import ConfirmDeleteModal from '@/components/common/ConfirmDeleteModal';
import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import EditModal from '@/components/master/EditModal';
import { DRIVER_STATUS_LABEL, DRIVER_STATUS_TONE } from '@/constants/driverStatus';
import { DRIVER_EDIT_FIELDS } from '@/constants/editFields';
import { useRowActions } from '@/hooks/useRowActions';
import { deleteDriver, updateDriver } from '@/services/driverService';
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

interface DriverTableProps {
  drivers: Driver[];
  loading: boolean;
  onChanged: () => void;
}

/** 기사 목록: 수정·삭제 지원 */
function DriverTable({ drivers, loading, onChanged }: DriverTableProps) {
  const a = useRowActions<Driver>((d) => deleteDriver(d.id), onChanged);
  const columns = [...COLUMNS, actionsColumn<Driver>(a.setEditing, a.setDeleting)];

  return (
    <>
      <DataTable fill columns={columns} rows={drivers} rowKey={(d) => d.id} loading={loading} />
      {a.editing && (
        <EditModal
          title={`기사 수정 (${a.editing.driverCode})`}
          fields={DRIVER_EDIT_FIELDS}
          initial={{ name: a.editing.name, phone: a.editing.phone ?? '' }}
          onSubmit={(v) => updateDriver(a.editing!.id, { name: v.name, phone: v.phone })}
          onSaved={onChanged}
          onClose={() => a.setEditing(null)}
        />
      )}
      {a.deleting && (
        <ConfirmDeleteModal
          label={a.deleting.name}
          busy={a.submitting}
          onConfirm={a.confirmDelete}
          onClose={() => a.setDeleting(null)}
        />
      )}
    </>
  );
}

export default DriverTable;
