import { useCallback, useEffect, useRef, useState } from 'react';
import { toast } from 'sonner';
import { getErrorMessage } from '@/utils/errorMessage';

/**
 * 조회 함수를 실행해 data/loading을 관리한다.
 * key가 바뀌면 다시 조회하고, reload()로 수동 갱신할 수 있다.
 */
export function useFetch<T>(fetcher: () => Promise<T>, key = '') {
  const fetcherRef = useRef(fetcher);
  fetcherRef.current = fetcher;
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);

  const reload = useCallback(() => {
    setLoading(true);
    fetcherRef
      .current()
      .then(setData)
      .catch((e) => toast.error(getErrorMessage(e, '데이터를 불러오지 못했습니다.')))
      .finally(() => setLoading(false));
  }, []);

  useEffect(reload, [reload, key]);

  return { data, loading, reload };
}
