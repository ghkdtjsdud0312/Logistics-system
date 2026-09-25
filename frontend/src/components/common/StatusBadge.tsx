import { Tone } from '@/types/ui';

const TONE_CLASS: Record<Tone, string> = {
  gray: 'bg-gray-100 text-gray-700',
  blue: 'bg-blue-100 text-blue-700',
  green: 'bg-green-100 text-green-700',
  red: 'bg-red-100 text-red-700',
  yellow: 'bg-yellow-100 text-yellow-800',
};

/** 상태 Badge */
function StatusBadge({ label, tone }: { label: string; tone: Tone }) {
  return (
    <span
      className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${TONE_CLASS[tone]}`}
    >
      {label}
    </span>
  );
}

export default StatusBadge;
