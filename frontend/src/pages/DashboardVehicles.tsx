import BackLink from '@/components/common/BackLink';
import PageHeader from '@/components/common/PageHeader';
import DeliveryCard from '@/components/shipping/DeliveryCard';
import { ROUTES } from '@/constants/routes';
import { useFetch } from '@/hooks/useFetch';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';
import { getDashboardVehicles } from '@/services/dashboardService';

/** 대시보드 상세: 차량 배송 현황 전체 */
function DashboardVehiclesPage() {
  const { data, loading, reload } = useFetch(getDashboardVehicles);
  useLogisticsEvents(reload);

  return (
    <>
      <BackLink to={ROUTES.DASHBOARD} label="전체 모니터링 현황" />
      <PageHeader title="차량 배송 현황" description="진행 중인 배송을 모두 보여 줍니다." />
      {!loading && (data ?? []).length === 0 && (
        <p className="text-sm text-gray-400">진행 중인 배송이 없습니다.</p>
      )}
      <div className="space-y-3">
        {(data ?? []).map((b) => (
          <DeliveryCard key={b.dispatchId} board={b} />
        ))}
      </div>
    </>
  );
}

export default DashboardVehiclesPage;
