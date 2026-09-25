import { Link } from 'react-router-dom';
import ReleaseButton from '@/components/order/ReleaseButton';
import { ORDER_NEXT_STEP } from '@/constants/nextStep';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { OrderStatus } from '@/types/order';

interface OrderNextActionProps {
  orderId: number;
  status: OrderStatus;
  onDone: () => void;
}

/** 주문 상태에 맞는 다음 처리: 주문접수는 출고 지시, 그 외는 해당 화면으로 이동 */
function OrderNextAction({ orderId, status, onDone }: OrderNextActionProps) {
  if (status === 'RECEIVED') {
    return <ReleaseButton orderId={orderId} status={status} onDone={onDone} />;
  }
  const next = ORDER_NEXT_STEP[status];
  if (!next) {
    return <span className="text-sm text-gray-400">처리가 끝난 주문입니다.</span>;
  }
  return (
    <Link to={next.to} className={BUTTON_PRIMARY}>
      {next.label} →
    </Link>
  );
}

export default OrderNextAction;
