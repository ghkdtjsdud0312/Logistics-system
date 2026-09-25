import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { ORDER_STATUS_LABEL } from '@/constants/orderStatus';
import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { OrderSearch } from '@/types/order';
import { toOptions } from '@/utils/options';

const EMPTY: OrderSearch = { orderNo: '', customerName: '', status: '', from: '', to: '' };

/** 주문 목록 검색 영역 */
function OrderSearchBar({ onSearch }: { onSearch: (search: OrderSearch) => void }) {
  const { values, setField, reset } = useForm(EMPTY);

  return (
    <div className="mb-4 flex flex-wrap items-end gap-3">
      <TextField label="주문번호" value={values.orderNo} onChange={(v) => setField('orderNo', v)} />
      <TextField
        label="고객명"
        value={values.customerName}
        onChange={(v) => setField('customerName', v)}
      />
      <SelectField
        label="주문상태"
        value={values.status}
        onChange={(v) => setField('status', v)}
        options={toOptions(ORDER_STATUS_LABEL)}
        placeholder="전체"
      />
      <TextField
        label="주문일자(시작)"
        type="date"
        value={values.from}
        onChange={(v) => setField('from', v)}
      />
      <TextField
        label="주문일자(끝)"
        type="date"
        value={values.to}
        onChange={(v) => setField('to', v)}
      />
      <button className={BUTTON_PRIMARY} onClick={() => onSearch(values)}>
        검색
      </button>
      <button
        className={BUTTON_SECONDARY}
        onClick={() => {
          reset();
          onSearch(EMPTY);
        }}
      >
        초기화
      </button>
    </div>
  );
}

export default OrderSearchBar;
