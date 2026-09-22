import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Inbound } from '@/types/inbound';
import { getInboundList } from '@/services/inboundService';
import InboundForm from '@/components/inbound/InboundForm';
import InboundBoard from '@/components/inbound/InboundBoard';

/** 입고 관리 페이지: 입고 요청 → 입고 처리 → 검수 완료 흐름 */
function InboundPage() {
  const [inbounds, setInbounds] = useState<Inbound[]>([]);

  const reload = () => {
    getInboundList()
      .then(setInbounds)
      .catch(() => toast.error('입고 목록을 불러오지 못했습니다.'));
  };

  useEffect(reload, []);

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold text-gray-900">입고 관리</h1>
      <InboundForm onCreated={reload} />
      <InboundBoard inbounds={inbounds} onChanged={reload} />
    </div>
  );
}

export default InboundPage;
