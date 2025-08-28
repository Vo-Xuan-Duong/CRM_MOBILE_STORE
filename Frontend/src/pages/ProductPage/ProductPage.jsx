// src/pages/ProductPage/ProductPage.jsx
import React, { useEffect, useMemo, useState } from "react";
import {
  Plus, Edit, Trash2, Eye, X, Package,
  FileUp, FileDown, Printer, ChevronLeft, ChevronRight
} from "lucide-react";

import Header from "../../components/Header/Header.jsx";
import "../../components/Header/Header.css";
import "./ProductPage.css";

import {
  listProducts,       // trả về { items, page, totalPages, totalItems }
  getProduct,
  createProduct,
  updateProduct,
  deleteProduct,
  activateProduct,
  deactivateProduct,
} from "../../api/productPage.js";
import ProductImportExcel from "../../components/ProductImportExcel";

/* ---------------- UI helpers ---------------- */
const Backdrop = ({ onClose }) => <div className="pg-backdrop" onClick={onClose} />;

const Modal = ({ title, onClose, children, wide }) => (
  <>
    <Backdrop onClose={onClose} />
    <div className="pg-modal-wrap" onClick={onClose}>
      <div
        className={`pg-modal ${wide ? "pg-modal--wide" : ""}`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="pg-modal__header">
          <h3 className="pg-modal__title">{title}</h3>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Đóng">
            <X size={18} />
          </button>
        </div>
        <div className="pg-modal__body">{children}</div>
      </div>
    </div>
  </>
);

/* ---------------- Modal: Chi tiết ---------------- */
const ProductDetailModal = ({ open, onClose, product }) => {
  if (!open || !product) return null;

  return (
    <Modal title="Chi tiết mẫu sản phẩm" onClose={onClose} wide>
      <div className="grid grid-3">
        <div>
          <div className="thumb-large">
            {product.thumbnail ? (
              <img src={product.thumbnail} alt={product.name} />
            ) : (
              <div className="thumb-empty">
                <Package size={48} color="#6b7280" />
              </div>
            )}
          </div>
        </div>

        <div className="grid-span-2">
          <div className="kv">
            <div className="kv__label">Tên mẫu</div>
            <div className="kv__value h1">{product.name}</div>
          </div>

          <div className="grid grid-2 gap">
            <div className="info">
              <div className="info__label">Danh mục</div>
              <div className="info__value">{product.category || "—"}</div>
            </div>
            <div className="info">
              <div className="info__label">Thương hiệu</div>
              <div className="info__value">
                {product.brandName || (product.brandId ?? "—")}
              </div>
            </div>
          </div>

          <div className="grid grid-2 gap">
            <div className="stat">
              <div className="stat__label">Bảo hành (tháng)</div>
              <div className="stat__value">{product.defaultWarrantyMonths ?? 0}</div>
            </div>
            <div className="stat">
              <div className="stat__label">Trạng thái</div>
              <div className="stat__value">
                <span className={`badge ${product.isActive ? "badge-success" : "badge-muted"}`}>
                  {product.isActive ? "Active" : "Inactive"}
                </span>
              </div>
            </div>
          </div>

          {!!product.description && (
            <div className="desc">
              <div className="desc__label">Mô tả</div>
              <p className="desc__content">{product.description}</p>
            </div>
          )}
        </div>
      </div>
    </Modal>
  );
};

/* ---------------- Modal: Form thêm/sửa ---------------- */
const ProductFormModal = ({ open, onClose, initial, onSubmit }) => {
  const [form, setForm] = useState(
    initial || {
      brandId: "",
      name: "",
      category: "PHONE",
      defaultWarrantyMonths: 0,
      description: "",
      isActive: true, // chỉ để UI toggle; create/put không bắt buộc field này
    }
  );
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    setForm(
      initial || {
        brandId: "",
        name: "",
        category: "PHONE",
        defaultWarrantyMonths: 0,
        description: "",
        isActive: true,
      }
    );
  }, [initial, open]);

  if (!open) return null;

  const field = (label, node, alt) => (
    <tr className={alt ? "row-alt" : ""}>
      <th className="cell-label">{label}</th>
      <td className="cell-input">{node}</td>
    </tr>
  );

  const submit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = {
        brandId: Number(form.brandId || 0),
        name: String(form.name || "").trim(),
        category: String(form.category || "PHONE"),
        defaultWarrantyMonths: Number(form.defaultWarrantyMonths || 0),
        description: form.description || "",
        isActive: !!form.isActive,
      };
      await onSubmit(payload);
      onClose();
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title={initial?.id ? "Sửa mẫu sản phẩm" : "Thêm mẫu sản phẩm"} onClose={onClose}>
      <form onSubmit={submit}>
        <div className="table-wrap">
          <table className="table">
            <tbody>
              {field(
                "Brand ID",
                <input
                  type="number"
                  className="input"
                  value={form.brandId}
                  onChange={(e) => setForm({ ...form, brandId: e.target.value })}
                />,
                true
              )}
              {field(
                "Tên mẫu",
                <input
                  className="input"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                />
              )}
              {field(
                "Danh mục",
                <input
                  className="input"
                  value={form.category}
                  onChange={(e) => setForm({ ...form, category: e.target.value })}
                />,
                true
              )}
              {field(
                "Bảo hành (tháng)",
                <input
                  type="number"
                  className="input"
                  value={form.defaultWarrantyMonths}
                  onChange={(e) =>
                    setForm({ ...form, defaultWarrantyMonths: e.target.value })
                  }
                />
              )}
              {field(
                "Mô tả",
                <textarea
                  rows={3}
                  className="textarea"
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                />,
                true
              )}
              {field(
                "Trạng thái",
                <label className="switch">
                  <input
                    type="checkbox"
                    checked={!!form.isActive}
                    onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
                  />
                  <span className="switch-slider" />
                </label>
              )}
            </tbody>
          </table>
        </div>
        <div className="actions-right">
          <button type="button" className="btn btn-outline" onClick={onClose}>
            Hủy
          </button>
          <button className="btn btn-primary" disabled={saving}>
            {saving ? "Đang lưu..." : "Lưu"}
          </button>
        </div>
      </form>
    </Modal>
  );
};

