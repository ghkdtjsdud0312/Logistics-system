/** 완료/전체 진행률 막대 */
function ProgressBar({ value, total }: { value: number; total: number }) {
  const percent = total === 0 ? 0 : Math.round((value / total) * 100);
  return (
    <div className="h-2 w-full rounded bg-gray-200">
      <div className="h-2 rounded bg-primary" style={{ width: `${percent}%` }} />
    </div>
  );
}

export default ProgressBar;
