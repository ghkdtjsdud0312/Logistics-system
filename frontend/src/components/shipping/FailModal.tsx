import { useState } from 'react';
import Modal from '@/components/common/Modal';
import { FAIL_REASON_LABEL } from '@/constants/failReason';
import { BUTTON_PRIMARY, INPUT_CLASS } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { failShipment } from '@/services/deliveryService';
import { FailReason, Shipment } from '@/types/shipment';

interface FailModalProps {
  shipment: Shipment;
  onClose: () => void;
  onDone: () => void;
}

/** 배송 실패 사유 선택 모달 */
function FailModal({ shipment, onClose, onDone }: FailModalProps) {
  const [reason, setReason] = useState<FailReason>('CUSTOMER_ABSENT');
  const [detail, setDetail] = useState('');
  const { submitting, run } = useSubmit();

  const submit = async () => {
    if (await run(() => failShipment(shipment.id, reason, detail), '배송 실패로 처리했습니다.'))
      onDone();
  };

  return (
    <Modal title={`배송 실패 처리 · ${shipment.orderNo}`} onClose={onClose}>
      <div className="space-y-2 text-sm">
        {(Object.keys(FAIL_REASON_LABEL) as FailReason[]).map((key) => (
          <label key={key} className="flex items-center gap-2">
            <input type="radio" checked={reason === key} onChange={() => setReason(key)} />
            {FAIL_REASON_LABEL[key]}
          </label>
        ))}
        <textarea
          className={INPUT_CLASS}
          rows={3}
          placeholder="상세내용"
          value={detail}
          onChange={(e) => setDetail(e.target.value)}
        />
        <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
          배송실패 처리
        </button>
      </div>
    </Modal>
  );
}

export default FailModal;
