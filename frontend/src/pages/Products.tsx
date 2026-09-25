import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import ProductForm from '@/components/master/ProductForm';
import ProductTable from '@/components/master/ProductTable';
import { useFetch } from '@/hooks/useFetch';
import { getProducts } from '@/services/productService';

/** 상품관리 */
function ProductsPage() {
  const { data, loading, reload } = useFetch(() => getProducts());

  return (
    <>
      <PageHeader title="상품관리" description="상품 기준정보를 관리합니다." />
      <ProductTable products={data ?? []} loading={loading} />
      <div className="mt-4">
        <FormCard title="상품 등록">
          <ProductForm onCreated={reload} />
        </FormCard>
      </div>
    </>
  );
}

export default ProductsPage;
