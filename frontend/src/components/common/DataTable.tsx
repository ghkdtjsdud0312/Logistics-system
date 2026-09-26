import { ReactNode } from 'react';
import { Column } from '@/types/ui';

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  loading?: boolean;
  /** true면 부모 높이를 채우고 표 안에서만 세로 스크롤된다(헤더 고정). */
  fill?: boolean;
}

/** 글자·숫자 셀은 길면 ...으로 줄이고 마우스를 올리면 전체를 보여 준다. */
const cell = (value: ReactNode) =>
  typeof value === 'string' || typeof value === 'number' ? (
    <span className="inline-block max-w-[16rem] truncate align-bottom" title={String(value)}>
      {value}
    </span>
  ) : (
    value
  );

/** 공통 목록 테이블: 모든 셀 중앙정렬, 긴 글자 말줄임, 행 hover 강조 */
function DataTable<T>({ columns, rows, rowKey, loading = false, fill = false }: DataTableProps<T>) {
  return (
    <div
      className={`rounded-md border border-gray-200 bg-white ${
        fill ? 'min-h-0 flex-1 overflow-auto' : 'overflow-x-auto'
      }`}
    >
      <table className="min-w-full text-center text-sm tabular-nums">
        <thead className="sticky top-0 bg-gray-50 text-xs text-gray-500">
          <tr>
            {columns.map((c) => (
              <th key={c.header} className="whitespace-nowrap px-3 py-2 font-medium">
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr
              key={rowKey(row)}
              className="border-t border-gray-100 transition-colors hover:bg-blue-50/50"
            >
              {columns.map((c) => (
                <td key={c.header} className="px-3 py-2">
                  {cell(c.render(row))}
                </td>
              ))}
            </tr>
          ))}
          {rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-3 py-6 text-center text-gray-400">
                {loading ? '불러오는 중...' : '데이터가 없습니다.'}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default DataTable;
