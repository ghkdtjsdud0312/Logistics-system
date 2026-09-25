import PageHeader from '@/components/common/PageHeader';
import ReturnTable from '@/components/returns/ReturnTable';
import { useFetch } from '@/hooks/useFetch';
import { getReturns } from '@/services/returnService';
import { getWarehouseTree } from '@/services/warehouseService';
import { locationOptions } from '@/utils/warehouseTree';

/** 반품관리: 배송 실패 건의 회수와 반품입고 */
function ReturnsPage() {
  const returns = useFetch(getReturns);
  const tree = useFetch(getWarehouseTree);

  return (
    <>
      <PageHeader
        title="반품관리"
        description="배송 실패 건이 자동으로 등록됩니다. 파손이 아니면 반품입고 시 재고가 복구됩니다."
      />
      <ReturnTable
        returns={returns.data ?? []}
        locations={locationOptions(tree.data ?? [])}
        loading={returns.loading}
        onChanged={returns.reload}
      />
    </>
  );
}

export default ReturnsPage;
