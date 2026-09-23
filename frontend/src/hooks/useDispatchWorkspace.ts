import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Outbound } from '@/types/outbound';
import { Driver } from '@/types/driver';
import { DispatchCandidateResult } from '@/types/dispatch';
import { getOutboundList } from '@/services/outboundService';
import { getDrivers } from '@/services/driverService';
import { confirmDispatch, findDispatchCandidates } from '@/services/dispatchService';

/** 배차 확정 위자드의 상태와 액션 (출고 선택 -> 후보 조회 -> 차량/기사 선택 -> 확정) */
export function useDispatchWorkspace(onConfirmed: () => void) {
  const [outbounds, setOutbounds] = useState<Outbound[]>([]);
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [selectedOutboundIds, setSelectedOutboundIds] = useState<number[]>([]);
  const [plannedAt, setPlannedAt] = useState('');
  const [candidateResult, setCandidateResult] = useState<DispatchCandidateResult | null>(null);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number | null>(null);
  const [selectedDriverId, setSelectedDriverId] = useState<number | null>(null);

  useEffect(() => {
    getOutboundList()
      .then((all) => setOutbounds(all.filter((o) => o.status === 'REQUESTED')))
      .catch(() => toast.error('출고 계획 목록을 불러오지 못했습니다.'));
    getDrivers()
      .then(setDrivers)
      .catch(() => toast.error('기사 목록을 불러오지 못했습니다.'));
  }, []);

  const toggleOutbound = (id: number) => {
    setSelectedOutboundIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
    setCandidateResult(null);
    setSelectedVehicleId(null);
  };

  const findCandidates = async () => {
    if (selectedOutboundIds.length === 0 || !plannedAt) {
      toast.error('출고 계획과 배차 계획 시각을 입력하세요.');
      return;
    }
    try {
      const result = await findDispatchCandidates({ outboundIds: selectedOutboundIds, plannedAt });
      setCandidateResult(result);
      setSelectedVehicleId(null);
    } catch {
      toast.error('차량 후보 조회에 실패했습니다.');
    }
  };

  const confirm = async () => {
    if (!selectedVehicleId || !selectedDriverId) {
      toast.error('차량과 기사를 모두 선택하세요.');
      return;
    }
    try {
      await confirmDispatch({
        vehicleId: selectedVehicleId,
        driverId: selectedDriverId,
        outboundIds: selectedOutboundIds,
        plannedAt,
      });
      toast.success('배차가 확정되었습니다.');
      setSelectedOutboundIds([]);
      setCandidateResult(null);
      setSelectedVehicleId(null);
      setSelectedDriverId(null);
      onConfirmed();
    } catch {
      toast.error('배차 확정에 실패했습니다. 적재량/일정 충돌 여부를 확인하세요.');
    }
  };

  return {
    outbounds,
    drivers,
    selectedOutboundIds,
    plannedAt,
    setPlannedAt,
    candidateResult,
    selectedVehicleId,
    setSelectedVehicleId,
    selectedDriverId,
    setSelectedDriverId,
    toggleOutbound,
    findCandidates,
    confirm,
  };
}
