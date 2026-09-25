import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { AUDIT_ACTION_OPTIONS } from '@/constants/auditAction';
import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { AuditSearch } from '@/types/audit';

const EMPTY: AuditSearch = { from: '', to: '', actor: '', target: '', action: '' };

/** 감사로그 검색: 기간, 사용자, 대상, 작업 */
function AuditSearchBar({ onSearch }: { onSearch: (search: AuditSearch) => void }) {
  const { values, setField, reset } = useForm(EMPTY);

  return (
    <div className="mb-4 flex flex-wrap items-end gap-3">
      <TextField
        label="기간(시작)"
        type="date"
        value={values.from}
        onChange={(v) => setField('from', v)}
      />
      <TextField
        label="기간(끝)"
        type="date"
        value={values.to}
        onChange={(v) => setField('to', v)}
      />
      <TextField label="사용자" value={values.actor} onChange={(v) => setField('actor', v)} />
      <TextField
        label="대상"
        placeholder="ORD-001"
        value={values.target}
        onChange={(v) => setField('target', v)}
      />
      <SelectField
        label="작업"
        value={values.action}
        onChange={(v) => setField('action', v)}
        options={AUDIT_ACTION_OPTIONS}
        placeholder="전체"
      />
      <button className={BUTTON_PRIMARY} onClick={() => onSearch(values)}>
        검색
      </button>
      <button
        className={BUTTON_SECONDARY}
        onClick={() => {
          reset();
          onSearch(EMPTY);
        }}
      >
        초기화
      </button>
    </div>
  );
}

export default AuditSearchBar;
