interface PageHeaderProps {
  title: string;
  description?: string;
}

/** 페이지 상단 제목 영역 */
function PageHeader({ title, description }: PageHeaderProps) {
  return (
    <div className="mb-4">
      <h1 className="text-xl font-semibold text-gray-900">{title}</h1>
      {description && <p className="mt-1 text-sm text-gray-500">{description}</p>}
    </div>
  );
}

export default PageHeader;
