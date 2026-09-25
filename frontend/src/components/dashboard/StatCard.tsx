import { useFlashOnChange } from '@/hooks/useFlashOnChange';

interface StatCardProps {
  label: string;
  value: number;
  /** Tailwind 색 계열 이름 (gray, yellow, blue, green, red) */
  tone: 'gray' | 'yellow' | 'blue' | 'green' | 'red';
}

const TONE_CLASS = {
  gray: 'border-gray-300 bg-white text-gray-900',
  yellow: 'border-yellow-300 bg-yellow-50 text-yellow-800',
  blue: 'border-blue-300 bg-blue-50 text-blue-800',
  green: 'border-green-300 bg-green-50 text-green-800',
  red: 'border-red-300 bg-red-50 text-red-700',
} as const;

/** 숫자가 바뀌면 잠깐 확대·강조되는 현황 카드 */
function StatCard({ label, value, tone }: StatCardProps) {
  const flashing = useFlashOnChange(value);

  return (
    <div
      className={`rounded-md border-2 p-3 text-center transition-all duration-300 ${TONE_CLASS[tone]} ${
        flashing ? 'scale-105 shadow-lg ring-2 ring-primary' : ''
      }`}
    >
      <div className="text-xs font-medium opacity-80">{label}</div>
      <div className="text-3xl font-extrabold tabular-nums">{value}</div>
    </div>
  );
}

export default StatCard;
