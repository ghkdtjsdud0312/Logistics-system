import { FormEvent, useState } from 'react';
import { DriverCreateRequest } from '@/types/driver';

interface Props {
  onSubmit: (request: DriverCreateRequest) => Promise<boolean>;
}

/** 기사 등록 폼 */
function DriverForm({ onSubmit }: Props) {
  const [name, setName] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (await onSubmit({ name })) setName('');
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-end gap-2 rounded border p-3 text-sm">
      <div>
        <label className="block text-xs text-gray-500">기사 이름</label>
        <input className="w-40 rounded border px-2 py-1" value={name} onChange={(e) => setName(e.target.value)} required />
      </div>
      <button type="submit" className="rounded bg-gray-900 px-3 py-1.5 text-white">
        기사 등록
      </button>
    </form>
  );
}

export default DriverForm;
