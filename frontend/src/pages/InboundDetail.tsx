import { useParams } from 'react-router-dom';
import BackLink from '@/components/common/BackLink';
import DetailCard from '@/components/common/DetailCard';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import StatusBadge from '@/components/common/StatusBadge';
import StepTimeline from '@/components/common/StepTimeline';
import InboundActions from '@/components/inbound/InboundActions';
import { INBOUND_STATUS_LABEL, INBOUND_STATUS_TONE } from '@/constants/inboundStatus';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useRefreshOnEvents } from '@/hooks/useRefreshOnEvents';
import { getInbound } from '@/services/inboundService';
import { getWarehouseTree } from '@/services/warehouseService';
import { InboundStatus } from '@/types/inbound';
import { locationLabel } from '@/utils/locationLabel';
import { locationOptions } from '@/utils/warehouseTree';

const FLOW: InboundStatus[] = ['EXPECTED', 'RECEIVED', 'PUTAWAY_WAITING', 'PUTAWAY_DONE'];

/** 입고 상세: 입고 정보, 진행 단계, 입고완료·적치 처리 */
function InboundDetailPage() {
  const { id } = useParams();
  const inbound = useFetch(() => getInbound(Number(id)), String(id));
  const tree = useFetch(getWarehouseTree);
  useRefreshOnEvents(inbound.reload);
  const data = inbound.data;
  const locations = locationOptions(tree.data ?? []);

  if (!data) {
    return <PageHeader title="입고 상세" description="불러오는 중..." />;
  }
  const current = FLOW.indexOf(data.status);
  return (
    <>
      <BackLink to={ROUTES.INBOUNDS} label="입고 목록" />
      <DetailCard
        title={data.inboundNo}
        badge={
          <StatusBadge
            size="lg"
            label={INBOUND_STATUS_LABEL[data.status]}
            tone={INBOUND_STATUS_TONE[data.status]}
          />
        }
        action={<InboundActions inbound={data} locations={locations} onChanged={inbound.reload} />}
        fields={[
          ['거래처', data.partnerName],
          ['상품', `${data.productCode} ${data.productName}`],
          ['입고수량', String(data.quantity)],
          ['입고일', data.inboundDate],
          ['적치 위치', locationLabel(locations, data.locationId)],
        ]}
      />
      <Section title="진행 단계">
        <StepTimeline
          steps={FLOW.map((s, i) => ({
            key: s,
            label: INBOUND_STATUS_LABEL[s],
            done: i <= current,
          }))}
        />
      </Section>
    </>
  );
}

export default InboundDetailPage;
