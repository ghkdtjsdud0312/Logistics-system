import { ReactNode } from 'react';

interface SectionProps {
  title: string;
  children: ReactNode;
  /** 제목 오른쪽에 둘 바로가기(예: 상세보기) */
  action?: ReactNode;
}

/** 제목이 있는 화면 구획 */
function Section({ title, children, action }: SectionProps) {
  return (
    <section className="mb-6">
      <div className="mb-2 flex items-center justify-between">
        <h2 className="text-sm font-semibold text-gray-700">{title}</h2>
        {action}
      </div>
      {children}
    </section>
  );
}

export default Section;
