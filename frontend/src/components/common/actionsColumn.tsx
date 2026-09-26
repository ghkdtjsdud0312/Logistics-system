import RowActions from '@/components/common/RowActions';
import { Column } from '@/types/ui';

/** 표 맨 끝의 '관리'(수정·삭제) 열을 만든다. */
export function actionsColumn<T>(onEdit: (row: T) => void, onDelete: (row: T) => void): Column<T> {
  return {
    header: '관리',
    render: (row) => <RowActions onEdit={() => onEdit(row)} onDelete={() => onDelete(row)} />,
  };
}
