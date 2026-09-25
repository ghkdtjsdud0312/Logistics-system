import { useState } from 'react';
import TextField from '@/components/common/TextField';
import OrderItemRows from '@/components/order/OrderItemRows';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createOrder } from '@/services/orderService';
import { OrderLineInput } from '@/types/order';
import { Product } from '@/types/product';

const EMPTY_LINE: OrderLineInput = { productId: '', quantity: '' };

/** 주문 등록 폼. 등록하면 재고가 예약된다. */
function OrderForm({ products, onCreated }: { products: Product[]; onCreated: () => void }) {
  const { values, setField, reset } = useForm({ customerName: '', address: '', phone: '' });
  const [lines, setLines] = useState<OrderLineInput[]>([EMPTY_LINE]);
  const { submitting, run } = useSubmit();

  const submit = async () => {
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
      <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
        <TextField
          label="고객명"
          value={values.customerName}
          onChange={(v) => setField('customerName', v)}
        />
        <TextField
          label="배송 주소"
          value={values.address}
          onChange={(v) => setField('address', v)}
        />
        <TextField label="연락처" value={values.phone} onChange={(v) => setField('phone', v)} />
      </div>
      <OrderItemRows products={products} lines={lines} onChange={setLines} />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        주문등록
      </button>
    </div>
  );
}

export default OrderForm;
