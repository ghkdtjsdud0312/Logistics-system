import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createDriver } from '@/services/driverService';

const INITIAL = { driverCode: '', name: '', phone: '' };

/** 기사 등록 폼 */
function DriverForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const { submitting, run } = useSubmit();

  const submit = async () => {
    if (await run(() => createDriver(values), '기사를 등록했습니다.')) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-end gap-2">
      <TextField
        label="기사ID"
        placeholder="D001"
        value={values.driverCode}
        onChange={(v) => setField('driverCode', v)}
      />
      <TextField label="이름" value={values.name} onChange={(v) => setField('name', v)} />
      <TextField label="연락처" value={values.phone} onChange={(v) => setField('phone', v)} />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        기사등록
      </button>
    </div>
  );
}

export default DriverForm;
