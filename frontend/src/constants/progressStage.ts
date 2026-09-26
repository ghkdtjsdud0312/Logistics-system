/** 물류 진행 현황 단계 (백엔드 ProgressStage와 같은 키) */
export const PROGRESS_STAGES: { key: string; label: string }[] = [
  { key: 'ORDERS', label: '주문' },
  { key: 'PICKING', label: '피킹' },
  { key: 'PACKING', label: '포장' },
  { key: 'LOADING', label: '상차' },
  { key: 'DELIVERY', label: '배송' },
];
