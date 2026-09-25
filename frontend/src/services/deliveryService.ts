import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { DeliveryBoard } from '@/types/dispatch';
import { FailReason, Shipment } from '@/types/shipment';

export const getDeliveryBoard = () => unwrap<DeliveryBoard[]>(apiClient.get('/delivery-status'));

export const deliverShipment = (id: number, deliveredQty: number) =>
  unwrap<Shipment>(apiClient.patch(`/shipments/${id}/deliver`, { deliveredQty }));

export const failShipment = (id: number, reason: FailReason, detail: string) =>
  unwrap<Shipment>(apiClient.patch(`/shipments/${id}/fail`, { reason, detail }));
