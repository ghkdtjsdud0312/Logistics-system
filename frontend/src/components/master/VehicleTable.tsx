import { actionsColumn } from '@/components/common/actionsColumn';
import ConfirmDeleteModal from '@/components/common/ConfirmDeleteModal';
import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import EditModal from '@/components/master/EditModal';
import { VEHICLE_EDIT_FIELDS } from '@/constants/editFields';
import { VEHICLE_STATUS_LABEL, VEHICLE_STATUS_TONE } from '@/constants/vehicleStatus';
import { useRowActions } from '@/hooks/useRowActions';
import { deleteVehicle, updateVehicle } from '@/services/vehicleService';
import { Column } from '@/types/ui';
import { Vehicle } from '@/types/vehicle';

const COLUMNS: Column<Vehicle>[] = [
  { header: '차량번호', render: (v) => v.vehicleNumber },
  { header: '차량종류', render: (v) => v.vehicleType },
  { header: '적재량', render: (v) => `${v.capacityKg}kg` },
  {
    header: '상태',
    render: (v) => (
      <StatusBadge label={VEHICLE_STATUS_LABEL[v.status]} tone={VEHICLE_STATUS_TONE[v.status]} />
    ),
  },
];

interface VehicleTableProps {
  vehicles: Vehicle[];
  loading: boolean;
  onChanged: () => void;
}

/** 차량 목록: 수정·삭제 지원 */
function VehicleTable({ vehicles, loading, onChanged }: VehicleTableProps) {
  const a = useRowActions<Vehicle>((v) => deleteVehicle(v.id), onChanged);
  const columns = [...COLUMNS, actionsColumn<Vehicle>(a.setEditing, a.setDeleting)];

  return (
    <>
      <DataTable fill columns={columns} rows={vehicles} rowKey={(v) => v.id} loading={loading} />
      {a.editing && (
        <EditModal
          title={`차량 수정 (${a.editing.vehicleNumber})`}
          fields={VEHICLE_EDIT_FIELDS}
          initial={{
            vehicleType: a.editing.vehicleType,
            capacityKg: String(a.editing.capacityKg),
          }}
          onSubmit={(v) =>
            updateVehicle(a.editing!.id, {
              vehicleType: v.vehicleType,
              capacityKg: Number(v.capacityKg),
            })
          }
          onSaved={onChanged}
          onClose={() => a.setEditing(null)}
        />
      )}
      {a.deleting && (
        <ConfirmDeleteModal
          label={a.deleting.vehicleNumber}
          busy={a.submitting}
          onConfirm={a.confirmDelete}
          onClose={() => a.setDeleting(null)}
        />
      )}
    </>
  );
}

export default VehicleTable;
