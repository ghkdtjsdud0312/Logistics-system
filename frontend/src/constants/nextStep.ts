import { ROUTES } from '@/constants/routes';
import { NextStep } from '@/types/nextStep';
import { OrderStatus } from '@/types/order';

/** 주문 상태별로 다음에 해야 할 일과 이동할 화면. 주문접수·배송완료는 별도 처리한다. */
export const ORDER_NEXT_STEP: Partial<Record<OrderStatus, NextStep>> = {
  OUTBOUND_WAITING: { label: '피킹 진행하기', to: ROUTES.WAREHOUSE_WORK },
  PICKING: { label: '피킹·포장 진행하기', to: ROUTES.WAREHOUSE_WORK },
  PICKED: { label: '포장 진행하기', to: ROUTES.WAREHOUSE_WORK },
  PACKED: { label: '상차하러 가기', to: ROUTES.LOADING },
  LOADED: { label: '배차하러 가기', to: ROUTES.DISPATCH },
  DISPATCHED: { label: '배송 시작하러 가기', to: ROUTES.DISPATCH },
  IN_DELIVERY: { label: '배송 완료·실패 처리', to: ROUTES.DELIVERY_RESULT },
  FAILED: { label: '반품 처리하러 가기', to: ROUTES.RETURNS },
};

/** 화면별 "이전 단계" 안내 (첫 단계인 주문 목록은 없다) */
export const PAGE_PREV: Record<string, NextStep> = {
  WAREHOUSE_WORK: { label: '주문 목록', to: ROUTES.ORDERS },
  LOADING: { label: '피킹·포장', to: ROUTES.WAREHOUSE_WORK },
  DISPATCH: { label: '상차관리', to: ROUTES.LOADING },
  DELIVERY_STATUS: { label: '배차관리', to: ROUTES.DISPATCH },
  DELIVERY_RESULT: { label: '배송현황', to: ROUTES.DELIVERY_STATUS },
  RETURNS: { label: '배송완료·실패', to: ROUTES.DELIVERY_RESULT },
};

/** 화면별 "다음 단계" 안내 */
export const PAGE_NEXT: Record<string, NextStep> = {
  ORDERS: { label: '피킹·포장', to: ROUTES.WAREHOUSE_WORK },
  WAREHOUSE_WORK: { label: '상차관리', to: ROUTES.LOADING },
  LOADING: { label: '배차관리', to: ROUTES.DISPATCH },
  DISPATCH: { label: '배송현황', to: ROUTES.DELIVERY_STATUS },
  DELIVERY_STATUS: { label: '배송완료·실패', to: ROUTES.DELIVERY_RESULT },
  DELIVERY_RESULT: { label: '반품관리 (실패 건)', to: ROUTES.RETURNS },
};
