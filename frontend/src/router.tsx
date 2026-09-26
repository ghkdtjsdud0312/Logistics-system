import { Routes, Route } from 'react-router-dom';
import AppLayout from './layout/AppLayout';
import { ROUTES } from './constants/routes';
import DashboardPage from './pages/Dashboard';
import DashboardVehiclesPage from './pages/DashboardVehicles';
import DashboardProgressPage from './pages/DashboardProgress';
import DashboardEventsPage from './pages/DashboardEvents';
import OrderListPage from './pages/OrderList';
import OrderDetailPage from './pages/OrderDetail';
import StockStatusPage from './pages/StockStatus';
import InboundPutawayPage from './pages/InboundPutaway';
import InboundDetailPage from './pages/InboundDetail';
import PickingPackingPage from './pages/PickingPacking';
import LoadingPage from './pages/Loading';
import DispatchRegisterPage from './pages/DispatchRegister';
import DeliveryStatusPage from './pages/DeliveryStatus';
import DeliveryResultPage from './pages/DeliveryResult';
import ReturnsPage from './pages/Returns';
import ReturnDetailPage from './pages/ReturnDetail';
import SchedulePage from './pages/Schedule';
import AuditLogsPage from './pages/AuditLogs';
import ProductsPage from './pages/Products';
import LocationsPage from './pages/Locations';
import Locations3DPage from './pages/Locations3D';
import VehiclesPage from './pages/Vehicles';
import DriversPage from './pages/Drivers';

/** 라우팅: AppLayout(Header + Sidebar) 하위에 화면 뼈대를 연결한다. */
function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
        <Route path={ROUTES.DASHBOARD_VEHICLES} element={<DashboardVehiclesPage />} />
        <Route path={ROUTES.DASHBOARD_PROGRESS} element={<DashboardProgressPage />} />
        <Route path={ROUTES.DASHBOARD_EVENTS} element={<DashboardEventsPage />} />
        <Route path={ROUTES.ORDERS} element={<OrderListPage />} />
        <Route path={ROUTES.ORDER_DETAIL} element={<OrderDetailPage />} />
        <Route path={ROUTES.STOCKS} element={<StockStatusPage />} />
        <Route path={ROUTES.INBOUNDS} element={<InboundPutawayPage />} />
        <Route path={ROUTES.INBOUND_DETAIL} element={<InboundDetailPage />} />
        <Route path={ROUTES.WAREHOUSE_WORK} element={<PickingPackingPage />} />
        <Route path={ROUTES.LOADING} element={<LoadingPage />} />
        <Route path={ROUTES.DISPATCH} element={<DispatchRegisterPage />} />
        <Route path={ROUTES.DELIVERY_STATUS} element={<DeliveryStatusPage />} />
        <Route path={ROUTES.DELIVERY_RESULT} element={<DeliveryResultPage />} />
        <Route path={ROUTES.RETURNS} element={<ReturnsPage />} />
        <Route path={ROUTES.RETURN_DETAIL} element={<ReturnDetailPage />} />
        <Route path={ROUTES.SCHEDULE} element={<SchedulePage />} />
        <Route path={ROUTES.AUDIT_LOGS} element={<AuditLogsPage />} />
        <Route path={ROUTES.PRODUCTS} element={<ProductsPage />} />
        <Route path={ROUTES.LOCATIONS} element={<LocationsPage />} />
        <Route path={ROUTES.LOCATIONS_3D} element={<Locations3DPage />} />
        <Route path={ROUTES.VEHICLES} element={<VehiclesPage />} />
        <Route path={ROUTES.DRIVERS} element={<DriversPage />} />
      </Route>
    </Routes>
  );
}

export default AppRouter;
