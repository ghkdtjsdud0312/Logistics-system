import { InboundStatus } from '@/types/inbound';
import { Tone } from '@/types/ui';

export const INBOUND_STATUS_LABEL: Record<InboundStatus, string> = {
  EXPECTED: '입고예정',
  RECEIVED: '입고완료',
  PUTAWAY_WAITING: '적치대기',
  PUTAWAY_DONE: '적치완료',
};

export const INBOUND_STATUS_TONE: Record<InboundStatus, Tone> = {
  EXPECTED: 'gray',
  RECEIVED: 'blue',
  PUTAWAY_WAITING: 'yellow',
  PUTAWAY_DONE: 'green',
};
