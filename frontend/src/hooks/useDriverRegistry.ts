import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Driver, DriverCreateRequest } from '@/types/driver';
import { createDriver, getDrivers } from '@/services/driverService';

/** 기사 목록 조회 + 등록 */
export function useDriverRegistry() {
  const [drivers, setDrivers] = useState<Driver[]>([]);

  const reload = () => {
    getDrivers()
      .then(setDrivers)
      .catch(() => toast.error('기사 목록을 불러오지 못했습니다.'));
  };

  useEffect(reload, []);

  const register = async (request: DriverCreateRequest) => {
    try {
      await createDriver(request);
      toast.success('기사가 등록되었습니다.');
      reload();
      return true;
    } catch {
      toast.error('기사 등록에 실패했습니다.');
      return false;
    }
  };

  return { drivers, register };
}
