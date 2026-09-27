import { CELL_STATE_COLOR, CELL_STATE_LABEL, toCssColor } from '@/constants/cellState';
import { CellState } from '@/types/warehouse3d';

/** 색상 범례 */
function CellLegend() {
  return (
    <ul className="flex flex-wrap gap-4 text-xs text-gray-600">
      {(Object.keys(CELL_STATE_LABEL) as CellState[]).map((state) => (
        <li key={state} className="flex items-center gap-1.5">
          <span
            className="inline-block h-3 w-3 rounded-sm"
            style={{ backgroundColor: toCssColor(CELL_STATE_COLOR[state]) }}
          />
          {CELL_STATE_LABEL[state]}
        </li>
      ))}
      <li className="flex items-center gap-1.5">
        <span className="inline-block h-3 w-3 rounded-sm border-2 border-cyan-400 bg-white" />
        최근 변경(24시간 이내)
      </li>
      <li className="flex items-center gap-1.5">
        <span className="inline-block h-3 w-3 rounded-sm bg-amber-400" />
        피킹 진행 중
      </li>
    </ul>
  );
}

export default CellLegend;
