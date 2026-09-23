import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Dispatch } from '@/types/dispatch';
import { getDispatchList } from '@/services/dispatchService';
import DispatchWorkspace from '@/components/dispatch/DispatchWorkspace';
import DispatchBoard from '@/components/dispatch/DispatchBoard';

/** 배차 관리 페이지: 차량 후보 조회 -> 배차 확정 흐름 + 배차 현황 보드 */
function DispatchPage() {
  const [dispatches, setDispatches] = useState<Dispatch[]>([]);

  const reload = () => {
    getDispatchList()
      .then(setDispatches)
      .catch(() => toast.error('배차 목록을 불러오지 못했습니다.'));
  };

  useEffect(reload, []);

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold text-gray-900">배차 관리</h1>
      <DispatchWorkspace onConfirmed={reload} />
      <DispatchBoard dispatches={dispatches} onChanged={reload} />
    </div>
  );
}

export default DispatchPage;
