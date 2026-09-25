import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { CodeName } from '@/types/warehouse';

interface CodeNameFormProps {
  buttonLabel: string;
  onSubmit: (body: CodeName) => Promise<unknown>;
  onCreated: () => void;
}

/** 창고·구역 등록 폼(코드 + 이름) */
function CodeNameForm({ buttonLabel, onSubmit, onCreated }: CodeNameFormProps) {
  const { values, setField, reset } = useForm({ code: '', name: '' });
  const { submitting, run } = useSubmit();

  const submit = async () => {
    if (await run(() => onSubmit(values), '등록했습니다.')) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-end gap-2">
      <TextField label="코드" value={values.code} onChange={(v) => setField('code', v)} />
      <TextField label="이름" value={values.name} onChange={(v) => setField('name', v)} />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        {buttonLabel}
      </button>
    </div>
  );
}

export default CodeNameForm;
