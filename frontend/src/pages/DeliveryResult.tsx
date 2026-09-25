import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT } from '@/constants/nextStep';
import DeliveryResultTable from '@/components/shipping/DeliveryResultTable';
import { useFetch } from '@/hooks/useFetch';
import { useRefreshOnEvents } from '@/hooks/useRefreshOnEvents';
import { getShipments } from '@/services/loadingService';

/** 배송완료·실패: 배송중인 주문을 완료 또는 실패로 처리 */
function DeliveryResultPage() {
  const { data, loading, reload } = useFetch(() => getShipments('IN_DELIVERY'));
  useRefreshOnEvents(reload);

  return (
    <>
      <PageHeader
        title="배송완료·실패"
        next={PAGE_NEXT.DELIVERY_RESULT}
        description="인도수량이 배송수량과 같을 때만 배송완료 처리할 수 있습니다."
      />
      <DeliveryResultTable shipments={data ?? []} loading={loading} onChanged={reload} />
    </>
  );
}

export default DeliveryResultPage;
