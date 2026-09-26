import { useState } from 'react';
import Modal from '@/components/common/Modal';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { EditField } from '@/types/editField';
import { Errors, required, validate } from '@/utils/validation';

interface EditModalProps {
  title: string;
  fields: EditField[];
  initial: Record<string, string>;
  onSubmit: (values: Record<string, string>) => Promise<unknown>;
  onSaved: () => void;
  onClose: () => void;
}

/** 필드 정의만으로 만드는 공통 수정 모달 */
function EditModal({ title, fields, initial, onSubmit, onSaved, onClose }: EditModalProps) {
  const { values, setField } = useForm(initial);
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const rules = Object.fromEntries(
      fields.filter((f) => f.required !== false).map((f) => [f.name, required(f.label)]),
    );
    const found = validate(values, rules);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    if (await run(() => onSubmit(values), '수정했습니다.')) {
      onSaved();
      onClose();
    }
  };

  return (
    <Modal title={title} onClose={onClose}>
      <div className="space-y-3">
        {fields.map((f) => (
          <TextField
            key={f.name}
            label={f.label}
            type={f.type}
            required={f.required !== false}
            error={errors[f.name]}
            value={values[f.name]}
            onChange={(v) => setField(f.name, v)}
          />
        ))}
      </div>
      <div className="mt-4 flex justify-end gap-2">
        <button className={BUTTON_SECONDARY} onClick={onClose}>
          취소
        </button>
        <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
          저장
        </button>
      </div>
    </Modal>
  );
}

export default EditModal;
