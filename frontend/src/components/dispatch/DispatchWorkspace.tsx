import { useDispatchWorkspace } from '@/hooks/useDispatchWorkspace';
import OutboundSelector from './OutboundSelector';
import VehicleCandidateTable from './VehicleCandidateTable';
import DriverSelect from './DriverSelect';

interface Props {
  onConfirmed: () => void;
}

/** 배차 확정 위자드: 출고 물량 선택 -> 차량 후보 조회 -> 차량 선택 -> 기사 배정 -> 배차 확정 */
function DispatchWorkspace({ onConfirmed }: Props) {
  const w = useDispatchWorkspace(onConfirmed);

  return (
    <div className="space-y-4 rounded border p-4">
      <OutboundSelector
        outbounds={w.outbounds}
        selectedIds={w.selectedOutboundIds}
        plannedAt={w.plannedAt}
        onToggle={w.toggleOutbound}
        onPlannedAtChange={w.setPlannedAt}
      />
      <button type="button" onClick={w.findCandidates} className="rounded bg-gray-700 px-3 py-1.5 text-sm text-white">
        차량 후보 조회
      </button>

      {w.candidateResult && (
        <>
          <VehicleCandidateTable
            result={w.candidateResult}
            selectedVehicleId={w.selectedVehicleId}
            onSelect={w.setSelectedVehicleId}
          />
          <DriverSelect
            drivers={w.drivers}
            selectedDriverId={w.selectedDriverId}
            onChange={w.setSelectedDriverId}
            onConfirm={w.confirm}
            disabled={!w.selectedVehicleId || !w.selectedDriverId}
          />
        </>
      )}
    </div>
  );
}

export default DispatchWorkspace;
