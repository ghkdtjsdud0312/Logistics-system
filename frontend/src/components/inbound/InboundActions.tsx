import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import { BUTTON_PRIMARY } from '@/constants/styles';
import { useSubmit } from '@/hooks/useSubmit';
import { putawayInbound, receiveInbound } from '@/services/inboundService';
import { Inbound } from '@/types/inbound';
import { Option } from '@/types/ui';

interface InboundActionsProps {
  inbound: Inbound;
  locations: Option[];
  onChanged: () => void;
}

/** 입고 상태에 맞는 처리 버튼: 입고완료 / 위치 선택 후 적치완료 */
function InboundActions({ inbound, locations, onChanged }: InboundActionsProps) {
  const [locationId, setLocationId] = useState('');
  const { submitting, run } = useSubmit();

  const handle = async (action: () => Promise<unknown>, message: string) => {
    if (await run(action, message)) onChanged();
  };

  if (inbound.status === 'EXPECTED') {
    return (
      <button
        className={BUTTON_PRIMARY}
        disabled={submitting}
        onClick={() => handle(() => receiveInbound(inbound.id), '입고완료 처리했습니다.')}
      >
        입고완료
      </button>
    );
  }
  if (inbound.status === 'PUTAWAY_WAITING') {
    return (
      <div className="flex items-center gap-2">
        <SelectField
          value={locationId}
          onChange={setLocationId}
          options={locations}
          placeholder="적치 위치"
        />
        <button
          className={BUTTON_PRIMARY}
          disabled={submitting || !locationId}
          onClick={() =>
            handle(() => putawayInbound(inbound.id, Number(locationId)), '적치완료 처리했습니다.')
          }
        >
          적치완료
        </button>
      </div>
    );
  }
  return <span className="text-gray-400">-</span>;
}

export default InboundActions;
