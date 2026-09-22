import { NavLink } from 'react-router-dom';

const menuItems = [
  { to: '/', label: '대시보드' },
  { to: '/inbound', label: '입고 관리' },
  { to: '/outbound', label: '출고 관리' },
  { to: '/dispatch', label: '배차 최적화' },
];

/**
 * 공통 사이드바
 * - 도메인별 메뉴 내비게이션
 */
function Sidebar() {
  return (
    <aside className="w-56 shrink-0 border-r border-gray-200 bg-white">
      <nav className="flex flex-col gap-1 p-4">
        {menuItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) =>
              `rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary/10 text-primary'
                  : 'text-gray-600 hover:bg-gray-100'
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

export default Sidebar;
