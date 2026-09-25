import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import { Column } from '@/types/ui';
import { Product } from '@/types/product';

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

function ProductTable({ products, loading }: { products: Product[]; loading: boolean }) {
  return <DataTable columns={COLUMNS} rows={products} rowKey={(p) => p.id} loading={loading} />;
}

export default ProductTable;
