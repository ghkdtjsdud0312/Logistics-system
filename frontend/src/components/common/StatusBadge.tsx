import { Tone } from '@/types/ui';

const TONE_CLASS: Record<Tone, string> = {
  gray: 'bg-gray-100 text-gray-700',
  blue: 'bg-blue-100 text-blue-700',
  green: 'bg-green-100 text-green-700',
  red: 'bg-red-100 text-red-700',
  yellow: 'bg-yellow-100 text-yellow-800',
};

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
