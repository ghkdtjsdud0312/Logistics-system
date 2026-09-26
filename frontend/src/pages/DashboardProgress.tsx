import { useState } from 'react';
import BackLink from '@/components/common/BackLink';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import ProgressChart from '@/components/dashboard/ProgressChart';
import StatCards from '@/components/dashboard/StatCards';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import OrderTable from '@/components/order/OrderTable';
import { PROGRESS_STAGES } from '@/constants/progressStage';
import { getDashboardSummary, getProgressOrders } from '@/services/dashboardService';

/** 대시보드 상세: 오늘 주문 기준 물류 진행 현황과 단계별 주문 목록 (실시간) */
function DashboardProgressPage() {
  const [stage, setStage] = useState('ORDERS');
  const { data, reload } = useFetch(() => getDashboardSummary());
  const orders = useFetch(() => getProgressOrders(stage), stage);
  useLogisticsEvents(() => {
    reload();
    orders.reload();
  });
  const stageLabel = PROGRESS_STAGES.find((s) => s.key === stage)?.label;

  return (
    <>
      <BackLink to={ROUTES.DASHBOARD} label="전체 모니터링 현황" />
      <PageHeader title="물류 진행 현황" description="오늘 주문 기준 (실시간)" />
      {data && <StatCards summary={data} />}
      <Section title="단계별 진행 (단계를 누르면 해당 주문이 아래에 나옵니다)">
        {data && <ProgressChart progress={data.progress} selected={stage} onSelect={setStage} />}
      </Section>
      <Section title={`${stageLabel} 단계 주문 (${(orders.data ?? []).length}건)`}>
        <OrderTable orders={orders.data ?? []} loading={orders.loading} />
      </Section>
    </>
  );
}

export default DashboardProgressPage;
