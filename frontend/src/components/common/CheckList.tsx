interface CheckItem {
  id: number;
  title: string;
  detail: string;
}

interface CheckListProps {
  items: CheckItem[];
  selected: number[];
  onToggle: (id: number) => void;
  emptyText: string;
}

/** 체크박스 선택 목록 */
function CheckList({ items, selected, onToggle, emptyText }: CheckListProps) {
  if (items.length === 0) {
    return (
      <p className="rounded-md border border-gray-200 bg-white p-4 text-sm text-gray-400">
        {emptyText}
      </p>
    );
  }
  return (
    <ul className="divide-y divide-gray-100 rounded-md border border-gray-200 bg-white text-sm">
      {items.map((item) => (
        <li key={item.id}>
          <label className="flex cursor-pointer items-center gap-3 px-3 py-2">
            <input
              type="checkbox"
              checked={selected.includes(item.id)}
              onChange={() => onToggle(item.id)}
            />
            <span className="font-medium">{item.title}</span>
            <span className="text-gray-500">{item.detail}</span>
          </label>
        </li>
      ))}
    </ul>
  );
}

export default CheckList;
