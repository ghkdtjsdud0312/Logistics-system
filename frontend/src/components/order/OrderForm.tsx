import { useState } from 'react';
import TextField from '@/components/common/TextField';
import OrderItemRows from '@/components/order/OrderItemRows';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createOrder } from '@/services/orderService';
import { OrderLineInput } from '@/types/order';
import { Product } from '@/types/product';
import { Errors, required, validate } from '@/utils/validation';

const EMPTY_LINE: OrderLineInput = { productId: '', quantity: '' };
const RULES = {
  customerName: required('고객명'),
  address: required('배송 주소'),
  phone: required('연락처'),
};
const LINES_ERROR = '모든 상품 행에 상품을 선택하고 수량을 1 이상으로 입력하세요.';

const isValidLine = (l: OrderLineInput) => l.productId !== '' && Number(l.quantity) > 0;

/** 주문 등록 폼. 등록하면 재고가 예약된다. */
function OrderForm({ products, onCreated }: { products: Product[]; onCreated: () => void }) {
  const { values, setField, reset } = useForm({ customerName: '', address: '', phone: '' });
  const [lines, setLines] = useState<OrderLineInput[]>([EMPTY_LINE]);
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    if (!lines.every(isValidLine)) found.items = LINES_ERROR;
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    const items = lines.map((l) => ({
      productId: Number(l.productId),
      quantity: Number(l.quantity),
    }));
    if (await run(() => createOrder({ ...values, items }), '주문을 등록했습니다.')) {
      reset();
      setLines([EMPTY_LINE]);
      onCreated();
    }
  };

  return (
    <div className="space-y-3">
      <div className="grid grid-cols-1 items-start gap-3 md:grid-cols-3">
        <TextField
          required
          label="고객명"
          error={errors.customerName}
          value={values.customerName}
          onChange={(v) => setField('customerName', v)}
        />
        <TextField
          required
          label="배송 주소"
          error={errors.address}
          value={values.address}
          onChange={(v) => setField('address', v)}
        />
        <TextField
          required
          label="연락처"
          error={errors.phone}
          value={values.phone}
          onChange={(v) => setField('phone', v)}
        />
      </div>
      <OrderItemRows products={products} lines={lines} onChange={setLines} error={errors.items} />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        주문등록
      </button>
    </div>
  );
}

export default OrderForm;
