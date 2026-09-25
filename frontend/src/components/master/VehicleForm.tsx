import { useState } from 'react';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createVehicle } from '@/services/vehicleService';
import { all, Errors, positive, required, validate } from '@/utils/validation';

const INITIAL = { vehicleNumber: '', vehicleType: '', capacityKg: '' };
const RULES = {
  vehicleNumber: required('차량번호'),
  vehicleType: required('차량종류'),
  capacityKg: all(required('적재량'), positive('적재량')),
};

/** 차량 등록 폼 */
function VehicleForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const [errors, setErrors] = useState<Errors>({});
  const { submitting, run } = useSubmit();

  const submit = async () => {
    const found = validate(values, RULES);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    const ok = await run(
      () => createVehicle({ ...values, capacityKg: Number(values.capacityKg) }),
      '차량을 등록했습니다.',
    );
    if (ok) {
      reset();
      onCreated();
    }
  };

  return (
    <div className="flex items-start gap-2">
      <TextField
        required
        label="차량번호"
        placeholder="12가1234"
        error={errors.vehicleNumber}
        value={values.vehicleNumber}
        onChange={(v) => setField('vehicleNumber', v)}
      />
      <TextField
        required
        label="차량종류"
        placeholder="1톤"
        error={errors.vehicleType}
        value={values.vehicleType}
        onChange={(v) => setField('vehicleType', v)}
      />
      <TextField
        required
        label="적재량(kg)"
        type="number"
        error={errors.capacityKg}
        value={values.capacityKg}
        onChange={(v) => setField('capacityKg', v)}
      />
      <button className={`${BUTTON_PRIMARY} mt-5`} disabled={submitting} onClick={submit}>
        차량등록
      </button>
    </div>
  );
}

export default VehicleForm;
