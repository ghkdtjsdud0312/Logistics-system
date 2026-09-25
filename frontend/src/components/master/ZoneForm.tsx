import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import CodeNameForm from '@/components/master/CodeNameForm';
import { createZone } from '@/services/warehouseService';
import { WarehouseNode } from '@/types/warehouse';
import { warehouseOptions } from '@/utils/warehouseTree';

/** 구역 등록 폼(창고 선택 + 코드/이름) */
function ZoneForm({ tree, onCreated }: { tree: WarehouseNode[]; onCreated: () => void }) {
  const [warehouseId, setWarehouseId] = useState('');

  return (
    <div className="flex items-start gap-2">
      <SelectField
        required
        label="창고"
        value={warehouseId}
        onChange={setWarehouseId}
        options={warehouseOptions(tree)}
        placeholder="창고 선택"
      />
      <CodeNameForm
        buttonLabel="구역등록"
        onCreated={onCreated}
        blockedReason={warehouseId ? undefined : '창고를 먼저 선택하세요.'}
        onSubmit={(body) => createZone(Number(warehouseId), body)}
      />
    </div>
  );
}

export default ZoneForm;
