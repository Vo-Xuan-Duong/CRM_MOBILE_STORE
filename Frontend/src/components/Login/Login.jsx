import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Lock, User } from 'lucide-react'; // đổi Mail → User cho icon
import './Login.css';
import { loginUser } from '../../api/user';

const Login = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    username: "",
    password: ""
  });

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await loginUser(formData); // { username, password }
      console.log("Login OK:", res);

      if (res.accessToken) {
        localStorage.setItem("access_token", res.accessToken);
      }

      navigate("/customers");
    } catch (err) {
      const msg = err?.message || "Đăng nhập thất bại!";
      alert(msg);
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPassword = () => {
    alert('Tính năng quên mật khẩu sẽ được triển khai sớm!');
  };

  return (
    <div className="auth-container">
      <div className="background-decoration">
        <div className="decoration-circle decoration-circle-1"></div>
        <div className="decoration-circle decoration-circle-2"></div>
      </div>

      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">
            <Lock className="logo-icon" />
          </div>
          <h1 className="auth-title">Đăng Nhập</h1>
        </div>

        <div className="auth-form">
          <div className="input-group">
            <div className="input-icon">
              <User className="icon" />
            </div>
            <input
              type="text"
              name="username"
              placeholder="Tên đăng nhập"
              value={formData.username}
              onChange={handleInputChange}
              className="auth-input"
              required
            />
          </div>

          <div className="input-group">
            <div className="input-icon">
              <Lock className="icon" />
            </div>
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              placeholder="Mật khẩu"
              value={formData.password}
              onChange={handleInputChange}
              className="auth-input"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="password-toggle"
            >
              {showPassword ? <EyeOff className="icon" /> : <Eye className="icon" />}
            </button>
          </div>

          <div className="forgot-password-container">
            <button
              type="button"
              onClick={handleForgotPassword}
              className="forgot-password-btn"
            >
              Quên mật khẩu?
            </button>
          </div>

          <button
            type="button"
            onClick={handleSubmit}
            className="auth-submit-btn"
            disabled={loading}
          >
            {loading ? "Đang đăng nhập..." : "Đăng Nhập"}
          </button>

          <div className="divider">
            <span className="divider-text">hoặc</span>
          </div>

          <div className="social-login">
            <button type="button" className="social-btn">Google</button>
            <button type="button" className="social-btn">Facebook</button>
          </div>
        </div>

        <div className="auth-switch">
          <p className="switch-text">
            Chưa có tài khoản?
            <button
              type="button"
              onClick={() => navigate('/register')}
              className="switch-btn"
            >
              Đăng ký ngay
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
