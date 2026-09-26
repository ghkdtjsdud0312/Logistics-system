import { ReactNode } from 'react';

export type Tone = 'gray' | 'blue' | 'green' | 'red' | 'yellow';

export interface Column<T> {
  header: string;
  render: (row: T) => ReactNode;
  /** 이전 호환용. 모든 표는 중앙정렬이라 값은 무시된다. */
  align?: 'right' | 'center';
}

export interface Option {
  value: string;
  label: string;
}
