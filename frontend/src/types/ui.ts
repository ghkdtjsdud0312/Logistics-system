import { ReactNode } from 'react';

export type Tone = 'gray' | 'blue' | 'green' | 'red' | 'yellow';

export interface Column<T> {
  header: string;
  render: (row: T) => ReactNode;
}

export interface Option {
  value: string;
  label: string;
}
