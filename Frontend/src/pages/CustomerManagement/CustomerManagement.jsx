// src/pages/CustomerManagement/CustomerManagement.jsx
import React, { useEffect, useState, useMemo } from "react";
import {
  Users,
  UserPlus,
  QrCode,
  Search,
  Edit,
  Eye,
  Trash2,
  Download,
  Upload,
  X,
  ChevronLeft,
  ChevronRight,
  TrendingUp,
  Star,
  CheckCircle,
  XCircle,
} from "lucide-react";

import CustomerForm from "./CustomerForm/CustomerForm";
import CustomerDetailModal from "./CustomerDetailModal/CustomerDetailModal";
import "./CustomerManagement.css";

// === API thật ===
import {
  listCustomers,            // GET /api/customers?page=&size=&sort=fullName,asc
  quickSearchCustomers,     // GET /api/customers/quick-search?keyword=&page=&size=
  createCustomer,           // POST /api/customers
  updateCustomer,           // PUT  /api/customers/{id}
  deactivateCustomer,       // DELETE /api/customers/{id}   (soft delete)
  activateCustomer,         // PATCH  /api/customers/{id}/activate
  getCustomerStats,         // GET /api/customers/statistics
} from "../../api/customerManagement";

import { normalizeCustomerPayload } from "../../utils/customerPayload";

// map field sort UI -> field API
const mapSortField = (ui) => {
  switch (ui) {
    case "name":
      return "fullName";
    case "createdAt":
      return "createdAt";
    case "totalSpent":
      return "totalSpent";
    case "totalOrders":
      return "totalOrders";
    default:
      return "fullName";
  }
};

// map item trả về từ API -> item dùng trong UI
const toUI = (it) => ({
  id: it.id,
  name: it.fullName ?? it.name ?? "",
  email: it.email ?? "",
  phone: it.phone ?? "",
  address: it.fullAddress ?? it.address ?? "",
  company: it.company ?? "",
  tier: (it.tier ?? "REGULAR").toUpperCase(), // REGULAR | VIP | POTENTIAL...
  totalOrders: it.totalOrders ?? 0,
  totalSpent: it.totalSpent ?? 0,
  createdAt: it.createdAt ?? "",
  lastPurchase: it.lastOrderDate ?? it.lastPurchase ?? "",
});

