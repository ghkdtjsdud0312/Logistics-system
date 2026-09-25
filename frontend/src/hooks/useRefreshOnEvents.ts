import { useEffect, useRef } from 'react';
import { useLogisticsEvents } from '@/hooks/useLogisticsEvents';

/**
 * 물류 상태 변경 이벤트(SSE)가 오면 refresh를 호출해 화면을 자동으로 갱신한다.
 * 한 번의 처리에서 이벤트가 연달아 오므로 짧게 모아서(기본 300ms) 한 번만 호출한다.
 */
export function useRefreshOnEvents(refresh: () => void, delayMs = 300) {
  const timer = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
  const refreshRef = useRef(refresh);
  refreshRef.current = refresh;

  useLogisticsEvents(() => {
    clearTimeout(timer.current);
    timer.current = setTimeout(() => refreshRef.current(), delayMs);
  });

  useEffect(() => () => clearTimeout(timer.current), []);
}
