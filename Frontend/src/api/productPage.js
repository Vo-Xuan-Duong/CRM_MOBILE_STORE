// src/api/productPage.js
import axios from "axios";

const API_BASE = (import.meta?.env?.VITE_API_BASE || "http://localhost:8080")
  .trim()
  .replace(/\/+$/, "");

const client = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
});

client.interceptors.request.use((cfg) => {
  const t = localStorage.getItem("access_token");
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

const unwrap = (res) => res?.data?.data ?? res?.data;

// Chuẩn hoá: mọi hàm list đều TRẢ VỀ { items: [], page, totalPages, totalItems }
const normalizePage = (d) => ({
  items: Array.isArray(d?.content) ? d.content : [],
  page: Number(d?.number ?? 0),
  totalPages: Number(d?.totalPages ?? 1),
  totalItems: Number(d?.totalElements ?? (Array.isArray(d?.content) ? d.content.length : 0)),
});

const normalizeArray = (arr) => ({
  items: Array.isArray(arr) ? arr : [],
  page: 0,
  totalPages: 1,
  totalItems: Array.isArray(arr) ? arr.length : 0,
});

/* ===== LIST (có phân trang server) =====
   params = { page (1-based UI), size, sortBy, sortDir, category, brandId, active }
   - Nếu active === true  -> /active   (array)
   - Nếu active === false -> /inactive (array)
   - Nếu brandId         -> /brand/{brandId} (array)
   - Nếu category        -> /category/{category} (array)
   - Ngược lại           -> /  (Spring Page)
*/
export async function listProducts(params = {}) {
  const { page, size, sortBy, sortDir, category, brandId, active } = params;

  if (active === true) {
    const res = await client.get(`/api/product-models/active`);
    return normalizeArray(unwrap(res));
  }
  if (active === false) {
    const res = await client.get(`/api/product-models/inactive`);
    return normalizeArray(unwrap(res));
  }
  if (brandId != null && brandId !== "" && brandId !== "ALL") {
    const res = await client.get(`/api/product-models/brand/${encodeURIComponent(brandId)}`);
    return normalizeArray(unwrap(res));
  }
  if (category != null && category !== "" && category !== "ALL") {
    const res = await client.get(`/api/product-models/category/${encodeURIComponent(category)}`);
    return normalizeArray(unwrap(res));
  }

  // Pageable (Spring) – lưu ý server dùng 0-based
  const res = await client.get(`/api/product-models`, {
    params: {
      page: Math.max(0, Number(page ?? 1) - 1),
      size: Number(size ?? 10),
      sortBy: sortBy || "name",
      sortDir: sortDir || "asc",
    },
  });
  return normalizePage(unwrap(res));
}

/* ===== CRUD ===== */
export async function getProduct(id) {
  const res = await client.get(`/api/product-models/${Number(id)}`);
  return unwrap(res);
}

export async function createProduct(payload) {
  const body = {
    brandId: Number(payload.brandId),
    name: String(payload.name || "").trim(),
    category: String(payload.category || "").trim(),
    defaultWarrantyMonths: Number(payload.defaultWarrantyMonths ?? 0),
    description: payload.description ?? "",
  };
  const res = await client.post(`/api/product-models`, body);
  return unwrap(res);
}

export async function updateProduct(id, payload) {
  const body = {
    brandId: Number(payload.brandId),
    name: String(payload.name || "").trim(),
    category: String(payload.category || "").trim(),
    defaultWarrantyMonths: Number(payload.defaultWarrantyMonths ?? 0),
    description: payload.description ?? "",
  };
  const res = await client.put(`/api/product-models/${Number(id)}`, body);
  return unwrap(res);
}

export async function deleteProduct(id) {
  const res = await client.delete(`/api/product-models/${Number(id)}`);
  return unwrap(res);
}

export async function activateProduct(id) {
  const res = await client.put(`/api/product-models/${Number(id)}/activate`);
  return unwrap(res);
}

export async function deactivateProduct(id) {
  const res = await client.put(`/api/product-models/${Number(id)}/deactivate`);
  return unwrap(res);
}
