import { ROUTES } from '@/constants/routes';
import { MenuGroup } from '@/types/menu';

/** 사이드바 메뉴 구조 */
export const MENU: MenuGroup[] = [
  { label: '대시보드', children: [{ label: '전체 모니터링 현황', to: ROUTES.DASHBOARD }] },
  {
    label: '주문·출고',
    children: [
      { label: '주문 목록', to: ROUTES.ORDERS },
      { label: '피킹·포장', to: ROUTES.WAREHOUSE_WORK },
      { label: '상차관리', to: ROUTES.LOADING },
      { label: '배차관리', to: ROUTES.DISPATCH },
      { label: '배송현황', to: ROUTES.DELIVERY_STATUS },
      { label: '배송완료·실패', to: ROUTES.DELIVERY_RESULT },
      { label: '반품관리', to: ROUTES.RETURNS },
    ],
  },
  {
    label: '재고·입고',
    children: [
      { label: '재고 현황', to: ROUTES.STOCKS },
      { label: '입고·적치', to: ROUTES.INBOUNDS },
    ],
  },
  {
    label: '기준정보등록(관리자)',
    children: [
      { label: '상품관리', to: ROUTES.PRODUCTS },
      {
        label: '창고·위치관리',
        children: [
          { label: '창고 구조 등록 및 목록', to: ROUTES.LOCATIONS, end: true },
          { label: '창고 구조 3D 보기', to: ROUTES.LOCATIONS_3D },
        ],
      },
      {
        label: '차량·기사관리',
        children: [
          { label: '차량 등록 및 목록', to: ROUTES.VEHICLES },
          { label: '기사 등록 및 목록', to: ROUTES.DRIVERS },
        ],
      },
      {
        label: '관리',
        children: [{ label: '감사로그', to: ROUTES.AUDIT_LOGS }],
      },
      { label: '스케줄(달력)', to: ROUTES.SCHEDULE },
    ],
  },
];
