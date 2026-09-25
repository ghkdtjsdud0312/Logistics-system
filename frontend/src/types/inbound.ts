export type InboundStatus = 'EXPECTED' | 'RECEIVED' | 'PUTAWAY_WAITING' | 'PUTAWAY_DONE';

export interface Inbound {
  id: number;
  inboundNo: string;
  partnerName: string;
  productId: number;
  productCode: string;
  productName: string;
  quantity: number;
  inboundDate: string;
  status: InboundStatus;
  locationId: number | null;
}

export interface InboundCreate {
  partnerName: string;
  productId: number;
  quantity: number;
  inboundDate: string;
}
