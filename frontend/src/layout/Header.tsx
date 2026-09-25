/**
 * 공통 헤더
 * - 로고, 사용자 정보, 알림 등 전역 영역
 */
function Header() {
  return (
    <header className="flex h-14 w-full items-center justify-between border-b border-gray-200 bg-white px-6">
      <div className="text-lg font-bold text-primary">물류 통합 관리 시스템</div>
      <div className="flex items-center gap-4 text-sm text-gray-600">
        <span>물류 담당자</span>
      </div>
    </header>
  );
}

export default Header;
