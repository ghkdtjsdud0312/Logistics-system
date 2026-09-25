import { useEffect, useRef } from 'react';
import { API_BASE_URL } from '@/config/env';
import { DashboardEvent } from '@/types/dashboard';

/**
 * 물류 상태 변경 SSE(status-changed)를 구독한다. 연결이 끊기면 브라우저가 자동으로 재연결한다.
 * onEvent는 최신 함수를 항상 사용한다.
 */
export function useLogisticsEvents(onEvent: (event: DashboardEvent) => void) {
  const handlerRef = useRef(onEvent);
  handlerRef.current = onEvent;

  useEffect(() => {
    const source = new EventSource(`${API_BASE_URL}/events/logistics`);
    source.addEventListener('status-changed', (e) => {
      handlerRef.current(JSON.parse((e as MessageEvent<string>).data) as DashboardEvent);
    });
    return () => source.close();
  }, []);
}
