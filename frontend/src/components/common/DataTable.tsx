import { Column } from '@/types/ui';

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  loading?: boolean;
}

/** 공통 목록 테이블 */
function DataTable<T>({ columns, rows, rowKey, loading = false }: DataTableProps<T>) {
  return (
    <div className="overflow-x-auto rounded-md border border-gray-200 bg-white">
      <table className="min-w-full text-sm">
        <thead className="bg-gray-50 text-left text-xs text-gray-500">
          <tr>
            {columns.map((c) => (
              <th key={c.header} className="px-3 py-2 font-medium">
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={rowKey(row)} className="border-t border-gray-100">
              {columns.map((c) => (
                <td key={c.header} className="px-3 py-2">
                  {c.render(row)}
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
