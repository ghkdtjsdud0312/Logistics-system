import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { AuditLog, AuditSearch } from '@/types/audit';

const clean = (v: string) => v || undefined;

export const getAuditLogs = (s: AuditSearch) =>
  unwrap<AuditLog[]>(
    apiClient.get('/audit-logs', {
      params: {
        from: clean(s.from),
        to: clean(s.to),
        actor: clean(s.actor),
        target: clean(s.target),
        action: clean(s.action),
      },
    }),
  );
