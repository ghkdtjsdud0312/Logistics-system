import { useState } from 'react';

/** 문자열 입력 폼 상태 관리 */
export function useForm<T extends Record<string, string>>(initial: T) {
  const [values, setValues] = useState<T>(initial);
  const setField = (name: keyof T, value: string) =>
    setValues((prev) => ({ ...prev, [name]: value }));
  const reset = () => setValues(initial);
  return { values, setField, reset };
}
