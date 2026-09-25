import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import {
  collectedReturn,
  collectReturn,
  completeReturn,
  receiveReturn,
} from '@/services/returnService';
import { ReturnOrder } from '@/types/return';
import { Option } from '@/types/ui';

interface ReturnActionsProps {
  returnOrder: ReturnOrder;
  locations: Option[];
  onChanged: () => void;
}

/** 반품 상태에 맞는 처리 버튼. 반품입고는 파손이 아니면 재고를 복구할 위치를 고른다. */
function ReturnActions({ returnOrder, locations, onChanged }: ReturnActionsProps) {
  const [locationId, setLocationId] = useState('');
  const { submitting, run } = useSubmit();
  const handle = async (action: () => Promise<unknown>, message: string) => {
    if (await run(action, message)) onChanged();
  };
  const button = (label: string, action: () => Promise<unknown>, disabled = false) => (
    <button
      className={BUTTON_PRIMARY}
      disabled={submitting || disabled}
      onClick={() => handle(action, `${label} 처리했습니다.`)}
    >
      {label}
    </button>
  );
  const { id, status, reason } = returnOrder;

  if (status === 'REQUESTED') return button('회수시작', () => collectReturn(id));
  if (status === 'COLLECTING') return button('회수완료', () => collectedReturn(id));
  if (status === 'RETURN_RECEIVED') return button('처리완료', () => completeReturn(id));
  if (status === 'COLLECTED') {
    const needsLocation = reason !== 'DAMAGED';
    return (
      <div className="flex items-center gap-2">
        {needsLocation && (
          <SelectField
            value={locationId}
            onChange={setLocationId}
            options={locations}
            placeholder="복구 위치"
          />
        )}
        {button(
          '반품입고',
          () => receiveReturn(id, locationId ? Number(locationId) : undefined),
          needsLocation && !locationId,
        )}
      </div>
    );
  }
  return <span className="text-gray-400">-</span>;
}

export default ReturnActions;
