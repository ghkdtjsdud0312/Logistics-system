import { useState } from 'react';
import SidebarLink from './SidebarLink';
import { MenuChild } from '@/types/menu';

/** 하위 메뉴가 있는 항목: 제목을 누르면 하위 링크를 접고 펼 수 있다. */
function SidebarSubMenu({ item }: { item: MenuChild }) {
  const [open, setOpen] = useState(true);

  return (
    <div className="flex flex-col gap-1">
      <button
        type="button"
        aria-expanded={open}
        onClick={() => setOpen(!open)}
        className="flex items-center justify-between py-1 pl-5 pr-3 text-left text-sm text-gray-500 hover:text-gray-700"
      >
        {item.label}
        <span className="text-xs">{open ? '▾' : '▸'}</span>
      </button>
      {open &&
        item.children?.map((sub) => (
          <SidebarLink key={sub.to} to={sub.to!} label={sub.label} end={sub.end} nested deep />
        ))}
    </div>
  );
}

export default SidebarSubMenu;
