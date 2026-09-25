/** 라우트 경로 상수 */
export const ROUTES = {
  DASHBOARD: '/',
  ORDERS: '/orders',
  ORDER_DETAIL: '/orders/:id',
  STOCKS: '/warehouse/stocks',
  INBOUNDS: '/warehouse/inbounds',
  WAREHOUSE_WORK: '/warehouse/work',
  LOADING: '/delivery/loading',
  DISPATCH: '/delivery/dispatch',
  DELIVERY_STATUS: '/delivery/status',
  DELIVERY_RESULT: '/delivery/result',
  RETURNS: '/returns',
  AUDIT_LOGS: '/audit-logs',
  PRODUCTS: '/master/products',
  LOCATIONS: '/master/locations',
  FLEET: '/master/fleet',
} as const;
