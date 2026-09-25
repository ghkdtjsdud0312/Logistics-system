import { ReactNode } from 'react';

interface DetailCardProps {
  title: string;
  badge: ReactNode;
  action?: ReactNode;
  fields: [string, ReactNode][];
}

/** 상세 화면 상단: 번호 + 큰 상태 Badge + 처리 버튼을 한 줄에, 항목은 아래 격자로 보여 준다. */
function DetailCard({ title, badge, action, fields }: DetailCardProps) {
  return (
    <div className="mb-4 rounded-md border border-gray-200 bg-white p-4 text-sm">
      <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="text-2xl font-bold text-gray-900">{title}</span>
          {badge}
        </div>
        {action}
      </div>
      <dl className="grid grid-cols-2 gap-3 text-gray-700 md:grid-cols-3">
        {fields.map(([label, value]) => (
          <div key={label}>
            <dt className="text-xs text-gray-400">{label}</dt>
            <dd>{value}</dd>
          </div>
        ))}
      </dl>
    </div>
  );
}

export default DetailCard;
