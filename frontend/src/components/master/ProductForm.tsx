import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createProduct } from '@/services/productService';

const INITIAL = { code: '', name: '', unit: 'EA', unitWeightKg: '' };

/** 상품 등록 폼 */
function ProductForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const { submitting, run } = useSubmit();

  const submit = async () => {
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
    <div className="grid grid-cols-2 items-end gap-3 md:grid-cols-5">
      <TextField label="상품코드" value={values.code} onChange={(v) => setField('code', v)} />
      <TextField label="상품명" value={values.name} onChange={(v) => setField('name', v)} />
      <TextField label="단위" value={values.unit} onChange={(v) => setField('unit', v)} />
      <TextField
        label="단위 중량(kg)"
        type="number"
        value={values.unitWeightKg}
        onChange={(v) => setField('unitWeightKg', v)}
      />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        상품등록
      </button>
    </div>
  );
}

export default ProductForm;
