import { ReactNode } from 'react';

export type Tone = 'gray' | 'blue' | 'green' | 'red' | 'yellow';

export interface Column<T> {
  header: string;
  render: (row: T) => ReactNode;
  /** 숫자 열은 'right'로 두면 헤더와 값이 오른쪽 정렬된다. */
  align?: 'right' | 'center';
}

export interface Option {
  value: string;
  label: string;
}
