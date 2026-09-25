import { ReactNode, useState } from 'react';

interface FormCardProps {
  title: string;
  children: ReactNode;
  defaultOpen?: boolean;
}

/** 접었다 펼 수 있는 등록 폼 카드. 기본은 접어 두어 목록이 먼저 보이게 한다. */
function FormCard({ title, children, defaultOpen = false }: FormCardProps) {
  const [open, setOpen] = useState(defaultOpen);

  return (
    <section className="mb-4 rounded-md border border-gray-200 bg-white">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="flex w-full items-center justify-between px-4 py-2 text-left text-sm font-semibold text-gray-700"
      >
        <span>
          {open ? '▾' : '▸'} {title}
        </span>
        <span className="text-xs font-normal text-gray-400">{open ? '접기' : '＋ 열기'}</span>
      </button>
      {open && <div className="border-t border-gray-100 p-4">{children}</div>}
    </section>
  );
}

export default FormCard;