/* ---------------- Main Page ---------------- */
export default function ProductPage() {
  // Data
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  // Filters (server: status/category/brand; client: keyword)
  const [statusFilter, setStatusFilter] = useState("ALL"); // ALL|ACTIVE|INACTIVE
  const [categoryFilter, setCategoryFilter] = useState("ALL");
  const [brandFilter, setBrandFilter] = useState("ALL");
  const [keyword, setKeyword] = useState("");

  // Paging (SERVER-SIDE)
  const [page, setPage] = useState(1);           // 1-based cho UI
  const [pageSize, setPageSize] = useState(15);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  // Modals
  const [showForm, setShowForm] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [detailTarget, setDetailTarget] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  // Load list theo filter (server-side)
  const fetchList = async () => {
    setLoading(true);
    setErr("");
    try {
      const { items, page: srvPage, totalPages, totalItems } = await listProducts({
        page,                       // 1-based
        size: pageSize,
        sortBy: "name",
        sortDir: "asc",
        category: categoryFilter === "ALL" ? undefined : categoryFilter,
        brandId: brandFilter === "ALL" ? undefined : brandFilter,
        active:
          statusFilter === "ALL" ? undefined : statusFilter === "ACTIVE" ? true : false,
      });
      setItems(items);
      setTotalPages(totalPages ?? 1);
      setTotalItems(totalItems ?? items.length);
      // đồng bộ lại page nếu server trả 0-based
      setPage((srvPage ?? 0) + 1);
    } catch (e) {
      setErr(e.message || "Không tải được dữ liệu");
      setItems([]);
      setTotalPages(1);
      setTotalItems(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter, categoryFilter, brandFilter, page, pageSize]);

  // Filter keyword (client) – chỉ lọc trên items hiện tại
  const filtered = useMemo(() => {
    const kw = keyword.trim().toLowerCase();
    if (!kw) return items;
    return items.filter((p) =>
      [p.name, p.category, p.brandName, p.brandId]
        .filter(Boolean)
        .some((v) => String(v).toLowerCase().includes(kw))
    );
  }, [items, keyword]);

  /* ---- CRUD Handlers ---- */
  const addNew = () => {
    setEditTarget(null);
    setShowForm(true);
  };

  const editOne = async (row) => {
    try {
      const fresh = await getProduct(row.id);
      setEditTarget(fresh);
    } catch {
      setEditTarget(row);
    } finally {
      setShowForm(true);
    }
  };

  const saveForm = async (form) => {
    if (editTarget?.id) {
      await updateProduct(editTarget.id, form);
      // xử lý bật/tắt active qua endpoint riêng
      if (typeof form.isActive === "boolean" && form.isActive !== !!editTarget.isActive) {
        if (form.isActive) await activateProduct(editTarget.id);
        else await deactivateProduct(editTarget.id);
      }
    } else {
      const created = await createProduct(form);
      if (created?.id && form.isActive === false) {
        await deactivateProduct(created.id);
      }
    }
    await fetchList();
  };

// helper: đọc message lỗi từ axios
const axMsg = (e) =>
  e?.response?.data?.message ??
  (typeof e?.response?.data === "string" ? e.response.data : null) ??
  e?.message ?? "Lỗi không xác định";

// helper: nhận diện lỗi ràng buộc khóa ngoại
const isForeignKeyError = (e) => {
  const m = (axMsg(e) || "").toLowerCase();
  return m.includes("foreign key constraint") || m.includes('referenced from table "sku"');
};

const removeOne = async (row) => {
  if (!window.confirm(`Xoá mẫu "${row.name}"?`)) return;
  setDeletingId(row.id);
  try {
    // nếu đang active -> vô hiệu hoá trước
    if (row.isActive || row.active) {
      await deactivateProduct(Number(row.id));
    }
    // thử xóa vĩnh viễn
    await deleteProduct(Number(row.id));
    await fetchList();
    alert("Xoá thành công!");
  } catch (e) {
    if (isForeignKeyError(e)) {
      alert(
        "Không thể xoá vĩnh viễn vì mẫu đang được tham chiếu bởi SKU.\n" +
        "Hãy xoá/cập nhật tất cả SKU thuộc mẫu này hoặc chỉ dùng Deactivate."
      );
    } else {
      alert(axMsg(e));
    }
    console.error("Delete error:", e);
  } finally {
    setDeletingId(null);
  }
};



  const toggleActive = async (row) => {
    try {
      if (row.isActive) await deactivateProduct(row.id);
      else await activateProduct(row.id);
      await fetchList();
    } catch (e) {
      alert(e.message || "Thao tác thất bại");
    }
  };

  /* ---- Extra actions ---- */
  const handleImportExcel = (rows) => {
    if (!Array.isArray(rows) || !rows.length) return alert("Không có dữ liệu");
    // TODO: map dữ liệu rows sang API createProduct hoặc batch import
    alert("Đã đọc " + rows.length + " dòng từ Excel. (Bạn cần xử lý lưu vào API)");
    // Ví dụ: rows.forEach(row => createProduct(row));
  };
  const handleExportExcel = () => {
    const rows = [["ID", "Tên", "Danh mục", "Brand", "BH(tháng)", "Trạng thái"]];
    items.forEach((p) =>
      rows.push([
        p.id,
        p.name,
        p.category || "",
        p.brandName || p.brandId || "",
        p.defaultWarrantyMonths ?? 0,
        p.isActive ? "Active" : "Inactive",
      ])
    );
    const csv = rows.map(r => r.map(v => `"${String(v).replace(/"/g, '""')}"`).join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "product-models.csv";
    a.click();
  };
  const handlePrintLabels = () => alert("In tem nhãn - sẽ tích hợp sau");

  // Tính số trang hiển thị (server-side)
  const pageSafe = Math.min(page, Math.max(1, totalPages));

  return (
    <div className="pg-root">
      <Header />

      <div className="pg-container">
        {/* Toolbar */}
        <div className="toolbar">
          <div className="toolbar-left">
            <select
              value={statusFilter}
              onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}
              className="select"
              title="Lọc theo trạng thái"
            >
              <option value="ALL">Tất cả</option>
              <option value="ACTIVE">Đang hoạt động</option>
              <option value="INACTIVE">Bị vô hiệu</option>
            </select>

            <input
              value={categoryFilter === "ALL" ? "" : categoryFilter}
              onChange={(e) => { setCategoryFilter(e.target.value.trim() || "ALL"); setPage(1); }}
              placeholder="Lọc theo danh mục (server)"
              className="input"
              style={{ width: 220 }}
            />

            <input
              value={brandFilter === "ALL" ? "" : brandFilter}
              onChange={(e) => { setBrandFilter(e.target.value.trim() || "ALL"); setPage(1); }}
              placeholder="Lọc theo brandId (server)"
              className="input"
              style={{ width: 220 }}
            />

            <input
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Tìm nhanh tên / danh mục / brand"
              className="input input-lg"
            />
          </div>

          <div className="toolbar-right">
            <button onClick={addNew} className="btn btn-primary btn-with-icon">
              <Plus size={16} /> Thêm
            </button>
            <ProductImportExcel onImport={handleImportExcel} />
            <button onClick={handleExportExcel} className="btn btn-primary btn-with-icon">
              <FileDown size={16} /> Xuất Excel
            </button>
            <button onClick={handlePrintLabels} className="btn btn-primary btn-with-icon">
              <Printer size={16} /> In Tem Nhãn
            </button>
          </div>
        </div>

        {/* Bảng */}
  <div className="card" style={{ maxWidth: 'none', width: '100%', fontSize: '1.1rem' }}>
          {err && <div className="alert alert-danger">{err}</div>}
          {loading && <div className="alert">Đang tải dữ liệu…</div>}

          <table className="table">
            <thead>
              <tr>
                <th>Mẫu sản phẩm</th>
                <th>Danh mục</th>
                <th>Thương hiệu</th>
                <th>Bảo hành (tháng)</th>
                <th>Trạng thái</th>
                <th className="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((p) => (
                <tr key={p.id}>
                  <td>
                    <div className="prod">
                      <div className="thumb">
                        {p.thumbnail ? (
                          <img src={p.thumbnail} alt={p.name} />
                        ) : (
                          <div className="thumb-empty">
                            <Package size={18} color="#6b7280" />
                          </div>
                        )}
                      </div>
                      <div className="prod__name">{p.name}</div>
                    </div>
                  </td>
                  <td>{p.category || "—"}</td>
                  <td>{p.brandName || p.brandId || "—"}</td>
                  <td>{p.defaultWarrantyMonths ?? 0}</td>
                  <td>
                    <span className={`badge ${p.isActive ? "badge-success" : "badge-muted"}`}>
                      {p.isActive ? "Active" : "Inactive"}
                    </span>
                  </td>
                  <td>
                    <div className="row-actions">
                      <button onClick={() => setDetailTarget(p)} className="btn btn-ghost">
                        <Eye size={16} /> Xem
                      </button>
                      <button onClick={() => editOne(p)} className="btn btn-outline">
                        <Edit size={16} /> Sửa
                      </button>
                      <button
                        onClick={() => toggleActive(p)}
                        className="btn btn-outline"
                        title={p.isActive ? "Vô hiệu hoá" : "Kích hoạt"}
                      >
                        {p.isActive ? "Deactivate" : "Activate"}
                      </button>
                      <button
                        onClick={() => removeOne(p)}
                        className="btn btn-danger"
                        disabled={deletingId === p.id}
                      >
                        <Trash2 size={16} />
                        {deletingId === p.id ? "Đang xoá…" : "Xoá"}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && !loading && (
                <tr>
                  <td colSpan={6} className="empty">
                    Không có mẫu sản phẩm phù hợp
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          {/* Phân trang (server-side) */}
          <div className="pg-pagination">
            <button
              className="page-btn"
              disabled={pageSafe <= 1}
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              title="Trang trước"
            >
              <ChevronLeft size={16} />
            </button>

            <span className="page-info" style={{ padding: "0 8px" }}>
              {pageSafe} / {Math.max(1, totalPages)}
            </span>

            <button
              className="page-btn"
              disabled={pageSafe >= Math.max(1, totalPages)}
              onClick={() => setPage((p) => p + 1)}
              title="Trang sau"
            >
              <ChevronRight size={16} />
            </button>

            <select
              className="select"
              value={pageSize}
              onChange={(e) => {
                setPageSize(Number(e.target.value));
                setPage(1);
              }}
              style={{ marginLeft: 8 }}
              title="Số dòng / trang"
            >
              {[10, 15, 25, 50, 100].map((n) => (
                <option key={n} value={n}>{n}/trang</option>
              ))}
            </select>

            <span className="page-info" style={{ marginLeft: 8 }}>
              Tổng: {totalItems}
            </span>
          </div>
        </div>
      </div>

      {/* Modal Form */}
      {showForm && (
        <ProductFormModal
          open={showForm}
          onClose={() => {
            setShowForm(false);
            setEditTarget(null);
          }}
          initial={editTarget}
          onSubmit={saveForm}
        />
      )}

      {/* Modal Detail */}
      {detailTarget && (
        <ProductDetailModal
          open={!!detailTarget}
          onClose={() => setDetailTarget(null)}
          product={detailTarget}
        />
      )}
    </div>
  );
}
