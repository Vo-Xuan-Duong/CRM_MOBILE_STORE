// src/api/customerManagement.js
import axios from "axios";

const API_BASE = (import.meta?.env?.VITE_API_BASE || "http://localhost:8080").replace(/\/+$/, "");

// Axios instance
const http = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
});

// Gắn token nếu có
http.interceptors.request.use((cfg) => {
  const token = localStorage.getItem("access_token");
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

// === LIST: GET /api/customers?page=&size=&sort=fullName,asc
export function listCustomers({ page = 0, size = 10, sort = "fullName,asc" } = {}) {
  return http.get("/api/customers", { params: { page, size, sort } });
}

// === QUICK SEARCH: GET /api/customers/quick-search?keyword=&page=&size=
export function quickSearchCustomers({ keyword = "", page = 0, size = 10, sort = "fullName,asc" } = {}) {
  return http.get("/api/customers/quick-search", { params: { keyword, page, size, sort } });
}

// === CREATE: POST /api/customers
export function createCustomer(payload) {
  return http.post("/api/customers", payload);
}

// === UPDATE: PUT /api/customers/{id}
export function updateCustomer(id, payload) {
  return http.put(`/api/customers/${id}`, payload);
}

// === DEACTIVATE (soft-delete): DELETE /api/customers/{id}
export function deactivateCustomer(id) {
  return http.delete(`/api/customers/${id}`);
}

// === ACTIVATE: PATCH /api/customers/{id}/activate
export function activateCustomer(id) {
  return http.patch(`/api/customers/${id}/activate`);
}

// === STATS: GET /api/customers/statistics
export function getCustomerStats() {
  return http.get("/api/customers/statistics");
}
