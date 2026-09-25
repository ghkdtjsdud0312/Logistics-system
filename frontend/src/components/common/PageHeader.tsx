import { Link } from 'react-router-dom';
import { NextStep } from '@/types/nextStep';

interface PageHeaderProps {
  title: string;
  description?: string;
  /** 업무 흐름상 다음 화면 안내 */
  next?: NextStep;
}

/** 페이지 상단 제목 영역. 다음 단계가 있으면 오른쪽에 바로가기를 보여 준다. */
function PageHeader({ title, description, next }: PageHeaderProps) {
  return (
    <div className="mb-4 flex items-start justify-between gap-4">
      <div>
        <h1 className="text-xl font-semibold text-gray-900">{title}</h1>
        {description && <p className="mt-1 text-sm text-gray-500">{description}</p>}
      </div>
      {next && (
        <Link
          to={next.to}
          className="shrink-0 rounded-md border border-primary/30 bg-primary/5 px-3 py-1.5 text-sm text-primary hover:bg-primary/10"
        >
          다음 단계 → {next.label}
        </Link>
      )}
    </div>
  );
}

export default PageHeader;
