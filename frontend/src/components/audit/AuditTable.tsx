import DataTable from '@/components/common/DataTable';
import { AuditLog } from '@/types/audit';
import { Column } from '@/types/ui';
import { statusLabel } from '@/utils/auditStatus';
import { formatDateTime } from '@/utils/format';

const COLUMNS: Column<AuditLog>[] = [
  { header: '시간', render: (l) => formatDateTime(l.occurredAt) },
  { header: '사용자', render: (l) => l.actor },
  { header: '대상', render: (l) => l.targetNo },
  { header: '작업', render: (l) => l.description },
  { header: '이전상태', render: (l) => statusLabel(l.targetType, l.fromStatus) },
  { header: '변경상태', render: (l) => statusLabel(l.targetType, l.toStatus) },
];

function AuditTable({ logs, loading }: { logs: AuditLog[]; loading: boolean }) {
  return (
    <DataTable
      columns={COLUMNS}
      rows={logs}
      rowKey={(l) => `${l.occurredAt}-${l.targetNo}-${l.action}`}
      loading={loading}
    />
  );
}

export default AuditTable;
