import { NavLink } from 'react-router-dom';

interface SidebarLinkProps {
  to: string;
  label: string;
  nested?: boolean;
}

/** 사이드바 링크 한 줄. 현재 메뉴는 왼쪽 굵은 막대와 진한 배경으로 강조한다. */
function SidebarLink({ to, label, nested = false }: SidebarLinkProps) {
  return (
    <NavLink
      to={to}
      end={to === '/'}
      className={({ isActive }) =>
        `border-l-4 py-2 pr-3 text-sm transition-colors ${nested ? 'pl-5' : 'pl-3'} ${
          isActive
            ? 'border-primary bg-primary/10 font-semibold text-primary'
            : 'border-transparent text-gray-600 hover:bg-gray-100'
        }`
      }
    >
      {label}
    </NavLink>
  );
}

export default SidebarLink;
