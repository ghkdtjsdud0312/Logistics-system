import { Option } from '@/types/ui';

/** { KEY: '라벨' } 형태의 상수를 select 옵션 배열로 바꾼다. */
export function toOptions(labels: Record<string, string>): Option[] {
  return Object.entries(labels).map(([value, label]) => ({ value, label }));
}
