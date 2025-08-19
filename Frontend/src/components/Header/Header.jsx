import "./Header.css";
import { FaChevronDown, FaSearch, FaBell, FaUserCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function Header({ onMenuClick }) {
  const navigate = useNavigate();

  return (
    <header className="header">
      <div className="header-left" onClick={onMenuClick} style={{cursor:'pointer'}}>
        <span className="office-name">CRM</span>
        <FaChevronDown style={{ marginLeft: 4 }} size={15} />
      </div>

      <div className="header-search">
        <FaSearch size={15} />
        <input
          type="text"
          placeholder="Tìm kiếm khách hàng, sản phẩm, hợp đồng, công việc..."
        />
      </div>

      <div className="header-right">
        <FaBell size={18} style={{ cursor: "pointer" }} />
        <FaUserCircle size={28} style={{ cursor: "pointer", marginLeft: 12 }} />

       
        <button
          className="login-btn"
          onClick={() => navigate("/login")}
        >
          Đăng nhập
        </button>

        
        <button
          className="register-btn"
          onClick={() => navigate("/register")}
        >
          Đăng ký
        </button>
      </div>
    </header>
  );
}
