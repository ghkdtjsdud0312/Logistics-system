import { EditField } from '@/types/editField';

/** 수정 모달 입력 필드 정의 (코드류 식별자는 수정할 수 없다) */
export const PRODUCT_EDIT_FIELDS: EditField[] = [
  { name: 'name', label: '상품명' },
  { name: 'unit', label: '단위' },
  { name: 'unitWeightKg', label: '단위 중량(kg)', type: 'number' },
];

export const VEHICLE_EDIT_FIELDS: EditField[] = [
  { name: 'vehicleType', label: '차량종류' },
  { name: 'capacityKg', label: '적재량(kg)', type: 'number' },
];

export const DRIVER_EDIT_FIELDS: EditField[] = [
  { name: 'name', label: '이름' },
  { name: 'phone', label: '연락처', required: false },
];

export const NAME_EDIT_FIELDS: EditField[] = [{ name: 'name', label: '이름' }];
