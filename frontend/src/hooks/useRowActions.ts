import { useState } from 'react';
import { useSubmit } from '@/hooks/useSubmit';

/** 수정 대상·삭제 대상 상태와 삭제 실행을 묶는다. 삭제 성공 시 onDone으로 목록을 다시 불러온다. */
export function useRowActions<T>(remove: (row: T) => Promise<unknown>, onDone: () => void) {
  const [editing, setEditing] = useState<T | null>(null);
  const [deleting, setDeleting] = useState<T | null>(null);
  const { submitting, run } = useSubmit();

  const confirmDelete = async () => {
    if (!deleting) return;
    if (await run(() => remove(deleting), '삭제했습니다.')) {
      setDeleting(null);
      onDone();
    }
  };

  return { editing, setEditing, deleting, setDeleting, confirmDelete, submitting };
}
