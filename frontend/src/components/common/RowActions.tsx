import { BUTTON_SECONDARY } from '@/constants/styles';

interface RowActionsProps {
  onEdit?: () => void;
  onDelete: () => void;
}

/** 표 행/카드의 수정·삭제 버튼 */
function RowActions({ onEdit, onDelete }: RowActionsProps) {
  return (
    <div className="flex justify-center gap-1">
      {onEdit && (
        <button className={`${BUTTON_SECONDARY} px-2 py-0.5 text-xs`} onClick={onEdit}>
          수정
        </button>
      )}
      <button
        className="rounded-md border border-red-200 px-2 py-0.5 text-xs text-red-600 hover:bg-red-50"
        onClick={onDelete}
      >
        삭제
      </button>
    </div>
  );
}

export default RowActions;
