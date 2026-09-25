import { useLocation } from 'react-router-dom';
import { MENU } from '@/constants/menu';
import SidebarLink from './SidebarLink';

/** 공통 사이드바: 7개 메뉴 그룹. 현재 화면이 속한 그룹 제목도 강조한다. */
function Sidebar() {
  const { pathname } = useLocation();

  return (
    <aside className="w-56 shrink-0 overflow-y-auto border-r border-gray-200 bg-white">
      <nav className="flex flex-col gap-1 py-4 pr-3">
        {MENU.map((group) =>
          group.to ? (
            <SidebarLink key={group.label} to={group.to} label={group.label} />
          ) : (
            <div key={group.label} className="mt-3 flex flex-col gap-1">
              <span
                className={`pl-4 text-xs font-bold ${
                  group.children?.some((c) => pathname.startsWith(c.to))
                    ? 'text-primary'
                    : 'text-gray-400'
                }`}
              >
                {group.label}
              </span>
              {group.children?.map((child) => (
                <SidebarLink key={child.to} to={child.to} label={child.label} nested />
              ))}
            </div>
          ),
        )}
      </nav>
    </aside>
  );
}

export default Sidebar;
