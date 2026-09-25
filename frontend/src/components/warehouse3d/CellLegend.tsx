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
    </ul>
  );
}

export default CellLegend;
