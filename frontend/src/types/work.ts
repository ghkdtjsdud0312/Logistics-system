export type WorkStatus = 'WAITING' | 'IN_PROGRESS' | 'COMPLETED';

export interface PickingTask {
  id: number;
  taskNo: string;
  orderNo: string;
  locationCode: string;
  productName: string;
  requestedQty: number;
  pickedQty: number;
  status: WorkStatus;
}

export interface PackingTask {
  id: number;
  taskNo: string;
  orderNo: string;
  items: string;
  boxCode: string | null;
  status: WorkStatus;
}
