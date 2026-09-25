import { Option } from '@/types/ui';

/** 감사로그 검색용 작업 코드 (서버의 작업 코드와 같은 값) */
export const AUDIT_ACTION_OPTIONS: Option[] = [
  { value: 'CREATE', label: '생성/등록' },
  { value: 'RELEASE', label: '출고 지시' },
  { value: 'PICK_COMPLETE', label: '피킹 완료' },
  { value: 'PACK_COMPLETE', label: '포장 완료' },
  { value: 'LOAD_COMPLETE', label: '상차 완료' },
  { value: 'DISPATCH', label: '배차 완료' },
  { value: 'DELIVERY_START', label: '배송 시작' },
  { value: 'DELIVER', label: '배송 완료' },
  { value: 'DELIVERY_FAIL', label: '배송 실패' },
  { value: 'PUTAWAY', label: '적치 완료' },
  { value: 'RETURN_RECEIVE', label: '반품 입고' },
];
