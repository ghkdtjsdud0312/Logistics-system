import { useState } from 'react';
import CheckList from '@/components/common/CheckList';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT, PAGE_PREV } from '@/constants/nextStep';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useFetch } from '@/hooks/useFetch';
import { useSubmit } from '@/hooks/useSubmit';
import { getWaitingOrders, loadOrders } from '@/services/loadingService';
import { toggleId } from '@/utils/selection';

/** 상차관리: 포장완료 주문을 선택해 상차 */
function LoadingPage() {
  const { data, reload } = useFetch(getWaitingOrders);
  const [selected, setSelected] = useState<number[]>([]);
  const { submitting, run } = useSubmit();

  const load = async () => {
    if (await run(() => loadOrders(selected), '상차를 완료했습니다.')) {
      setSelected([]);
      reload();
    }
  };

  return (
    <>
      <PageHeader
        title="상차관리"
        prev={PAGE_PREV.LOADING}
        next={PAGE_NEXT.LOADING}
        description="포장된 주문을 선택해 상차합니다. 차량과 기사는 배차관리에서 배정합니다."
      />
      <div className="mb-3">
        <CheckList
          emptyText="상차 대기 주문이 없습니다."
          selected={selected}
          onToggle={(id) => setSelected(toggleId(selected, id))}
          items={(data ?? []).map((o) => ({
            id: o.id,
            title: o.orderNo,
            detail: `${o.customerName} · ${o.productSummary} ${o.quantity}`,
          }))}
        />
      </div>
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting || selected.length === 0}
        onClick={load}
      >
        상차완료 ({selected.length}건)
      </button>
    </>
  );
}

export default LoadingPage;
