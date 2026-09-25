import { useParams } from 'react-router-dom';
import PageHeader from '@/components/common/PageHeader';
import Section from '@/components/common/Section';
import OrderDeliveryInfo from '@/components/order/OrderDeliveryInfo';
import OrderEventList from '@/components/order/OrderEventList';
import OrderInfo from '@/components/order/OrderInfo';
import OrderItemsTable from '@/components/order/OrderItemsTable';
import OrderTimeline from '@/components/order/OrderTimeline';
import { useFetch } from '@/hooks/useFetch';
import { getOrder } from '@/services/orderService';

/** 주문 상세: 수량, 진행 타임라인, 배송 정보, 이벤트 이력 */
function OrderDetailPage() {
  const { id } = useParams();
  const { data: order, reload } = useFetch(() => getOrder(Number(id)), String(id));

  if (!order) {
    return <PageHeader title="주문 상세" description="불러오는 중..." />;
  }
  return (
    <>
      <PageHeader title="주문 상세" />
      <OrderInfo order={order} onChanged={reload} />
      <Section title="주문 상품">
        <OrderItemsTable items={order.items} />
      </Section>
      <Section title="물류 진행 상황">
        <OrderTimeline steps={order.timeline} />
      </Section>
      <Section title="배송 정보">
        <OrderDeliveryInfo delivery={order.delivery} />
      </Section>
      <Section title="이벤트 이력">
        <OrderEventList events={order.events} />
      </Section>
    </>
  );
}

export default OrderDetailPage;
