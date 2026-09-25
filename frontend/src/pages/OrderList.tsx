import { useState } from 'react';
import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT } from '@/constants/nextStep';
import Section from '@/components/common/Section';
import OrderForm from '@/components/order/OrderForm';
import OrderSearchBar from '@/components/order/OrderSearchBar';
import OrderTable from '@/components/order/OrderTable';
import { useFetch } from '@/hooks/useFetch';
import { getOrders } from '@/services/orderService';
import { getProducts } from '@/services/productService';
import { OrderSearch } from '@/types/order';

const EMPTY: OrderSearch = { orderNo: '', customerName: '', status: '', from: '', to: '' };

/** 주문 목록 */
function OrderListPage() {
  const [search, setSearch] = useState<OrderSearch>(EMPTY);
  const products = useFetch(() => getProducts());
  const orders = useFetch(() => getOrders(search), JSON.stringify(search));

  return (
    <>
      <PageHeader
        title="주문 목록"
        next={PAGE_NEXT.ORDERS}
        description="전체 주문을 조회하고 현재 처리 상태를 확인합니다."
      />
      <FormCard title="주문 등록 (재고가 예약됩니다)">
        <OrderForm products={products.data ?? []} onCreated={orders.reload} />
      </FormCard>
      <Section title="주문 조회">
        <OrderSearchBar onSearch={setSearch} />
        <OrderTable orders={orders.data ?? []} loading={orders.loading} />
      </Section>
    </>
  );
}

export default OrderListPage;
