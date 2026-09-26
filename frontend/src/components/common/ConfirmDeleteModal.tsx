import Modal from '@/components/common/Modal';
import { BUTTON_SECONDARY } from '@/constants/styles';

interface ConfirmDeleteModalProps {
  /** 삭제 대상 이름 */
  label: string;
  busy: boolean;
  onConfirm: () => void;
  onClose: () => void;
}

/** 삭제 확인 모달 */
function ConfirmDeleteModal({ label, busy, onConfirm, onClose }: ConfirmDeleteModalProps) {
  return (
    <Modal title="삭제 확인" onClose={onClose}>
      <p className="break-words text-sm text-gray-700">“{label}”을(를) 삭제할까요?</p>
      <div className="mt-4 flex justify-end gap-2">
        <button className={BUTTON_SECONDARY} onClick={onClose}>
          취소
        </button>
        <button
          disabled={busy}
          onClick={onConfirm}
          className="rounded-md bg-red-600 px-3 py-1.5 text-sm text-white hover:bg-red-700 disabled:opacity-50"
        >
          삭제
        </button>
      </div>
    </Modal>
  );
}

export default ConfirmDeleteModal;
