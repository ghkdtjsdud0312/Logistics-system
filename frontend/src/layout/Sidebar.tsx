import { MENU } from '@/constants/menu';
import SidebarLink from './SidebarLink';

/** 공통 사이드바: 7개 메뉴 그룹 */
function Sidebar() {
  return (
    <aside className="w-56 shrink-0 overflow-y-auto border-r border-gray-200 bg-white">
      <nav className="flex flex-col gap-1 p-4">
        {MENU.map((group) =>
          group.to ? (
            <SidebarLink key={group.label} to={group.to} label={group.label} />
          ) : (
            <div key={group.label} className="mt-2 flex flex-col gap-1">
              <span className="px-3 text-xs font-semibold text-gray-400">{group.label}</span>
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
