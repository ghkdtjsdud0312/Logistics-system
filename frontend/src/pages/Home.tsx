import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { AvailableInbound } from '@/types/outbound';
import { getAvailableInbounds } from '@/services/outboundService';
import { groupByWarehouseLocation } from '@/utils/warehouseSummary';
import WarehouseSummaryBoard from '@/components/warehouse/WarehouseSummaryBoard';

/** 대시보드: 창고 위치별 출고 가능 재고 현황 */
function HomePage() {
  const [available, setAvailable] = useState<AvailableInbound[]>([]);

  useEffect(() => {
    getAvailableInbounds()
      .then(setAvailable)
      .catch(() => toast.error('창고 현황을 불러오지 못했습니다.'));
  }, []);

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-semibold text-gray-900">대시보드</h1>
        <p className="mt-1 text-sm text-gray-500">
          창고 위치별로 현재 출고 가능한 재고 수량을 보여줍니다.
        </p>
      </div>
      <WarehouseSummaryBoard summaries={groupByWarehouseLocation(available)} />
    </div>
  );
}

export default HomePage;
