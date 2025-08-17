import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Lock, Mail, User, Phone } from 'lucide-react';
import './Register.css';

const Register = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    agreeTerms: false
  });

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSubmit = () => {
    if (formData.password !== formData.confirmPassword) {
      alert('Mật khẩu xác nhận không khớp!');
      return;
    }
    
    if (!formData.agreeTerms) {
      alert('Vui lòng đồng ý với điều khoản sử dụng!');
      return;
    }

    console.log('Đăng ký với:', formData);
    alert('Đăng ký thành công!');
  };

  return (
    <div className="register-container">
      <div className="background-decoration">
        <div className="decoration-circle decoration-circle-1"></div>
        <div className="decoration-circle decoration-circle-2"></div>
        <div className="decoration-circle decoration-circle-3"></div>
      </div>

      <div className="register-card">
        <div className="register-header">
          <div className="register-logo">
            <User className="logo-icon" />
          </div>
          <h1 className="register-title">Đăng Ký</h1>
          <p className="register-subtitle">Tạo tài khoản mới của bạn</p>
        </div>

        <div className="register-form">
          <div className="input-group">
            <div className="input-icon">
              <User className="icon" />
            </div>
            <input
              type="text"
              name="fullName"
              placeholder="Họ và tên"
              value={formData.fullName}
              onChange={handleInputChange}
              className="register-input"
              required
            />
          </div>

          <div className="input-group">
            <div className="input-icon">
              <Mail className="icon" />
            </div>
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={formData.email}
              onChange={handleInputChange}
              className="register-input"
              required
            />
          </div>

          <div className="input-group">
            <div className="input-icon">
              <Phone className="icon" />
            </div>
            <input
              type="tel"
              name="phone"
              placeholder="Số điện thoại"
              value={formData.phone}
              onChange={handleInputChange}
              className="register-input"
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
              className="register-input"
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

          <div className="input-group">
            <div className="input-icon">
              <Lock className="icon" />
            </div>
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              name="confirmPassword"
              placeholder="Xác nhận mật khẩu"
              value={formData.confirmPassword}
              onChange={handleInputChange}
              className="register-input"
              required
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="password-toggle"
            >
              {showConfirmPassword ? <EyeOff className="icon" /> : <Eye className="icon" />}
            </button>
          </div>

          <div className="checkbox-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="agreeTerms"
                checked={formData.agreeTerms}
                onChange={handleInputChange}
                className="checkbox-input"
              />
              <span className="checkbox-custom"></span>
              <span className="checkbox-text">
                Tôi đồng ý với{' '}
                <a href="#" className="terms-link">Điều khoản sử dụng</a>
                {' '}và{' '}
                <a href="#" className="terms-link">Chính sách bảo mật</a>
              </span>
            </label>
          </div>

          <button
            type="button"
            onClick={handleSubmit}
            className="register-submit-btn"
          >
            Đăng Ký
          </button>

          <div className="divider">
            <span className="divider-text">hoặc đăng ký với</span>
          </div>

          <div className="social-register">
            <button type="button" className="social-btn">
              <svg className="social-icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              Google
            </button>
            <button type="button" className="social-btn">
              <svg className="social-icon" fill="currentColor" viewBox="0 0 24 24">
                <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
              </svg>
              Facebook
            </button>
          </div>
        </div>

        <div className="register-switch">
          <p className="switch-text">
            Đã có tài khoản?
            <button
              type="button"
              onClick={() => navigate('/login')}
              className="switch-btn"
            >
              Đăng nhập
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;