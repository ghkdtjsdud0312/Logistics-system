import { DashboardSummary } from '@/types/dashboard';

interface Stat {
  label: string;
  value: number;
  color: string;
}

/** 오늘의 물류 현황 카드 7개 */
function StatCards({ summary }: { summary: DashboardSummary }) {
  const stats: Stat[] = [
    { label: '주문', value: summary.orders, color: 'text-gray-900' },
    { label: '피킹 대기', value: summary.pickingWaiting, color: 'text-yellow-600' },
    { label: '포장 대기', value: summary.packingWaiting, color: 'text-yellow-600' },
    { label: '상차 대기', value: summary.loadingWaiting, color: 'text-blue-600' },
    { label: '배송중', value: summary.inDelivery, color: 'text-green-600' },
    { label: '완료', value: summary.delivered, color: 'text-green-700' },
    { label: '실패', value: summary.failed, color: 'text-red-600' },
  ];
  return (
    <div className="mb-4 grid grid-cols-2 gap-3 md:grid-cols-7">
      {stats.map((s) => (
        <div key={s.label} className="rounded-md border border-gray-200 bg-white p-3 text-center">
          <div className="text-xs text-gray-500">{s.label}</div>
          <div className={`text-2xl font-bold ${s.color}`}>{s.value}</div>
        </div>
      ))}
    </div>
  );
}

export default StatCards;
