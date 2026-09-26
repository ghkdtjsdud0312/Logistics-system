import { PROGRESS_STAGES } from '@/constants/progressStage';

interface ProgressChartProps {
  progress: Record<string, number>;
  /** 선택된 단계 키. onSelect와 함께 주면 막대를 눌러 단계를 고를 수 있다. */
  selected?: string;
  onSelect?: (key: string) => void;
}

/** 물류 진행 현황: 단계별 현재 건수 막대 */
function ProgressChart({ progress, selected, onSelect }: ProgressChartProps) {
  const max = Math.max(1, ...PROGRESS_STAGES.map((s) => progress[s.key] ?? 0));
  return (
    <div className="space-y-1 rounded-md border border-gray-200 bg-white p-2 text-sm">
      {PROGRESS_STAGES.map((s) => {
        const value = progress[s.key] ?? 0;
        return (
          <button
            key={s.key}
            type="button"
            disabled={!onSelect}
            onClick={() => onSelect?.(s.key)}
            className={`flex w-full items-center gap-3 rounded px-2 py-1 text-left ${
              selected === s.key ? 'bg-primary/10 ring-1 ring-primary' : ''
            } ${onSelect ? 'hover:bg-gray-50' : 'cursor-default'}`}
          >
            <span className="w-10 text-gray-600">{s.label}</span>
            <div className="h-3 flex-1 rounded bg-gray-100">
              <div
                className="h-3 rounded bg-primary"
                style={{ width: `${(value / max) * 100}%` }}
              />
            </div>
            <span className="w-8 text-right font-medium">{value}</span>
          </button>
        );
      })}
    </div>
  );
}

export default ProgressChart;
