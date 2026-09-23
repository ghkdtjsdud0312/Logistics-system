import { Routes, Route } from 'react-router-dom';
import AppLayout from './layout/AppLayout';
import HomePage from './pages/Home';
import InboundPage from './pages/Inbound';
import OutboundPage from './pages/Outbound';
import DispatchPage from './pages/Dispatch';
import FleetPage from './pages/Fleet';

/**
 * 라우팅 뼈대
 * - AppLayout(Header + Sidebar) 하위에 각 도메인 페이지를 배치
 * - 실제 페이지가 추가되면 이곳에 Route를 계속 등록
 */
function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/inbound" element={<InboundPage />} />
        <Route path="/outbound" element={<OutboundPage />} />
        <Route path="/dispatch" element={<DispatchPage />} />
        <Route path="/fleet" element={<FleetPage />} />
      </Route>
    </Routes>
  );
}

export default AppRouter;
