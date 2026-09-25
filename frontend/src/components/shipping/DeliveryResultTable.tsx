import DataTable from '@/components/common/DataTable';
import DeliveryResultActions from '@/components/shipping/DeliveryResultActions';
import { Shipment } from '@/types/shipment';
import { Column } from '@/types/ui';

interface DeliveryResultTableProps {
  shipments: Shipment[];
  loading: boolean;
  onChanged: () => void;
}

/** 배송중인 주문 목록과 완료/실패 처리 */
function DeliveryResultTable({ shipments, loading, onChanged }: DeliveryResultTableProps) {
  const columns: Column<Shipment>[] = [
    { header: '주문번호', render: (s) => s.orderNo },
    { header: '고객', render: (s) => s.customerName },
    { header: '배송지', render: (s) => s.address },
    { header: '배송수량', render: (s) => s.quantity },
    {
      header: '처리(인도수량)',
      render: (s) => <DeliveryResultActions shipment={s} onChanged={onChanged} />,
    },
  ];
  return <DataTable columns={columns} rows={shipments} rowKey={(s) => s.id} loading={loading} />;
}

export default DeliveryResultTable;
