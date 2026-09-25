import { useParams } from 'react-router-dom';
import BackLink from '@/components/common/BackLink';
import DetailCard from '@/components/common/DetailCard';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import StatusBadge from '@/components/common/StatusBadge';
import StepTimeline from '@/components/common/StepTimeline';
import ReturnActions from '@/components/returns/ReturnActions';
import { FAIL_REASON_LABEL } from '@/constants/failReason';
import { RETURN_STATUS_LABEL, RETURN_STATUS_TONE } from '@/constants/returnStatus';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useRefreshOnEvents } from '@/hooks/useRefreshOnEvents';
import { getReturn } from '@/services/returnService';
import { getWarehouseTree } from '@/services/warehouseService';
import { ReturnStatus } from '@/types/return';
import { locationLabel } from '@/utils/locationLabel';
import { locationOptions } from '@/utils/warehouseTree';

const FLOW: ReturnStatus[] = [
  'REQUESTED',
  'COLLECTING',
  'COLLECTED',
  'RETURN_RECEIVED',
  'COMPLETED',
];

/** 반품 상세: 반품 정보, 회수 진행 단계, 단계별 처리 */
function ReturnDetailPage() {
  const { id } = useParams();
  const returnOrder = useFetch(() => getReturn(Number(id)), String(id));
  const tree = useFetch(getWarehouseTree);
  useRefreshOnEvents(returnOrder.reload);
  const data = returnOrder.data;
  const locations = locationOptions(tree.data ?? []);

  if (!data) {
    return <PageHeader title="반품 상세" description="불러오는 중..." />;
  }
  const current = FLOW.indexOf(data.status);
  return (
    <>
      <BackLink to={ROUTES.RETURNS} label="반품 목록" />
      <DetailCard
        title={data.returnNo}
        badge={
          <StatusBadge
            size="lg"
            label={RETURN_STATUS_LABEL[data.status]}
            tone={RETURN_STATUS_TONE[data.status]}
          />
        }
        action={
          <ReturnActions returnOrder={data} locations={locations} onChanged={returnOrder.reload} />
        }
        fields={[
          ['주문번호', data.orderNo],
          ['고객', data.customerName],
          ['상품', data.items],
          ['반품사유', FAIL_REASON_LABEL[data.reason]],
          ['수량', String(data.quantity)],
          [
            '재고 복구 위치',
            data.reason === 'DAMAGED'
              ? '복구 안 함(상품 파손)'
              : locationLabel(locations, data.locationId),
          ],
        ]}
      />
      <Section title="반품 흐름">
        <StepTimeline
          steps={FLOW.map((s, i) => ({
            key: s,
            label: RETURN_STATUS_LABEL[s],
            done: i <= current,
          }))}
        />
      </Section>
    </>
  );
}

export default ReturnDetailPage;
