import { ReturnStatus } from '@/types/return';
import { Tone } from '@/types/ui';

export const RETURN_STATUS_LABEL: Record<ReturnStatus, string> = {
  REQUESTED: '회수요청',
  COLLECTING: '회수중',
  COLLECTED: '회수완료',
  RETURN_RECEIVED: '반품입고',
  COMPLETED: '처리완료',
};

export const RETURN_STATUS_TONE: Record<ReturnStatus, Tone> = {
  REQUESTED: 'yellow',
  COLLECTING: 'blue',
  COLLECTED: 'blue',
  RETURN_RECEIVED: 'blue',
  COMPLETED: 'green',
};
