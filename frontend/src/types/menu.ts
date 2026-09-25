export interface MenuChild {
  label: string;
  to: string;
}

export interface MenuGroup {
  label: string;
  to?: string;
  children?: MenuChild[];
}
