import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Vehicle, VehicleCreateRequest } from '@/types/vehicle';
import { createVehicle, getVehicles } from '@/services/vehicleService';

/** 차량 목록 조회 + 등록 */
export function useVehicleRegistry() {
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);

  const reload = () => {
    getVehicles()
      .then(setVehicles)
      .catch(() => toast.error('차량 목록을 불러오지 못했습니다.'));
  };

  useEffect(reload, []);

  const register = async (request: VehicleCreateRequest) => {
    try {
      await createVehicle(request);
      toast.success('차량이 등록되었습니다.');
      reload();
      return true;
    } catch {
      toast.error('차량 등록에 실패했습니다. 차량번호 중복 여부를 확인하세요.');
      return false;
    }
  };

  return { vehicles, register };
}
