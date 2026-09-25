import { useState } from 'react';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createProduct } from '@/services/productService';
import { all, Errors, positive, required, validate } from '@/utils/validation';

const INITIAL = { code: '', name: '', unit: 'EA', unitWeightKg: '' };
const RULES = {
  code: required('상품코드'),
  name: required('상품명'),
  unit: required('단위'),
  unitWeightKg: all(required('단위 중량'), positive('단위 중량')),
};

/** 상품 등록 폼 */
function ProductForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    const ok = await run(
      () => createProduct({ ...values, unitWeightKg: Number(values.unitWeightKg) }),
      '상품을 등록했습니다.',
    );
    if (ok) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="grid grid-cols-2 items-start gap-3 md:grid-cols-5">
      <TextField
        required
        label="상품코드"
        error={errors.code}
        value={values.code}
        onChange={(v) => setField('code', v)}
      />
      <TextField
        required
        label="상품명"
        error={errors.name}
        value={values.name}
        onChange={(v) => setField('name', v)}
      />
      <TextField
        required
        label="단위"
        error={errors.unit}
        value={values.unit}
        onChange={(v) => setField('unit', v)}
      />
      <TextField
        required
        label="단위 중량(kg)"
        type="number"
        error={errors.unitWeightKg}
        value={values.unitWeightKg}
        onChange={(v) => setField('unitWeightKg', v)}
      />
      <button className={`${BUTTON_PRIMARY} mt-5`} disabled={submitting} onClick={submit}>
        상품등록
      </button>
    </div>
  );
}

export default ProductForm;
