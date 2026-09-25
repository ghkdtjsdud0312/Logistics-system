import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createInbound } from '@/services/inboundService';
import { Product } from '@/types/product';

interface InboundFormProps {
  products: Product[];
  onCreated: () => void;
}

const today = () => new Date().toISOString().split('T')[0];

/** 입고 예정 등록 폼 */
function InboundForm({ products, onCreated }: InboundFormProps) {
  const { values, setField, reset } = useForm({
    partnerName: '',
    productId: '',
    quantity: '',
    inboundDate: today(),
  });
  const { submitting, run } = useSubmit();

  const submit = async () => {
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
    <div className="flex flex-wrap items-end gap-3">
      <TextField
        label="거래처"
        value={values.partnerName}
        onChange={(v) => setField('partnerName', v)}
      />
      <SelectField
        label="상품"
        value={values.productId}
        onChange={(v) => setField('productId', v)}
        options={products.map((p) => ({ value: String(p.id), label: `${p.code} ${p.name}` }))}
        placeholder="상품 선택"
      />
      <TextField
        label="수량"
        type="number"
        value={values.quantity}
        onChange={(v) => setField('quantity', v)}
      />
      <TextField
        label="입고일"
        type="date"
        value={values.inboundDate}
        onChange={(v) => setField('inboundDate', v)}
      />
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting || !values.productId}
        onClick={submit}
      >
        입고예정 등록
      </button>
    </div>
  );
}

export default InboundForm;
