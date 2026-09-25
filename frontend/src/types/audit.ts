export interface AuditLog {
  occurredAt: string;
  actor: string;
  targetType: string;
  targetNo: string;
  action: string;
  description: string;
  fromStatus: string | null;
  toStatus: string;
}

export type AuditSearch = {
  from: string;
  to: string;
  actor: string;
  target: string;
  action: string;
};
