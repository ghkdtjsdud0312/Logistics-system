export interface LocationNode {
  id: number;
  code: string;
}

export interface ZoneNode {
  id: number;
  code: string;
  name: string;
  locations: LocationNode[];
}

export interface WarehouseNode {
  id: number;
  code: string;
  name: string;
  zones: ZoneNode[];
}

export interface CodeName {
  code: string;
  name: string;
}

/** 창고 화면에서 수정·삭제할 대상 */
export interface EditTarget {
  kind: 'warehouse' | 'zone' | 'location';
  id: number;
  /** 화면에 보여 줄 이름(위치는 코드) */
  name: string;
}
