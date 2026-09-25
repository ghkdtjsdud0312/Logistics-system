import { useState } from 'react';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { CodeName } from '@/types/warehouse';
import { Errors, required, validate } from '@/utils/validation';

interface CodeNameFormProps {
  buttonLabel: string;
  onSubmit: (body: CodeName) => Promise<unknown>;
  onCreated: () => void;
  /** 값이 있으면 등록을 막고 이 문구를 보여 준다(예: 상위 항목 미선택). */
  blockedReason?: string;
}

const RULES = { code: required('코드'), name: required('이름') };

/** 창고·구역 등록 폼(코드 + 이름) */
function CodeNameForm({ buttonLabel, onSubmit, onCreated, blockedReason }: CodeNameFormProps) {
  const { values, setField, reset } = useForm({ code: '', name: '' });
  const [errors, setErrors] = useState<Errors>({});
  const [blocked, setBlocked] = useState(false);
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    setBlocked(Boolean(blockedReason));
    if (blockedReason || Object.keys(found).length > 0) return;
    if (await run(() => onSubmit(values), '등록했습니다.')) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-start gap-2">
      <TextField
        required
        label="코드"
        error={errors.code}
        value={values.code}
        onChange={(v) => setField('code', v)}
      />
      <TextField
        required
        label="이름"
        error={errors.name}
        value={values.name}
        onChange={(v) => setField('name', v)}
      />
      <div className="mt-5">
        <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
          {buttonLabel}
        </button>
        {blocked && blockedReason && <p className="mt-0.5 text-xs text-red-500">{blockedReason}</p>}
      </div>
    </div>
  );
}

export default CodeNameForm;
