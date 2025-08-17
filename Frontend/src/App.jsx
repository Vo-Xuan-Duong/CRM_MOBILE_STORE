import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";

// Components
import Sidebar from "./components/Sidebar/Sidebar.jsx";
import Header from "./components/Header/Header.jsx";

// Pages
import CustomerManagement from "./pages/CustomerManagement/CustomerManagement.jsx";
import CustomerForm from "./pages/CustomerManagement/CustomerForm/CustomerForm.jsx";
import CustomerDetailModal from "./pages/CustomerManagement/CustomerDetailModal/CustomerDetailModal.jsx";
import AuthContainer from "./components/Login/AuthContainer.jsx"
import QRScanPage from "./pages/QRScan/QRScanPage.jsx"
import EcommerManagement from "./pages/EcommerceDashboard/EcommerceDashboard.jsx"
function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const toggleSidebar = () => setIsSidebarOpen((prev) => !prev);

  const location = useLocation();
  const isAuthPage = location.pathname === "/login" || location.pathname === "/register";

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Chỉ render Sidebar nếu không ở trang auth */}
      {!isAuthPage && (
        <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
      )}

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        {/* Chỉ render Header nếu không ở trang auth */}
        {!isAuthPage && <Header onMenuClick={toggleSidebar} />}

        {/* Page Content */}
        <main className={`${!isAuthPage ? "p-6" : ""} flex-1`}>
          <Routes>
            {/* Redirect root to customers */}
            <Route path="/" element={<Navigate to="/customers" replace />} />

            {/* Customer management */}
            <Route path="/customers" element={<CustomerManagement />} />
            <Route path="/customer-form" element={<CustomerForm />} />
            <Route path="/customer/:id" element={<CustomerDetailModal />} />

            {/* Auth */}
            <Route path="/login" element={<AuthContainer />} />
            <Route path="/register" element={<AuthContainer />} />
            <Route path="/qr-scan" element={<QRScanPage />} />
            <Route path="/ecommerceDashboard" element={<EcommerManagement/>}/>
            {/* Other features */}
            <Route path="/scan" element={<h1>Quét mã QR</h1>} />
            <Route path="/payment" element={<h1>Thanh toán</h1>} />
            <Route path="/warranty" element={<h1>Tra cứu bảo hành</h1>} />
            <Route path="/reports" element={<h1>Thống kê & báo cáo</h1>} />
            <Route path="/products" element={<h1>Sản phẩm</h1>} />
            <Route path="/staff" element={<h1>Quản lý nhân viên</h1>} />
            <Route path="/settings" element={<h1>Cài đặt</h1>} />

            {/* 404 */}
            <Route path="*" element={<h1>404 - Không tìm thấy trang</h1>} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <AppLayout />
    </Router>
  );
}
