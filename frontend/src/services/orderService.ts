import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { OrderCreate, OrderDetail, OrderListItem, OrderSearch } from '@/types/order';

const clean = (v: string) => v || undefined;

export const getOrders = (s: OrderSearch) =>
  unwrap<OrderListItem[]>(
    apiClient.get('/orders', {
      params: {
        orderNo: clean(s.orderNo),
        customerName: clean(s.customerName),
        status: clean(s.status),
        from: clean(s.from),
        to: clean(s.to),
      },
    }),
  );

export const getOrder = (id: number) => unwrap<OrderDetail>(apiClient.get(`/orders/${id}`));

export const createOrder = (body: OrderCreate) =>
  unwrap<{ id: number; orderNo: string }>(apiClient.post('/orders', body));

export const releaseOrder = (id: number) =>
  unwrap<unknown>(apiClient.post(`/orders/${id}/release`));
