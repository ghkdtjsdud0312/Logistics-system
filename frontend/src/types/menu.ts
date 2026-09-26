export interface MenuChild {
  label: string;
  /** 하위 메뉴만 있는 항목(제목 역할)은 생략한다. */
  to?: string;
  children?: MenuChild[];
  /** 경로가 정확히 같을 때만 활성화한다(하위 경로를 가진 메뉴용). */
  end?: boolean;
}

export interface MenuGroup {
  label: string;
  to?: string;
  children?: MenuChild[];
}
