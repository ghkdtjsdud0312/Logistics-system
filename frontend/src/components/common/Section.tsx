import { ReactNode } from 'react';

/** 제목이 있는 화면 구획 */
function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="mb-6">
      <h2 className="mb-2 text-sm font-semibold text-gray-700">{title}</h2>
      {children}
    </section>
  );
}

export default Section;
