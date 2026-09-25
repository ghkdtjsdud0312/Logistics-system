import { BUTTON_PRIMARY } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { releaseOrder } from '@/services/orderService';
import { OrderStatus } from '@/types/order';

/** 주문접수 상태에서만 보이는 출고 지시 버튼 */
function ReleaseButton({
  orderId,
  status,
  onDone,
}: {
  orderId: number;
  status: OrderStatus;
  onDone: () => void;
}) {
  const { submitting, run } = useSubmit();
  if (status !== 'RECEIVED') return null;

  return (
    <button
      className={BUTTON_PRIMARY}
      disabled={submitting}
      onClick={async () => {
        if (await run(() => releaseOrder(orderId), '출고 지시했습니다.')) onDone();
      }}
    >
      출고 지시
    </button>
  );
}

export default ReleaseButton;
