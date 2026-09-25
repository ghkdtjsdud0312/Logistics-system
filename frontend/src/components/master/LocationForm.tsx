import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createLocation } from '@/services/warehouseService';
import { WarehouseNode } from '@/types/warehouse';
import { Errors, required, selected, validate } from '@/utils/validation';
import { zoneOptions } from '@/utils/warehouseTree';

const RULES = { zoneId: selected('구역'), code: required('위치 코드') };

/** 위치 등록 폼(구역 선택 + 위치 코드) */
function LocationForm({ tree, onCreated }: { tree: WarehouseNode[]; onCreated: () => void }) {
  const { values, setField, reset } = useForm({ zoneId: '', code: '' });
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    if (
      await run(() => createLocation(Number(values.zoneId), values.code), '위치를 등록했습니다.')
    ) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-start gap-2">
      <SelectField
        required
        label="구역"
        error={errors.zoneId}
        value={values.zoneId}
        onChange={(v) => setField('zoneId', v)}
        options={zoneOptions(tree)}
        placeholder="구역 선택"
      />
      <TextField
        required
        label="위치 코드"
        placeholder="A-01-01"
        error={errors.code}
        value={values.code}
        onChange={(v) => setField('code', v)}
      />
      <button className={`${BUTTON_PRIMARY} mt-5`} disabled={submitting} onClick={submit}>
        위치등록
      </button>
    </div>
  );
}

export default LocationForm;
