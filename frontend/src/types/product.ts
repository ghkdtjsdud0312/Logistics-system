export interface Product {
  id: number;
  code: string;
  name: string;
  unit: string;
  unitWeightKg: number;
  active: boolean;
}

export interface ProductCreate {
  code: string;
  name: string;
  unit: string;
  unitWeightKg: number;
}
