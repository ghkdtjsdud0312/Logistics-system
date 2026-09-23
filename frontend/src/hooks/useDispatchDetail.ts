import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { DispatchDetail, RouteStopStatus } from '@/types/dispatch';
import { changeDispatchStatus, changeStopStatus, getDispatchDetail, optimizeRoute } from '@/services/dispatchService';
import { nextDispatchStatus, nextStopStatus } from '@/utils/dispatchStatusFlow';

/** 배차 상세(경로/상태 이력) 조회 + 경로 최적화/상태 전이 액션 */
export function useDispatchDetail(dispatchId: number, onChanged: () => void) {
  const [detail, setDetail] = useState<DispatchDetail | null>(null);

  const reload = () => {
    getDispatchDetail(dispatchId)
      .then(setDetail)
      .catch(() => toast.error('배차 상세를 불러오지 못했습니다.'));
  };

  useEffect(reload, [dispatchId]);

  const runOptimize = async () => {
    try {
      await optimizeRoute(dispatchId);
      reload();
      onChanged();
    } catch {
      toast.error('경로 최적화에 실패했습니다.');
    }
  };

  const advanceStatus = async () => {
    if (!detail) return;
    const next = nextDispatchStatus(detail.status);
    if (!next) return;
    try {
      await changeDispatchStatus(dispatchId, { status: next, expectedVersion: detail.version });
      reload();
      onChanged();
    } catch {
      toast.error('상태 변경에 실패했습니다. 경유지가 모두 배송 완료되었는지 확인하세요.');
    }
  };

  const advanceStop = async (stopId: number, current: RouteStopStatus) => {
    const next = nextStopStatus(current);
    if (!next) return;
    try {
      await changeStopStatus(dispatchId, stopId, next);
      reload();
    } catch {
      toast.error('경유지 상태 변경에 실패했습니다.');
    }
  };

  return { detail, runOptimize, advanceStatus, advanceStop };
}
