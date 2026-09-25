import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createLocation } from '@/services/warehouseService';
import { WarehouseNode } from '@/types/warehouse';
import { zoneOptions } from '@/utils/warehouseTree';

/** 위치 등록 폼(구역 선택 + 위치 코드) */
function LocationForm({ tree, onCreated }: { tree: WarehouseNode[]; onCreated: () => void }) {
  const { values, setField, reset } = useForm({ zoneId: '', code: '' });
  const { submitting, run } = useSubmit();

  const submit = async () => {
    if (
      await run(() => createLocation(Number(values.zoneId), values.code), '위치를 등록했습니다.')
    ) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-end gap-2">
      <SelectField
        label="구역"
        value={values.zoneId}
        onChange={(v) => setField('zoneId', v)}
        options={zoneOptions(tree)}
        placeholder="구역 선택"
      />
      <TextField
        label="위치 코드"
        placeholder="A-01-01"
        value={values.code}
        onChange={(v) => setField('code', v)}
      />
      <button className={BUTTON_PRIMARY} disabled={submitting || !values.zoneId} onClick={submit}>
        위치등록
      </button>
    </div>
  );
}

export default LocationForm;
