import { useState } from 'react';
import FormCard from '@/components/common/FormCard';
import PageHeader from '@/components/common/PageHeader';
import SearchBar from '@/components/common/SearchBar';
import ProductForm from '@/components/master/ProductForm';
import ProductTable from '@/components/master/ProductTable';
import { useFetch } from '@/hooks/useFetch';
import { getProducts } from '@/services/productService';

/** 상품관리: 화면 높이를 넘으면 표 안에서만 세로 스크롤 */
function ProductsPage() {
  const [keyword, setKeyword] = useState('');
  const { data, loading, reload } = useFetch(() => getProducts(keyword), keyword);

  return (
    <div className="flex h-full flex-col">
      <PageHeader title="상품관리" description="상품 기준정보를 관리합니다." />
      <FormCard title="상품 등록">
        <ProductForm onCreated={reload} />
      </FormCard>
      <SearchBar placeholder="상품코드 또는 상품명 검색" onSearch={setKeyword} />
      <ProductTable products={data ?? []} loading={loading} onChanged={reload} />
    </div>
  );
}

export default ProductsPage;
