import { TONE_CLASS } from '@/constants/tone';
import { Tone } from '@/types/ui';

const SIZE_CLASS = { md: 'px-2 py-0.5 text-xs', lg: 'px-3 py-1 text-sm' } as const;

interface StatusBadgeProps {
  label: string;
  tone: Tone;
  size?: keyof typeof SIZE_CLASS;
}

/** 상태 Badge. 상세 화면 제목에는 lg를 쓴다. */
function StatusBadge({ label, tone, size = 'md' }: StatusBadgeProps) {
  return (
    <span
      className={`inline-block rounded-full font-medium ${SIZE_CLASS[size]} ${TONE_CLASS[tone]}`}
    >
      {label}
    </span>
  );
}

export default StatusBadge;
