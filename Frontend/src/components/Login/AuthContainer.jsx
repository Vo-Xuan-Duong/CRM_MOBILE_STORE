import React from 'react';
import { useLocation } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import './AuthContainer.css';

const AuthContainer = () => {
  const location = useLocation();
  const isLogin = location.pathname === '/login';

  return (
    <div className="auth-wrapper">
      {isLogin ? <Login /> : <Register />}
    </div>
  );
};

export default AuthContainer;
