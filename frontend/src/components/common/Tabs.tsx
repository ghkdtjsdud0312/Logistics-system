import { Option } from '@/types/ui';

interface TabsProps {
  tabs: Option[];
  active: string;
  onChange: (key: string) => void;
}

/** 탭 전환 버튼 */
function Tabs({ tabs, active, onChange }: TabsProps) {
  return (
    <div className="mb-4 flex gap-1 border-b border-gray-200">
      {tabs.map((t) => (
        <button
          key={t.value}
          onClick={() => onChange(t.value)}
          className={`px-4 py-2 text-sm font-medium ${
            active === t.value ? 'border-b-2 border-primary text-primary' : 'text-gray-500'
          }`}
        >
          {t.label}
        </button>
      ))}
    </div>
  );
}

export default Tabs;
