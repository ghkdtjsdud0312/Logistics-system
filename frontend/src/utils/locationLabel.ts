import { Option } from '@/types/ui';

/** 위치 ID에 해당하는 표시 문구('창고 / 구역 / 위치'). 없으면 '-' */
export function locationLabel(options: Option[], locationId: number | null): string {
  if (locationId === null) return '-';
  return options.find((o) => o.value === String(locationId))?.label ?? '-';
}
