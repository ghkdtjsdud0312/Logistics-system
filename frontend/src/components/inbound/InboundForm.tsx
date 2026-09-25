import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createInbound } from '@/services/inboundService';
import { Product } from '@/types/product';
import { all, Errors, positive, required, selected, validate } from '@/utils/validation';

interface InboundFormProps {
  products: Product[];
  onCreated: () => void;
}

const today = () => new Date().toISOString().split('T')[0];
const RULES = {
  partnerName: required('거래처'),
  productId: selected('상품'),
  quantity: all(required('수량'), positive('수량')),
  inboundDate: required('입고일'),
};

/** 입고 예정 등록 폼 */
function InboundForm({ products, onCreated }: InboundFormProps) {
  const { values, setField, reset } = useForm({
    partnerName: '',
    productId: '',
    quantity: '',
    inboundDate: today(),
  });
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    const ok = await run(
      () =>
        createInbound({
          ...values,
          productId: Number(values.productId),
          quantity: Number(values.quantity),
        }),
      '입고 예정을 등록했습니다.',
    );
    if (ok) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex flex-wrap items-start gap-3">
      <TextField
        required
        label="거래처"
        error={errors.partnerName}
        value={values.partnerName}
        onChange={(v) => setField('partnerName', v)}
      />
      <SelectField
        required
        label="상품"
        error={errors.productId}
        value={values.productId}
        onChange={(v) => setField('productId', v)}
        options={products.map((p) => ({ value: String(p.id), label: `${p.code} ${p.name}` }))}
        placeholder="상품 선택"
      />
      <TextField
        required
        label="수량"
        type="number"
        error={errors.quantity}
        value={values.quantity}
        onChange={(v) => setField('quantity', v)}
      />
      <TextField
        required
        label="입고일"
        type="date"
        error={errors.inboundDate}
        value={values.inboundDate}
        onChange={(v) => setField('inboundDate', v)}
      />
      <button className={`${BUTTON_PRIMARY} mt-5`} disabled={submitting} onClick={submit}>
        입고예정 등록
      </button>
    </div>
  );
}

export default InboundForm;
