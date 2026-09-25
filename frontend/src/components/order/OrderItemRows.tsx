import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_SECONDARY } from '@/constants/styles';
import { OrderLineInput } from '@/types/order';
import { Product } from '@/types/product';

interface OrderItemRowsProps {
  products: Product[];
  lines: OrderLineInput[];
  onChange: (lines: OrderLineInput[]) => void;
}

/** 주문 상품 행 편집: 상품 선택 + 수량, 행 추가/삭제 */
function OrderItemRows({ products, lines, onChange }: OrderItemRowsProps) {
  const update = (index: number, patch: Partial<OrderLineInput>) =>
    onChange(lines.map((l, i) => (i === index ? { ...l, ...patch } : l)));
  const options = products.map((p) => ({ value: String(p.id), label: `${p.code} ${p.name}` }));

  return (
    <div className="space-y-2">
      {lines.map((line, i) => (
        <div key={i} className="flex items-end gap-2">
          <SelectField
            label="상품"
            value={line.productId}
            onChange={(v) => update(i, { productId: v })}
            options={options}
            placeholder="상품 선택"
          />
          <TextField
            label="수량"
            type="number"
            value={line.quantity}
            onChange={(v) => update(i, { quantity: v })}
          />
          {lines.length > 1 && (
            <button
              className={BUTTON_SECONDARY}
              onClick={() => onChange(lines.filter((_, idx) => idx !== i))}
            >
              삭제
            </button>
          )}
        </div>
      ))}
      <button
        className={BUTTON_SECONDARY}
        onClick={() => onChange([...lines, { productId: '', quantity: '' }])}
      >
        + 상품 추가
      </button>
    </div>
  );
}

export default OrderItemRows;
