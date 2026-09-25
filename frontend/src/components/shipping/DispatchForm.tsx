import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useForm } from '@/hooks/useForm';
import { useSubmit } from '@/hooks/useSubmit';
import { registerDispatch } from '@/services/dispatchService';
import { Driver } from '@/types/driver';
import { Vehicle } from '@/types/vehicle';

interface DispatchFormProps {
  vehicles: Vehicle[];
  drivers: Driver[];
  shipmentIds: number[];
  totalWeightKg: number;
  onDone: () => void;
}

const EMPTY = { vehicleId: '', driverId: '', plannedStartAt: '', plannedArrivalAt: '' };

/** 배차 등록 폼. 선택한 배송의 총 중량과 차량 적재량을 비교해 보여 준다. */
function DispatchForm({
  vehicles,
  drivers,
  shipmentIds,
  totalWeightKg,
  onDone,
}: DispatchFormProps) {
  const { values, setField, reset } = useForm(EMPTY);
  const { submitting, run } = useSubmit();
  const vehicle = vehicles.find((v) => String(v.id) === values.vehicleId);
  const over = vehicle !== undefined && totalWeightKg > vehicle.capacityKg;

  const submit = async () => {
    const body = {
      ...values,
      vehicleId: Number(values.vehicleId),
      driverId: Number(values.driverId),
      shipmentIds,
    };
    if (await run(() => registerDispatch(body), '배차를 등록했습니다.')) {
      reset();
      onDone();
    }
  };

  return (
    <div className="flex flex-wrap items-end gap-3">
      <SelectField
        required
        label="차량"
        value={values.vehicleId}
        onChange={(v) => setField('vehicleId', v)}
        options={vehicles.map((v) => ({
          value: String(v.id),
          label: `${v.vehicleNumber} (${v.capacityKg}kg)`,
        }))}
        placeholder="차량 선택"
      />
      <SelectField
        required
        label="기사"
        value={values.driverId}
        onChange={(v) => setField('driverId', v)}
        options={drivers.map((d) => ({ value: String(d.id), label: d.name }))}
        placeholder="기사 선택"
      />
      <TextField
        required
        label="출발예정"
        type="datetime-local"
        value={values.plannedStartAt}
        onChange={(v) => setField('plannedStartAt', v)}
      />
      <TextField
        required
        label="배송예정"
        type="datetime-local"
        value={values.plannedArrivalAt}
        onChange={(v) => setField('plannedArrivalAt', v)}
      />
      <span className={`text-sm ${over ? 'font-semibold text-red-600' : 'text-gray-600'}`}>
        선택 {shipmentIds.length}건 · {totalWeightKg}kg{over ? ' (적재량 초과)' : ''}
      </span>
      <button
        className={BUTTON_PRIMARY}
        onClick={submit}
        disabled={
          submitting ||
          shipmentIds.length === 0 ||
          !values.vehicleId ||
          !values.driverId ||
          !values.plannedStartAt ||
          !values.plannedArrivalAt
        }
      >
        배차등록
      </button>
    </div>
  );
}

export default DispatchForm;
