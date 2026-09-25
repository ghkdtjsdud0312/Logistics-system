import PageHeader from '@/components/common/PageHeader';
import DeliveryCard from '@/components/shipping/DeliveryCard';
import { useFetch } from '@/hooks/useFetch';
import { getDeliveryBoard } from '@/services/deliveryService';

/** 배송현황: 진행 중인 배차별 진행률과 주문별 상태 */
function DeliveryStatusPage() {
  const { data, loading } = useFetch(getDeliveryBoard);
  const boards = data ?? [];

  return (
    <>
      <PageHeader title="배송현황" description="차량별 배송 진행 상황을 확인합니다." />
      {boards.length === 0 && (
        <p className="text-sm text-gray-400">
          {loading ? '불러오는 중...' : '진행 중인 배송이 없습니다.'}
        </p>
      )}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        {boards.map((b) => (
          <DeliveryCard key={b.dispatchId} board={b} />
        ))}
      </div>
    </>
  );
}

export default DeliveryStatusPage;
