import { useState } from 'react';
import DetailLink from '@/components/common/DetailLink';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import ProgressChart from '@/components/dashboard/ProgressChart';
import RecentEvents from '@/components/dashboard/RecentEvents';
import StatCards from '@/components/dashboard/StatCards';
import DeliveryCard from '@/components/shipping/DeliveryCard';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import {
  getDashboardEvents,
  getDashboardSummary,
  getDashboardVehicles,
} from '@/services/dashboardService';
import { DashboardEvent } from '@/types/dashboard';
import { mergeEvents } from '@/utils/events';

const PREVIEW = 3;

/** 대시보드: 오늘의 물류 현황만 보여 주고 SSE로 실시간 갱신한다. 날짜별 조회는 상세 화면에서 한다. */
function DashboardPage() {
  const summary = useFetch(() => getDashboardSummary());
  const vehicles = useFetch(getDashboardVehicles);
  const events = useFetch(() => getDashboardEvents(PREVIEW));
  const [live, setLive] = useState<DashboardEvent[]>([]);

  useLogisticsEvents((event) => {
    setLive((prev) => [event, ...prev].slice(0, PREVIEW));
    summary.reload();
    vehicles.reload();
  });

  const boards = (vehicles.data ?? []).slice(0, PREVIEW);

  return (
    <>
      <PageHeader title="전체 모니터링 현황" description="오늘의 물류 현황 (실시간)" />
      {summary.data && <StatCards summary={summary.data} />}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Section title="물류 진행 현황" action={<DetailLink to={ROUTES.DASHBOARD_PROGRESS} />}>
          {summary.data && <ProgressChart progress={summary.data.progress} />}
        </Section>
        <Section title="차량 배송 현황" action={<DetailLink to={ROUTES.DASHBOARD_VEHICLES} />}>
          {boards.length === 0 && (
            <p className="text-sm text-gray-400">진행 중인 배송이 없습니다.</p>
          )}
          <div className="space-y-3">
            {boards.map((b) => (
              <DeliveryCard key={b.dispatchId} board={b} />
            ))}
          </div>
        </Section>
      </div>
      <Section title="최근 물류 이벤트" action={<DetailLink to={ROUTES.DASHBOARD_EVENTS} />}>
        <RecentEvents events={mergeEvents(live, events.data ?? [], PREVIEW)} />
      </Section>
    </>
  );
}

export default DashboardPage;
