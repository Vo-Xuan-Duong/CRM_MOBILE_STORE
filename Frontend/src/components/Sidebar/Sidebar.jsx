import "./Sidebar.css";
import { FaHome, FaUsers, FaQrcode, FaMoneyBillWave, FaShieldAlt, FaChartBar, FaBox, FaUserCog, FaCog } from "react-icons/fa";
import { NavLink } from "react-router-dom";

const menuItems = [
  { title: "Trang chủ", icon: <FaHome />, path: "/" },
  { title: "Quản lý khách hàng", icon: <FaUsers />, path: "/customers" },
  { title: "Quét mã QR/Barcode", icon: <FaQrcode />, path: "/scan" },
  { title: "Thanh toán", icon: <FaMoneyBillWave />, path: "/payment" },
  { title: "Tra cứu bảo hành", icon: <FaShieldAlt />, path: "/warranty" },
  { title: "Thống kê & báo cáo", icon: <FaChartBar />, path: "/reports" },
  { title: "Sản phẩm", icon: <FaBox />, path: "/products" },
  { title: "Quản lý nhân viên", icon: <FaUserCog />, path: "/staff" },
  { title: "Cài đặt", icon: <FaCog />, path: "/settings" },
];

export default function Sidebar() {
  return (
    <div className="sidebar">
      <div className="sidebar-header">📱 Quản lý cửa hàng</div>
      <nav>
        {menuItems.map((item, index) => (
          <NavLink
            key={index}
            to={item.path}
            className={({ isActive }) =>
              `menu-item ${isActive ? "active" : ""}`
            }
          >
            <span className="menu-icon">{item.icon}</span>
            <span>{item.title}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  );
}
