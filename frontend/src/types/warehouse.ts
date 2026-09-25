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
