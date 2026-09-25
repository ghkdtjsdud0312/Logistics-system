import DataTable from '@/components/common/DataTable';
import StatusBadge from '@/components/common/StatusBadge';
import InboundActions from '@/components/inbound/InboundActions';
import { INBOUND_STATUS_LABEL, INBOUND_STATUS_TONE } from '@/constants/inboundStatus';
import { Inbound } from '@/types/inbound';
import { Column, Option } from '@/types/ui';

interface InboundTableProps {
  inbounds: Inbound[];
  locations: Option[];
  loading: boolean;
  onChanged: () => void;
}

/** 입고 목록. 상태별 처리 버튼을 함께 보여 준다. */
function InboundTable({ inbounds, locations, loading, onChanged }: InboundTableProps) {
  const columns: Column<Inbound>[] = [
    { header: '입고번호', render: (i) => i.inboundNo },
    { header: '거래처', render: (i) => i.partnerName },
    { header: '입고일', render: (i) => i.inboundDate },
    { header: '상품', render: (i) => i.productName },
    { header: '수량', render: (i) => i.quantity },
    {
      header: '상태',
      render: (i) => (
        <StatusBadge label={INBOUND_STATUS_LABEL[i.status]} tone={INBOUND_STATUS_TONE[i.status]} />
      ),
    },
    {
      header: '처리',
      render: (i) => <InboundActions inbound={i} locations={locations} onChanged={onChanged} />,
    },
  ];
  return <DataTable columns={columns} rows={inbounds} rowKey={(i) => i.id} loading={loading} />;
}

export default InboundTable;
