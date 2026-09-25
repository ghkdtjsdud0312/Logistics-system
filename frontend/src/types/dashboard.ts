export interface DashboardSummary {
  date: string;
  orders: number;
  pickingWaiting: number;
  packingWaiting: number;
  loadingWaiting: number;
  inDelivery: number;
  delivered: number;
  failed: number;
  progress: Record<string, number>;
}

export interface DashboardEvent {
  at: string;
  description: string;
}
