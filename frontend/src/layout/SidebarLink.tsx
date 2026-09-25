import { NavLink } from 'react-router-dom';

interface SidebarLinkProps {
  to: string;
  label: string;
  nested?: boolean;
}

/** 사이드바 링크 한 줄 */
function SidebarLink({ to, label, nested = false }: SidebarLinkProps) {
  return (
    <NavLink
      to={to}
      end={to === '/'}
      className={({ isActive }) =>
        `rounded-md py-2 text-sm font-medium transition-colors ${nested ? 'pl-6 pr-3' : 'px-3'} ${
          isActive ? 'bg-primary/10 text-primary' : 'text-gray-600 hover:bg-gray-100'
        }`
      }
    >
      {label}
    </NavLink>
  );
}

export default SidebarLink;
