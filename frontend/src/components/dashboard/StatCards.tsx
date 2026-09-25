import StatCard from '@/components/dashboard/StatCard';
import { DashboardSummary } from '@/types/dashboard';

/** 오늘의 물류 현황 카드 7개 */
function StatCards({ summary }: { summary: DashboardSummary }) {
  return (
    <div className="mb-4 grid grid-cols-2 gap-3 md:grid-cols-7">
      <StatCard label="주문" value={summary.orders} tone="gray" />
      <StatCard label="피킹 대기" value={summary.pickingWaiting} tone="yellow" />
      <StatCard label="포장 대기" value={summary.packingWaiting} tone="yellow" />
      <StatCard label="상차 대기" value={summary.loadingWaiting} tone="blue" />
      <StatCard label="배송중" value={summary.inDelivery} tone="green" />
      <StatCard label="완료" value={summary.delivered} tone="green" />
      <StatCard label="실패" value={summary.failed} tone="red" />
    </div>
  );
}

export default StatCards;
