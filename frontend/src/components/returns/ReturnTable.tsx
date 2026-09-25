import { Link } from 'react-router-dom';
import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import ReturnActions from '@/components/returns/ReturnActions';
import { FAIL_REASON_LABEL } from '@/constants/failReason';
import { RETURN_STATUS_LABEL, RETURN_STATUS_TONE } from '@/constants/returnStatus';
import { ReturnOrder } from '@/types/return';
import { Column, Option } from '@/types/ui';

interface ReturnTableProps {
  returns: ReturnOrder[];
  locations: Option[];
  loading: boolean;
  onChanged: () => void;
}

/** 반품 목록 */
function ReturnTable({ returns, locations, loading, onChanged }: ReturnTableProps) {
  const columns: Column<ReturnOrder>[] = [
    { header: '반품번호', render: (r) => r.returnNo },
    { header: '주문번호', render: (r) => r.orderNo },
    { header: '고객', render: (r) => r.customerName },
    { header: '상품', render: (r) => r.items },
    { header: '사유', render: (r) => FAIL_REASON_LABEL[r.reason] },
    { header: '수량', align: 'right', render: (r) => r.quantity },
    {
      header: '상태',
      render: (r) => (
        <StatusBadge label={RETURN_STATUS_LABEL[r.status]} tone={RETURN_STATUS_TONE[r.status]} />
      ),
    },
    {
      header: '처리',
      render: (r) => <ReturnActions returnOrder={r} locations={locations} onChanged={onChanged} />,
    },
    {
      header: '상세',
      render: (r) => (
        <Link className="text-primary" to={`/returns/${r.id}`}>
          상세
        </Link>
      ),
    },
  ];
  return <DataTable columns={columns} rows={returns} rowKey={(r) => r.id} loading={loading} />;
}

export default ReturnTable;
