import { useState } from 'react';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createDriver } from '@/services/driverService';
import { Errors, required, validate } from '@/utils/validation';

const INITIAL = { driverCode: '', name: '', phone: '' };
const RULES = { driverCode: required('기사ID'), name: required('이름') };

/** 기사 등록 폼 (연락처는 선택) */
function DriverForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    if (await run(() => createDriver(values), '기사를 등록했습니다.')) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-start gap-2">
      <TextField
        required
        label="기사ID"
        placeholder="D001"
        error={errors.driverCode}
        value={values.driverCode}
        onChange={(v) => setField('driverCode', v)}
      />
      <TextField
        required
        label="이름"
        error={errors.name}
        value={values.name}
        onChange={(v) => setField('name', v)}
      />
      <TextField label="연락처" value={values.phone} onChange={(v) => setField('phone', v)} />
      <button className={`${BUTTON_PRIMARY} mt-5`} disabled={submitting} onClick={submit}>
        기사등록
      </button>
    </div>
  );
}

export default DriverForm;
