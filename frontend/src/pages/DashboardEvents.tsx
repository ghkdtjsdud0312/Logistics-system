import BackLink from '@/components/common/BackLink';
import PageHeader from '@/components/common/PageHeader';
import RecentEvents from '@/components/dashboard/RecentEvents';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import { getDashboardEvents } from '@/services/dashboardService';

const LIMIT = 100;

/** 대시보드 상세: 물류 이벤트 전체 (최신순, 실시간) */
function DashboardEventsPage() {
  const { data, reload } = useFetch(() => getDashboardEvents(LIMIT));
  useLogisticsEvents(reload);

  return (
    <>
      <BackLink to={ROUTES.DASHBOARD} label="전체 모니터링 현황" />
      <PageHeader title="물류 이벤트" description="최신순 최대 100건 (실시간)" />
      <RecentEvents events={data ?? []} />
    </>
  );
}

export default DashboardEventsPage;
