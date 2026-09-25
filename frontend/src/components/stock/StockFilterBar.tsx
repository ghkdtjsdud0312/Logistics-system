import { useState } from 'react';
import SelectField from '@/components/common/SelectField';
import TextField from '@/components/common/TextField';
import { BUTTON_PRIMARY, BUTTON_SECONDARY } from '@/constants/styles';
import { STOCK_STATUS_LABEL } from '@/constants/stockStatus';
import { StockFilter } from '@/types/stock';
import { WarehouseNode } from '@/types/warehouse';
import { toOptions } from '@/utils/options';
import { warehouseOptions, zoneOptions } from '@/utils/warehouseTree';

const EMPTY: StockFilter = { warehouseId: '', zoneId: '', keyword: '', stockStatus: '' };

interface StockFilterBarProps {
  tree: WarehouseNode[];
  onSearch: (filter: StockFilter) => void;
}

/** 재고 현황 검색 영역 */
function StockFilterBar({ tree, onSearch }: StockFilterBarProps) {
  const [filter, setFilter] = useState<StockFilter>(EMPTY);
  const set = (name: keyof StockFilter, value: string) =>
    setFilter((prev) => ({
      ...prev,
      [name]: value,
      ...(name === 'warehouseId' ? { zoneId: '' } : {}),
    }));

  return (
    <div className="mb-4 flex flex-wrap items-end gap-3">
      <SelectField
        label="창고"
        value={filter.warehouseId ?? ''}
        onChange={(v) => set('warehouseId', v)}
        options={warehouseOptions(tree)}
        placeholder="전체"
      />
      <SelectField
        label="구역"
        value={filter.zoneId ?? ''}
        onChange={(v) => set('zoneId', v)}
        options={zoneOptions(tree, filter.warehouseId)}
        placeholder="전체"
      />
      <TextField
        label="상품"
        placeholder="상품명/코드"
        value={filter.keyword ?? ''}
        onChange={(v) => set('keyword', v)}
      />
      <SelectField
        label="재고상태"
        value={filter.stockStatus ?? ''}
        onChange={(v) => set('stockStatus', v)}
        options={toOptions(STOCK_STATUS_LABEL)}
        placeholder="전체"
      />
      <button className={BUTTON_PRIMARY} onClick={() => onSearch(filter)}>
        검색
      </button>
      <button
        className={BUTTON_SECONDARY}
        onClick={() => {
          setFilter(EMPTY);
          onSearch(EMPTY);
        }}
      >
        초기화
      </button>
    </div>
  );
}

export default StockFilterBar;
