import { useState } from 'react';
import { toast } from 'sonner';
import { getErrorMessage } from '@/utils/errorMessage';

/** 등록/변경 요청을 실행하고 성공·실패 토스트를 띄운다. 성공 시 true를 반환한다. */
export function useSubmit() {
  const [submitting, setSubmitting] = useState(false);

  const run = async (action: () => Promise<unknown>, successMessage: string): Promise<boolean> => {
    setSubmitting(true);
    try {
      await action();
      toast.success(successMessage);
      return true;
    } catch (e) {
      toast.error(getErrorMessage(e));
      return false;
    } finally {
      setSubmitting(false);
    }
  };

  return { submitting, run };
}
