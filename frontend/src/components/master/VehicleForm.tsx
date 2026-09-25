import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { createVehicle } from '@/services/vehicleService';

const INITIAL = { vehicleNumber: '', vehicleType: '', capacityKg: '' };

/** 차량 등록 폼 */
function VehicleForm({ onCreated }: { onCreated: () => void }) {
  const { values, setField, reset } = useForm(INITIAL);
  const { submitting, run } = useSubmit();

  const submit = async () => {
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
    <div className="flex items-end gap-2">
      <TextField
        label="차량번호"
        placeholder="12가1234"
        value={values.vehicleNumber}
        onChange={(v) => setField('vehicleNumber', v)}
      />
      <TextField
        label="차량종류"
        placeholder="1톤"
        value={values.vehicleType}
        onChange={(v) => setField('vehicleType', v)}
      />
      <TextField
        label="적재량(kg)"
        type="number"
        value={values.capacityKg}
        onChange={(v) => setField('capacityKg', v)}
      />
      <button className={BUTTON_PRIMARY} disabled={submitting} onClick={submit}>
        차량등록
      </button>
    </div>
  );
}

export default VehicleForm;
