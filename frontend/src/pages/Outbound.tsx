import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Outbound } from '@/types/outbound';
import { getOutboundList } from '@/services/outboundService';
import OutboundForm from '@/components/outbound/OutboundForm';
import OutboundBoard from '@/components/outbound/OutboundBoard';

/** 출고 관리 페이지: 출고 대상 선정 → 출고 계획 → 피킹 → 출고 완료 흐름 */
function OutboundPage() {
  const [outbounds, setOutbounds] = useState<Outbound[]>([]);

  const reload = () => {
    getOutboundList()
      .then(setOutbounds)
      .catch(() => toast.error('출고 목록을 불러오지 못했습니다.'));
  };

  useEffect(reload, []);

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold text-gray-900">출고 관리</h1>
      <OutboundForm onCreated={reload} />
      <OutboundBoard outbounds={outbounds} onChanged={reload} />
    </div>
  );
}

export default OutboundPage;
