import { Vehicle } from '@/types/vehicle';

interface Props {
  vehicles: Vehicle[];
}

/** 등록된 차량 목록 표 */
function VehicleList({ vehicles }: Props) {
  if (vehicles.length === 0) {
    return <p className="text-sm text-gray-500">등록된 차량이 없습니다.</p>;
  }

  return (
    <table className="w-full text-left text-sm">
      <thead>
        <tr className="border-b bg-gray-50">
          <th className="px-2 py-1">차량번호</th>
          <th className="px-2 py-1">유형</th>
          <th className="px-2 py-1">최대 중량</th>
          <th className="px-2 py-1">최대 부피</th>
          <th className="px-2 py-1">상태</th>
        </tr>
      </thead>
      <tbody>
        {vehicles.map((v) => (
          <tr key={v.id} className="border-b">
            <td className="px-2 py-1">{v.vehicleNumber}</td>
            <td className="px-2 py-1">{v.vehicleType}</td>
            <td className="px-2 py-1">{v.maxWeightKg}kg</td>
            <td className="px-2 py-1">{v.maxVolumeM3}㎥</td>
            <td className="px-2 py-1">{v.status}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default VehicleList;
