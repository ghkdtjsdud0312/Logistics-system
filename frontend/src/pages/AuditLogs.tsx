import { useState } from 'react';
import AuditSearchBar from '@/components/audit/AuditSearchBar';
import AuditTable from '@/components/audit/AuditTable';
import PageHeader from '@/components/common/PageHeader';
import { useFetch } from '@/hooks/useFetch';
import { getAuditLogs } from '@/services/auditService';
import { AuditSearch } from '@/types/audit';

const EMPTY: AuditSearch = { from: '', to: '', actor: '', target: '', action: '' };

/** 감사로그: 누가 언제 어떤 상태를 바꿨는지 */
function AuditLogsPage() {
  const [search, setSearch] = useState<AuditSearch>(EMPTY);
  const logs = useFetch(() => getAuditLogs(search), JSON.stringify(search));

  return (
    <>
      <PageHeader
        title="감사로그"
        description="모든 상태 변경 이력을 최신순으로 조회합니다(최대 100건)."
      />
      <AuditSearchBar onSearch={setSearch} />
      <AuditTable logs={logs.data ?? []} loading={logs.loading} />
    </>
  );
}

export default AuditLogsPage;
