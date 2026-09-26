import SidebarLink from './SidebarLink';
import SidebarSubMenu from './SidebarSubMenu';
import { MenuChild } from '@/types/menu';

/** 그룹의 메뉴 항목들. 하위 메뉴가 있는 항목은 접고 펼 수 있다. */
function SidebarChildren({ items }: { items: MenuChild[] }) {
  return (
    <>
      {items.map((child) =>
        child.children ? (
          <SidebarSubMenu key={child.label} item={child} />
        ) : (
          <SidebarLink key={child.to} to={child.to!} label={child.label} end={child.end} nested />
        ),
      )}
    </>
  );
}

export default SidebarChildren;
