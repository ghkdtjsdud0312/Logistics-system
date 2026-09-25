import { useState } from 'react';
import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import { PAGE_NEXT } from '@/constants/nextStep';
import SelectField from '@/components/common/SelectField';
import Section from '@/components/common/Section';
import InboundForm from '@/components/inbound/InboundForm';
import InboundTable from '@/components/inbound/InboundTable';
import { INBOUND_STATUS_LABEL } from '@/constants/inboundStatus';
import { useFetch } from '@/hooks/useFetch';
import { getInbounds } from '@/services/inboundService';
import { getProducts } from '@/services/productService';
import { getWarehouseTree } from '@/services/warehouseService';
import { toOptions } from '@/utils/options';
import { locationOptions } from '@/utils/warehouseTree';

/** 입고·적치 */
function InboundPutawayPage() {
  const [status, setStatus] = useState('');
  const products = useFetch(() => getProducts());
  const tree = useFetch(getWarehouseTree);
  const inbounds = useFetch(() => getInbounds(status), status);

  return (
    <>
      <PageHeader
        title="입고·적치"
        next={PAGE_NEXT.INBOUNDS}
        description="입고를 처리하고 위치에 적치하면 재고가 늘어납니다."
      />
      <FormCard title="입고 예정 등록">
        <InboundForm products={products.data ?? []} onCreated={inbounds.reload} />
      </FormCard>
      <Section title="입고 목록">
        <div className="mb-2 w-40">
          <SelectField
            value={status}
            onChange={setStatus}
            options={toOptions(INBOUND_STATUS_LABEL)}
            placeholder="전체 상태"
          />
        </div>
        <InboundTable
          inbounds={inbounds.data ?? []}
          locations={locationOptions(tree.data ?? [])}
          loading={inbounds.loading}
          onChanged={inbounds.reload}
        />
      </Section>
    </>
  );
}

export default InboundPutawayPage;
