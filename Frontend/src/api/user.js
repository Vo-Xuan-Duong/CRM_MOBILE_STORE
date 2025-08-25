import axios from "axios";

// Base URL (có thể chỉnh trong .env: VITE_API_BASE=http://localhost:8080)
const API_BASE = (import.meta?.env?.VITE_API_BASE || "http://localhost:8080").replace(/\/+$/, "");

// Hàm đăng ký user
export async function registerUser(payload) {
  try {
    console.log('Registering user with payload:', payload);
    const res = await axios.post(`${API_BASE}/api/auth/register`, payload);
    return res.data;   // dữ liệu backend trả về
  } catch (error) {
    throw error.response?.data || error;
  }
}
// Hàm đăng nhập user
// api/authApi.js
export async function loginUser(credentials) {
  // đảm bảo credentials có 'username' thay vì 'email'
  const payload = {
    username: credentials.username ?? credentials.email, // fallback nếu UI đang dùng email
    password: credentials.password,
  };
  const res = await axios.post(`${API_BASE}/api/auth/login`, payload, {
    headers: { "Content-Type": "application/json" },
  });
  return res.data;
}

