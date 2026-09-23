import { Driver } from '@/types/driver';

interface Props {
  drivers: Driver[];
}

/** 등록된 기사 목록 표 */
function DriverList({ drivers }: Props) {
  if (drivers.length === 0) {
    return <p className="text-sm text-gray-500">등록된 기사가 없습니다.</p>;
  }

  return (
    <table className="w-full text-left text-sm">
      <thead>
        <tr className="border-b bg-gray-50">
          <th className="px-2 py-1">이름</th>
          <th className="px-2 py-1">상태</th>
        </tr>
      </thead>
      <tbody>
        {drivers.map((d) => (
          <tr key={d.id} className="border-b">
            <td className="px-2 py-1">{d.name}</td>
            <td className="px-2 py-1">{d.status}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default DriverList;
