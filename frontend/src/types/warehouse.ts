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
  address: string | null;
  latitude: number | null;
  longitude: number | null;
  zones: ZoneNode[];
}

export interface CodeName {
  code: string;
  name: string;
  /** 창고만 사용. 있으면 서버가 좌표로 변환한다. */
  address?: string;
}

/** 창고 화면에서 수정·삭제할 대상 */
export interface EditTarget {
  kind: 'warehouse' | 'zone' | 'location';
  id: number;
  /** 화면에 보여 줄 이름(위치는 코드) */
  name: string;
  /** 창고만 사용 */
  address?: string;
}
