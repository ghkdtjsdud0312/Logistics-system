/**
 * 배차 도메인 타입
 * 흐름: 출고 물량 선택 -> 차량 후보 조회 -> 적재량 검증(서버) -> 차량 선택 -> 기사 배정 -> 배차 확정
 *      -> 경로 최적화 -> 상차/운행/완료 상태 전이
 */
export type DispatchStatus = 'CONFIRMED' | 'LOADED' | 'IN_TRANSIT' | 'COMPLETED';
export type RouteStopStatus = 'PENDING' | 'ARRIVED' | 'DELIVERED';

export interface VehicleCandidate {
  vehicleId: number;
  vehicleNumber: string;
  usedWeightRatio: number;
  usedVolumeRatio: number;
  score: number;
  reason: string;
}

export interface ExcludedVehicle {
  vehicleId: number;
  vehicleNumber: string;
  reasonCode: string;
  reasonMessage: string;
}

export interface DispatchCandidateResult {
  totalWeightKg: number;
  totalVolumeM3: number;
  candidates: VehicleCandidate[];
  excluded: ExcludedVehicle[];
}

export interface DispatchCandidateRequest {
  outboundIds: number[];
  plannedAt: string;
}

export interface DispatchConfirmRequest {
  vehicleId: number;
  driverId: number;
  outboundIds: number[];
  plannedAt: string;
}

export interface Dispatch {
  id: number;
  vehicleId: number;
  driverId: number;
  outboundIds: number[];
  plannedAt: string;
  totalWeightKg: number;
  totalVolumeM3: number;
  status: DispatchStatus;
  version: number;
}

export interface RouteStop {
  id: number;
  sequence: number;
  outboundId: number | null;
  label: string;
  latitude: number;
  longitude: number;
  distanceFromPreviousKm: number;
  status: RouteStopStatus;
}

export interface RouteOptimizeResult {
  dispatchId: number;
  algorithm: string;
  initialDistanceKm: number;
  optimizedDistanceKm: number;
  improvementRate: number;
  stops: RouteStop[];
}

export interface DispatchStatusHistoryEntry {
  fromStatus: DispatchStatus;
  toStatus: DispatchStatus;
  actor: string;
  description: string | null;
  changedAt: string;
}

export interface DispatchDetail extends Dispatch {
  stops: RouteStop[];
  statusHistory: DispatchStatusHistoryEntry[];
}

export interface DispatchStatusChangeRequest {
  status: DispatchStatus;
  expectedVersion: number;
  actor?: string;
  description?: string;
}
