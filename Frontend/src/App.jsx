import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";

// Components
import Sidebar from "./components/Sidebar/Sidebar.jsx";
import Header from "./components/Header/Header.jsx";
import "./components/Header/Header.css";
import "./components/Sidebar/Sidebar.css";

// Pages
import CustomerManagement from "./pages/CustomerManagement/CustomerManagement.jsx";
import CustomerForm from "./pages/CustomerManagement/CustomerForm/CustomerForm.jsx";
import CustomerDetailModal from "./pages/CustomerManagement/CustomerDetailModal/CustomerDetailModal.jsx";
import AuthContainer from "./components/Login/AuthContainer.jsx";
import QRScanPage from "./pages/QRScan/QRScanPage.jsx";
import EcommerManagement from "./pages/EcommerceDashboard/EcommerceDashboard.jsx";
import WarrantyLookup from "./pages/Warranty/WarrantyLookup.jsx";
import PaymentPage from "./pages/Payment/Payment.jsx";
import RepairTickets from "./pages/Repair/RepairTickets";
import ProductPage from "./pages/ProductPage/ProductPage.jsx"; 
function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const toggleSidebar = () => setIsSidebarOpen((prev) => !prev);

  const location = useLocation();
  const isAuthPage = location.pathname === "/login" || location.pathname === "/register";

  return (
    <div>
      {!isAuthPage && <Header onMenuClick={toggleSidebar} />}
      {!isAuthPage && <Sidebar isOpen={isSidebarOpen} />}

      <main className={!isAuthPage ? "main-content" : ""}>
        <Routes>
          {/* Redirect root */}
          <Route path="/" element={<Navigate to="/customers" replace />} />

          {/* Auth */}
          <Route path="/login" element={<AuthContainer />} />
          <Route path="/register" element={<AuthContainer />} />

          {/* Features */}
          <Route path="/customers" element={<CustomerManagement />} />
          <Route path="/customer-form" element={<CustomerForm />} />
          <Route path="/customer/:id" element={<CustomerDetailModal />} />
          <Route path="/qr-scan" element={<QRScanPage />} />
          <Route path="/reports" element={<EcommerManagement />} />
          <Route path="/warranty" element={<WarrantyLookup />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/repair-tickets" element={<RepairTickets />} />
          <Route path="/products" element={<ProductPage />} />

          {/* 404 */}
          <Route path="*" element={<h1>404 - Không tìm thấy trang</h1>} />
        </Routes>
      </main>
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
