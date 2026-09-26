import { NavLink } from 'react-router-dom';

interface SidebarLinkProps {
  to: string;
  label: string;
  nested?: boolean;
  /** 하위 메뉴(창고·위치관리의 구조/3D 등)는 한 단계 더 들여 쓴다. */
  deep?: boolean;
  end?: boolean;
}

const INDENT = (nested: boolean, deep: boolean) => (deep ? 'pl-9' : nested ? 'pl-5' : 'pl-3');

/** 사이드바 링크 한 줄. 현재 메뉴는 왼쪽 굵은 막대와 진한 배경으로 강조한다. */
function SidebarLink({ to, label, nested = false, deep = false, end = false }: SidebarLinkProps) {
  return (
    <NavLink
      to={to}
      end={end || to === '/'}
      className={({ isActive }) =>
        `border-l-4 py-2 pr-3 text-sm transition-colors ${INDENT(nested, deep)} ${
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
