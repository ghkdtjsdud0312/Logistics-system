import ConfirmDeleteModal from '@/components/common/ConfirmDeleteModal';
import EditModal from '@/components/master/EditModal';
import WarehouseCards from '@/components/master/WarehouseCards';
import { NAME_EDIT_FIELDS } from '@/constants/editFields';
import { useRowActions } from '@/hooks/useRowActions';
import {
  deleteLocation,
  deleteWarehouse,
  deleteZone,
  renameWarehouse,
  renameZone,
} from '@/services/warehouseService';
import { EditTarget, WarehouseNode } from '@/types/warehouse';

const REMOVE = { warehouse: deleteWarehouse, zone: deleteZone, location: deleteLocation };
const KIND_LABEL = { warehouse: '창고', zone: '구역', location: '위치' };

/** 창고 카드 + 수정·삭제 모달 */
function WarehouseManager({ tree, onChanged }: { tree: WarehouseNode[]; onChanged: () => void }) {
  const a = useRowActions<EditTarget>((t) => REMOVE[t.kind](t.id), onChanged);
  const editing = a.editing;

  return (
    <>
      <WarehouseCards tree={tree} onEdit={a.setEditing} onDelete={a.setDeleting} />
      {editing && editing.kind !== 'location' && (
        <EditModal
          title={`${KIND_LABEL[editing.kind]} 수정`}
          fields={NAME_EDIT_FIELDS}
          initial={{ name: editing.name }}
          onSubmit={(v) =>
            editing.kind === 'warehouse'
              ? renameWarehouse(editing.id, v.name)
              : renameZone(editing.id, v.name)
          }
          onSaved={onChanged}
          onClose={() => a.setEditing(null)}
        />
      )}
      {a.deleting && (
        <ConfirmDeleteModal
          label={`${KIND_LABEL[a.deleting.kind]} ${a.deleting.name}`}
          busy={a.submitting}
          onConfirm={a.confirmDelete}
          onClose={() => a.setDeleting(null)}
        />
      )}
    </>
  );
}

export default WarehouseManager;
