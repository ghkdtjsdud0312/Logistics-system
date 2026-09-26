import { actionsColumn } from '@/components/common/actionsColumn';
import ConfirmDeleteModal from '@/components/common/ConfirmDeleteModal';
import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import EditModal from '@/components/master/EditModal';
import { PRODUCT_EDIT_FIELDS } from '@/constants/editFields';
import { useRowActions } from '@/hooks/useRowActions';
import { deleteProduct, updateProduct } from '@/services/productService';
import { Product } from '@/types/product';
import { Column } from '@/types/ui';

const COLUMNS: Column<Product>[] = [
  { header: '상품코드', render: (p) => p.code },
  { header: '상품명', render: (p) => p.name },
  { header: '단위', render: (p) => p.unit },
  { header: '단위 중량(kg)', render: (p) => p.unitWeightKg },
  {
    header: '상태',
    render: (p) => (
      <StatusBadge label={p.active ? '사용' : '미사용'} tone={p.active ? 'green' : 'gray'} />
    ),
  },
];

interface ProductTableProps {
  products: Product[];
  loading: boolean;
  onChanged: () => void;
}

/** 상품 목록: 표 안에서만 세로 스크롤, 수정·삭제 지원 */
function ProductTable({ products, loading, onChanged }: ProductTableProps) {
  const a = useRowActions<Product>((p) => deleteProduct(p.id), onChanged);
  const columns = [...COLUMNS, actionsColumn<Product>(a.setEditing, a.setDeleting)];

  return (
    <>
      <DataTable fill columns={columns} rows={products} rowKey={(p) => p.id} loading={loading} />
      {a.editing && (
        <EditModal
          title={`상품 수정 (${a.editing.code})`}
          fields={PRODUCT_EDIT_FIELDS}
          initial={{
            name: a.editing.name,
            unit: a.editing.unit,
            unitWeightKg: String(a.editing.unitWeightKg),
          }}
          onSubmit={(v) =>
            updateProduct(a.editing!.id, {
              name: v.name,
              unit: v.unit,
              unitWeightKg: Number(v.unitWeightKg),
            })
          }
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

export default ProductTable;
