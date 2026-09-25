const STAGES: { key: string; label: string }[] = [
  { key: 'ORDERS', label: '주문' },
  { key: 'PICKING', label: '피킹' },
  { key: 'PACKING', label: '포장' },
  { key: 'LOADING', label: '상차' },
  { key: 'DELIVERY', label: '배송' },
];

/** 물류 진행 현황: 단계별 현재 건수 막대 */
function ProgressChart({ progress }: { progress: Record<string, number> }) {
  const max = Math.max(1, ...STAGES.map((s) => progress[s.key] ?? 0));
  return (
    <div className="space-y-2 rounded-md border border-gray-200 bg-white p-4 text-sm">
      {STAGES.map((s) => {
        const value = progress[s.key] ?? 0;
        return (
          <div key={s.key} className="flex items-center gap-3">
            <span className="w-10 text-gray-600">{s.label}</span>
            <div className="h-3 flex-1 rounded bg-gray-100">
              <div
                className="h-3 rounded bg-primary"
                style={{ width: `${(value / max) * 100}%` }}
              />
            </div>
            <span className="w-8 text-right font-medium">{value}</span>
          </div>
        );
      })}
    </div>
  );
}

export default ProgressChart;
