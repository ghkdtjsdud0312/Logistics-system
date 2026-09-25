import { useState } from 'react';
import FailModal from '@/components/shipping/FailModal';
import { BUTTON_PRIMARY, BUTTON_SECONDARY, INPUT_CLASS } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { deliverShipment } from '@/services/deliveryService';
import { Shipment } from '@/types/shipment';

/** 인도수량을 입력해 배송완료 하거나 실패 모달을 연다. */
function DeliveryResultActions({
  shipment,
  onChanged,
}: {
  shipment: Shipment;
  onChanged: () => void;
}) {
  const [qty, setQty] = useState(String(shipment.quantity));
  const [failing, setFailing] = useState(false);
  const { submitting, run } = useSubmit();

  return (
    <div className="flex items-center gap-2">
      <input
        className={`${INPUT_CLASS} w-20`}
        type="number"
        value={qty}
        onChange={(e) => setQty(e.target.value)}
      />
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting}
        onClick={async () => {
          if (await run(() => deliverShipment(shipment.id, Number(qty)), '배송완료 처리했습니다.'))
            onChanged();
        }}
      >
        배송완료
      </button>
      <button className={BUTTON_SECONDARY} onClick={() => setFailing(true)}>
        배송실패
      </button>
      {failing && (
        <FailModal
          shipment={shipment}
          onClose={() => setFailing(false)}
          onDone={() => {
            setFailing(false);
            onChanged();
          }}
        />
      )}
    </div>
  );
}

export default DeliveryResultActions;