const CustomerManagement = () => {
  // Data
  const [rows, setRows] = useState([]);        // dữ liệu trang hiện tại (đã map UI)
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  // Search / filter / sort / paging
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedFilter, setSelectedFilter] = useState("all"); // chưa dùng vì API list là active
  const [sortBy, setSortBy] = useState("name");
  const [sortOrder, setSortOrder] = useState("asc");
  const [currentPage, setCurrentPage] = useState(1); // 1-based
  const [itemsPerPage] = useState(10);

  // Modal
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [formMode, setFormMode] = useState("add"); // add | edit

  // UI helpers
  const [selectedCustomers, setSelectedCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState(null);

  // ==== LOAD LIST (server-side) ====
  const loadCustomers = async () => {
    setLoading(true);
    try {
      const params = {
        page: Math.max(0, currentPage - 1), // API 0-based
        size: itemsPerPage,
        sort: `${mapSortField(sortBy)},${sortOrder}`,
      };

      const res = searchTerm.trim()
        ? await quickSearchCustomers({ ...params, keyword: searchTerm.trim() })
        : await listCustomers(params);

      const pageObj = res?.data?.data ?? res?.data ?? {};
      const content = pageObj.content ?? pageObj.items ?? [];

      setRows(content.map(toUI));
      setTotalPages(pageObj.totalPages ?? 1);
      setTotalItems(pageObj.totalElements ?? content.length);
    } catch (e) {
      alert(e?.message || "Không tải được danh sách khách hàng.");
      setRows([]);
      setTotalPages(1);
      setTotalItems(0);
    } finally {
      setLoading(false);
    }
  };

  // ==== LOAD STATS ====
  const loadStats = async () => {
    try {
      const res = await getCustomerStats();
      setStats(res?.data?.data ?? res?.data ?? null);
    } catch {
      setStats(null);
    }
  };

  // gọi lần đầu & mỗi khi filter/sort/page/search đổi
  useEffect(() => {
    loadCustomers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, itemsPerPage, sortBy, sortOrder, searchTerm]);

  useEffect(() => {
    loadStats();
  }, []);

  // ===== CRUD =====
  const handleAddCustomer = () => {
    setSelectedCustomer(null);
    setFormMode("add");
    setIsFormOpen(true);
  };

  const handleEditCustomer = (row) => {
    setSelectedCustomer(row);
    setFormMode("edit");
    setIsFormOpen(true);
  };

  const handleViewCustomer = (row) => {
    setSelectedCustomer(row);
    setIsDetailOpen(true);
  };

  const handleDeleteCustomer = async (id) => {
    if (!window.confirm("Vô hiệu hoá khách hàng này?")) return;
    try {
      await deactivateCustomer(id);
      await loadCustomers();
      await loadStats();
      alert("Đã vô hiệu hoá!");
    } catch (e) {
      alert(e?.response?.data?.message || e?.message || "Thao tác thất bại");
    }
  };

  const handleSaveCustomer = async (formData) => {
    try {
      // chuẩn hoá payload cho backend (fullName, fullAddress, birthDate...)
      const payload = normalizeCustomerPayload(formData);

      if (formMode === "add") {
        await createCustomer(payload);
      } else {
        await updateCustomer(selectedCustomer.id, payload);
      }

      setIsFormOpen(false);
      await loadCustomers();
      await loadStats();
    } catch (e) {
      alert(e?.response?.data?.message || e?.message || "Lưu thất bại");
    }
  };

  // ===== Bulk select =====
  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedCustomers(rows.map((r) => r.id));
    } else {
      setSelectedCustomers([]);
    }
  };
  const handleSelectCustomer = (id) => {
    setSelectedCustomers((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };
  const handleBulkDelete = async () => {
    if (selectedCustomers.length === 0) return;
    if (!window.confirm(`Vô hiệu hoá ${selectedCustomers.length} khách hàng?`))
      return;

    try {
      await Promise.all(selectedCustomers.map((id) => deactivateCustomer(id)));
      setSelectedCustomers([]);
      await loadCustomers();
      await loadStats();
    } catch (e) {
      alert(e?.response?.data?.message || e?.message || "Thao tác thất bại");
    }
  };

  // ===== helpers =====
  const formatCurrency = (v) =>
    new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
      Number(v || 0)
    );

  const getStatusBadge = (tier) => {
    const isVip = String(tier).toUpperCase() === "VIP";
    const Icon = isVip ? CheckCircle : Star;
    return (
      <span className={`status-badge ${isVip ? "active" : "inactive"}`}>
        <Icon /> {isVip ? "VIP" : "Thường"}
      </span>
    );
  };

  // số dòng hiển thị (để hiển thị “từ … đến … / tổng …”)
  const pageFirstIdx = useMemo(
    () => (totalItems === 0 ? 0 : (currentPage - 1) * itemsPerPage + 1),
    [currentPage, itemsPerPage, totalItems]
  );
  const pageLastIdx = useMemo(
    () => Math.min(currentPage * itemsPerPage, totalItems),
    [currentPage, itemsPerPage, totalItems]
  );

  return (
    <div className="customer-management" id="customer-management-page">
      <div className="customer-header">
        {/* Title & actions */}
        <div className="header-section" id="customer-title-row">
          <div className="title-group" id="customer-title-group">
            <h1 id="customer-main-title">
              <Users />
              Quản lý khách hàng
            </h1>
            <p id="customer-count-text">Tổng số: {totalItems} khách hàng</p>
          </div>

          <div className="action-buttons" id="customer-action-buttons">
            <button
              className="btn btn-success"
              onClick={() => (window.location.href = "/qr-scan")}
            >
              <QrCode /> Quét QR
            </button>

            <input
              type="file"
              accept=".csv"
              onChange={(e) => {
                // giữ tính năng import CSV cũ nếu bạn muốn
                // hoặc bỏ hẳn nếu không dùng
                e.target.value = "";
                alert("Import CSV tuỳ chọn — dữ liệu chính lấy từ API");
              }}
              id="import-file"
              style={{ display: "none" }}
            />
            <label htmlFor="import-file" className="btn btn-secondary" id="import-button">
              <Upload /> Import
            </label>

            <button
              className="btn btn-secondary"
              onClick={() => {
                // export từ rows hiện tại
                const csv = [
                  [
                    "Tên",
                    "Email",
                    "SĐT",
                    "Địa chỉ",
                    "Hạng",
                    "Số đơn",
                    "Chi tiêu",
                  ],
                  ...rows.map((c) => [
                    c.name,
                    c.email,
                    c.phone,
                    c.address,
                    c.tier,
                    c.totalOrders,
                    c.totalSpent,
                  ]),
                ]
                  .map((r) => r.join(","))
                  .join("\n");
                const blob = new Blob(["\uFEFF" + csv], {
                  type: "text/csv;charset=utf-8;",
                });
                const a = document.createElement("a");
                a.href = URL.createObjectURL(blob);
                a.download = `customers-page-${currentPage}.csv`;
                a.click();
              }}
              id="export-button"
            >
              <Download /> Export
            </button>

            <button className="btn btn-primary" onClick={handleAddCustomer} id="add-customer-button">
              <UserPlus /> Thêm khách hàng
            </button>
          </div>
        </div>

        {/* Stats */}
        <div className="stats-grid" id="customer-stats-grid">
          <div className="stats-card">
            <div className="stats-content">
              <div className="stats-icon blue">
                <Users />
              </div>
              <div className="stats-info">
                <h3>{stats?.totalActiveCustomers ?? totalItems}</h3>
                <p>Tổng khách hàng</p>
              </div>
            </div>
          </div>
          <div className="stats-card">
            <div className="stats-content">
              <div className="stats-icon green">
                <CheckCircle />
              </div>
              <div className="stats-info">
                <h3>{stats?.vipTierCount ?? "--"}</h3>
                <p>Khách VIP</p>
              </div>
            </div>
          </div>
          <div className="stats-card">
            <div className="stats-content">
              <div className="stats-icon yellow">
                <TrendingUp />
              </div>
              <div className="stats-info">
                <h3>
                  {formatCurrency(rows.reduce((s, c) => s + Number(c.totalSpent || 0), 0))}
                </h3>
                <p>Chi tiêu (trang này)</p>
              </div>
            </div>
          </div>
          <div className="stats-card">
            <div className="stats-content">
              <div className="stats-icon purple">
                <Star />
              </div>
              <div className="stats-info">
                <h3>{rows.reduce((s, c) => s + Number(c.totalOrders || 0), 0)}</h3>
                <p>Đơn hàng (trang này)</p>
              </div>
            </div>
          </div>
        </div>

        {/* Search & filter */}
        <div className="search-filter-section" id="search-filter-section">
          <div className="search-filter-content" id="search-filter-container">
            <div className="search-box" id="search-container">
              <Search id="search-icon" />
              <input
                type="text"
                placeholder="Tìm theo tên, SĐT, email…"
                value={searchTerm}
                onChange={(e) => {
                  setCurrentPage(1);
                  setSearchTerm(e.target.value);
                }}
                className="search-input"
                id="customer-search-input"
              />
            </div>

            <div className="filter-controls" id="filter-container">
              <select
                value={selectedFilter}
                onChange={(e) => setSelectedFilter(e.target.value)}
                className="select-control"
              >
                <option value="all">Tất cả</option>
                {/* có thể thêm bộ lọc tier nếu backend hỗ trợ */}
              </select>

              <select
                value={sortBy}
                onChange={(e) => {
                  setCurrentPage(1);
                  setSortBy(e.target.value);
                }}
                className="select-control"
              >
                <option value="name">Tên A-Z</option>
                <option value="createdAt">Ngày tạo</option>
                <option value="totalSpent">Chi tiêu</option>
                <option value="totalOrders">Số đơn</option>
              </select>

              <button
                onClick={() => setSortOrder((o) => (o === "asc" ? "desc" : "asc"))}
                className="sort-button"
                title={sortOrder === "asc" ? "Tăng dần" : "Giảm dần"}
              >
                {sortOrder === "asc" ? "↑" : "↓"}
              </button>
            </div>
          </div>

          {selectedCustomers.length > 0 && (
            <div className="bulk-actions" id="bulk-actions-section">
              <span className="bulk-actions-text">
                Đã chọn {selectedCustomers.length} khách hàng
              </span>
              <div className="bulk-actions-buttons">
                <button className="btn btn-danger btn-sm" onClick={handleBulkDelete}>
                  <Trash2 /> Vô hiệu hoá đã chọn
                </button>
                <button
                  className="btn btn-gray btn-sm"
                  onClick={() => setSelectedCustomers([])}
                >
                  <X /> Bỏ chọn
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Table */}
      <div className="data-table-container" id="customer-table-container">
        <div className="data-table-wrapper" id="customer-table-wrapper">
          {loading && <div className="alert">Đang tải dữ liệu…</div>}

          <table className="data-table" id="customer-table">
            <thead className="table-header">
              <tr>
                <th>
                  <input
                    type="checkbox"
                    checked={rows.length > 0 && selectedCustomers.length === rows.length}
                    onChange={handleSelectAll}
                  />
                </th>
                <th>Khách hàng</th>
                <th>Liên hệ</th>
                <th>Thống kê</th>
                <th>Hạng</th>
                <th>Thao tác</th>
              </tr>
            </thead>

            <tbody className="table-body">
              {rows.map((c) => (
                <tr key={c.id}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedCustomers.includes(c.id)}
                      onChange={() => handleSelectCustomer(c.id)}
                    />
                  </td>

                  <td>
                    <div className="customer-info">
                      <div className="customer-avatar">
                        {c.name?.charAt(0)?.toUpperCase() ?? "?"}
                      </div>
                      <div className="customer-details">
                        <h4>{c.name}</h4>
                        <p>Mã: KH{String(c.id).slice(-3).padStart(3, "0")}</p>
                      </div>
                    </div>
                  </td>

                  <td>
                    <div className="contact-info">
                      <div className="primary">{c.phone || "-"}</div>
                      <div className="secondary">{c.email || "-"}</div>
                    </div>
                  </td>

                  <td>
                    <div className="contact-info">
                      <div className="primary">{formatCurrency(c.totalSpent)}</div>
                      <div className="secondary">{c.totalOrders} đơn hàng</div>
                    </div>
                  </td>

                  <td>{getStatusBadge(c.tier)}</td>

                  <td>
                    <div className="action-buttons-cell">
                      <button
                        onClick={() => handleViewCustomer(c)}
                        className="action-btn view"
                        title="Xem chi tiết"
                      >
                        <Eye />
                      </button>
                      <button
                        onClick={() => handleEditCustomer(c)}
                        className="action-btn edit"
                        title="Chỉnh sửa"
                      >
                        <Edit />
                      </button>
                      <button
                        onClick={() => handleDeleteCustomer(c.id)}
                        className="action-btn delete"
                        title="Vô hiệu hoá"
                      >
                        <Trash2 />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {!loading && rows.length === 0 && (
                <tr>
                  <td colSpan={6} className="empty">
                    Không có khách hàng
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="pagination" id="pagination-section">
            <div className="pagination-info">
              Hiển thị {pageFirstIdx}-{pageLastIdx} / {totalItems} khách hàng
            </div>

            <div className="pagination-controls">
              <button
                className="pagination-btn"
                disabled={currentPage <= 1}
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                title="Trang trước"
              >
                <ChevronLeft />
              </button>

              <div className="pagination-numbers">
                {Array.from({ length: Math.min(totalPages, 6) }).map((_, i) => {
                  const n = i + 1;
                  return (
                    <button
                      key={n}
                      className={`pagination-number ${n === currentPage ? "active" : ""}`}
                      onClick={() => setCurrentPage(n)}
                    >
                      {n}
                    </button>
                  );
                })}
                {totalPages > 6 && <span className="page-ellipsis">…</span>}
                {totalPages > 6 && (
                  <button className="page-num" onClick={() => setCurrentPage(totalPages)}>
                    {totalPages}
                  </button>
                )}
              </div>

              <button
                className="pagination-btn"
                disabled={currentPage >= totalPages}
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                title="Trang sau"
              >
                <ChevronRight />
              </button>

              <select
                className="select"
                value={itemsPerPage}
                onChange={() => {}}
                disabled
                title="Kích thước trang (đặt 10 ở code)"
                style={{ marginLeft: 8, opacity: 0.6 }}
              >
                <option>10/trang</option>
              </select>
            </div>
          </div>
        )}
      </div>

      {/* Modal Form */}
      <CustomerForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSave={handleSaveCustomer}
        customer={selectedCustomer}
        title={formMode === "add" ? "Thêm khách hàng mới" : "Chỉnh sửa khách hàng"}
        id={formMode === "add" ? "add-customer-modal" : "edit-customer-modal"}
      />

      {/* Modal Detail */}
      <CustomerDetailModal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        customer={selectedCustomer}
        onEdit={(c) => {
          setIsDetailOpen(false);
          handleEditCustomer(c);
        }}
        id="customer-detail-modal"
      />
    </div>
  );
};

export default CustomerManagement;
