import { ReactNode } from 'react';

interface ModalProps {
  title: string;
  onClose: () => void;
  children: ReactNode;
}

/** 단순 모달 */
function Modal({ title, onClose, children }: ModalProps) {
  return (
    <div className="fixed inset-0 z-10 flex items-center justify-center bg-black/40">
      <div className="w-96 rounded-lg bg-white p-5 shadow-lg">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-semibold">{title}</h3>
          <button onClick={onClose} className="text-gray-400">
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default Modal;
