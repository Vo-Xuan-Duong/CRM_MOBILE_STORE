
import React, { useEffect, useMemo, useState, useCallback } from "react";
import {
  Plus,
  Search,
  Eye,
  Pencil,
  Trash2,
  X,
  Save,
  Loader2,
  LayoutGrid,
  Columns3,
  ChevronDown,
  CheckCircle2,
  RefreshCcw
} from "lucide-react";
import "./RepairTickets.css";

/** ======================= CONFIG ======================= */
// Adjust if your backend uses a different prefix
const API_PREFIX = "/api"; // e.g. "/api", ""
const TICKETS_PATH = `${API_PREFIX}/repair-tickets`;
const TECHNICIANS_PATH = `${API_PREFIX}/technicians`;
const CUSTOMERS_SEARCH_PATH = `${API_PREFIX}/customers/search`;

const STATUS_OPTIONS = [
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "IN_PROGRESS", label: "Đang sửa" },
  { value: "WAIT_PARTS", label: "Chờ linh kiện" },
  { value: "DONE", label: "Hoàn thành" },
  { value: "DELIVERED", label: "Đã trả khách" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const DEFAULT_FORM = {
  id: null,
  code: "",
  customer: { id: null, name: "", phone: "" },
  device: { brand: "", model: "", imei: "" },
  initialIssue: "",
  technicianId: "",
  status: "PENDING",
  createdAt: "",
  dueDate: "",
  partsCost: 0,
  laborCost: 0,
  notes: "",
};

/** Utility */
const classNames = (...arr) => arr.filter(Boolean).join(" ");

function statusClass(s) {
  switch (s) {
    case "PENDING": return "badge badge-gray";
    case "IN_PROGRESS": return "badge badge-amber";
    case "WAIT_PARTS": return "badge badge-blue";
    case "DONE": return "badge badge-green";
    case "DELIVERED": return "badge badge-primary";
    case "CANCELLED": return "badge badge-red";
    default: return "badge";
  }
}

function currency(n) {
  const val = Number(n || 0);
  return val.toLocaleString("vi-VN");
}

/** ======================= API ======================= */
async function fetchTickets({ q, status, from, to, page, size }) {
  const params = {};
  if (q) params.q = q;
  if (status) params.status = status;
  if (from) params.from = from;
  if (to) params.to = to;
  if (page != null) params.page = page;
  if (size != null) params.size = size;

  const { data } = await apiClient.get(TICKETS_PATH, { params });
  // Expect shape: { items: Ticket[], total: number, page: number, size: number }
  // If your API returns plain array, normalize it here
  if (Array.isArray(data)) {
    return { items: data, total: data.length, page: 0, size: data.length };
  }
  return data;
}

async function fetchTicketById(id) {
  const { data } = await apiClient.get(`${TICKETS_PATH}/${id}`);
  return data;
}

async function createTicket(payload) {
  const { data } = await apiClient.post(TICKETS_PATH, payload);
  return data;
}

async function updateTicket(id, payload) {
  const { data } = await apiClient.put(`${TICKETS_PATH}/${id}`, payload);
  return data;
}

async function deleteTicket(id) {
  await apiClient.delete(`${TICKETS_PATH}/${id}`);
}

async function updateStatus(id, status) {
  const { data } = await apiClient.patch(`${TICKETS_PATH}/${id}/status`, { status });
  return data;
}

async function fetchTechnicians() {
  try {
    const { data } = await apiClient.get(TECHNICIANS_PATH);
    return Array.isArray(data) ? data : (data?.items || []);
  } catch {
    return [];
  }
}

async function searchCustomers(keyword) {
  try {
    const { data } = await apiClient.get(CUSTOMERS_SEARCH_PATH, { params: { q: keyword } });
    return Array.isArray(data) ? data : (data?.items || []);
  } catch {
    return [];
  }
}

/** ======================= COMPONENT ======================= */
export default function RepairTickets() {
  // table / list state
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [grid, setGrid] = useState(false);

  // filters
  const [q, setQ] = useState("");
  const [debouncedQ, setDebouncedQ] = useState("");
  const [status, setStatus] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  // modal form
  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState("create"); // create | edit | view
  const [form, setForm] = useState({ ...DEFAULT_FORM });
  const [saving, setSaving] = useState(false);
  const [techs, setTechs] = useState([]);

  // customer search
  const [custKeyword, setCustKeyword] = useState("");
  const [custResults, setCustResults] = useState([]);
  const [custLoading, setCustLoading] = useState(false);

  const totalCost = useMemo(() => Number(form.partsCost || 0) + Number(form.laborCost || 0), [form.partsCost, form.laborCost]);

  // debounce search
  useEffect(() => {
    const t = setTimeout(() => setDebouncedQ(q.trim()), 400);
    return () => clearTimeout(t);
  }, [q]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetchTickets({ q: debouncedQ, status, from, to, page, size });
      setItems(res.items || []);
      setTotal(res.total || 0);
      setPage(res.page ?? 0);
      setSize(res.size ?? size);
    } catch (e) {
      console.error("Load tickets failed", e);
    } finally {
      setLoading(false);
    }
  }, [debouncedQ, status, from, to, page, size]);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    (async () => setTechs(await fetchTechnicians()))();
  }, []);

  // customer live search
  useEffect(() => {
    let active = true;
    (async () => {
      if (!custKeyword || custKeyword.length < 2) { setCustResults([]); return; }
      setCustLoading(true);
      const res = await searchCustomers(custKeyword.trim());
      if (active) setCustResults(res);
      setCustLoading(false);
    })();
    return () => { active = false; };
  }, [custKeyword]);

  function resetForm(data = null) {
    setForm(data ? { ...DEFAULT_FORM, ...data } : { ...DEFAULT_FORM });
  }

  function openCreate() {
    setMode("create");
    resetForm();
    setOpen(true);
  }

  async function openEdit(id, viewOnly = false) {
    setMode(viewOnly ? "view" : "edit");
    setSaving(false);
    try {
      setOpen(true);
      const data = await fetchTicketById(id);
      resetForm(data);
    } catch (e) {
      console.error(e);
    }
  }

  async function onSave(e) {
    e?.preventDefault?.();
    if (mode === "view") return;
    setSaving(true);
    try {
      const payload = { ...form, totalCost };
      if (mode === "create") {
        await createTicket(payload);
      } else {
        await updateTicket(form.id, payload);
      }
      setOpen(false);
      resetForm();
      await load();
    } catch (e) {
      console.error("Save failed", e);
    } finally {
      setSaving(false);
    }
  }

  async function onDelete(id) {
    if (!window.confirm("Xóa phiếu này?")) return;
    try {
      await deleteTicket(id);
      await load();
    } catch (e) {
      console.error("Delete failed", e);
    }
  }

  async function onMarkDone(id) {
    try {
      await updateStatus(id, "DONE");
      await load();
    } catch (e) { console.error(e); }
  }

  const pageCount = Math.max(1, Math.ceil(total / size));

  return (
    <div className="rt-page">
      <div className="rt-header">
        <div className="rt-title">
          <h1>Repair Tickets</h1>
          <button className="btn btn-primary" onClick={openCreate}>
            <Plus size={18} /> <span>Tạo phiếu</span>
          </button>
        </div>
        <div className="rt-toolbar">
          <div className="searchbox">
            <Search size={16} />
            <input
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="Tìm theo mã phiếu, IMEI, tên KH, SĐT..."
            />
          </div>

          <div className="filters">
            <select value={status} onChange={(e) => { setStatus(e.target.value); setPage(0); }}>
              <option value="">Tất cả trạng thái</option>
              {STATUS_OPTIONS.map(s => (
                <option key={s.value} value={s.value}>{s.label}</option>
              ))}
            </select>
            <input type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
            <span className="sep">→</span>
            <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />
            <button className="btn btn-ghost" title="Đổi dạng hiển thị" onClick={() => setGrid(v => !v)}>
              {grid ? <Columns3 size={16} /> : <LayoutGrid size={16} />}
            </button>
            <button className="btn btn-ghost" title="Tải lại" onClick={load}>
              <RefreshCcw size={16} />
            </button>
          </div>
        </div>
      </div>

      <div className="rt-content">
        {loading ? (
          <div className="rt-loading"><Loader2 className="spin" /> Đang tải...</div>
        ) : items.length === 0 ? (
          <div className="rt-empty">
            <div className="art" />
            <h3>Chưa có phiếu sửa chữa</h3>
            <p>Hãy tạo mới một phiếu để bắt đầu quy trình tiếp nhận.</p>
            <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Tạo phiếu</button>
          </div>
        ) : grid ? (
          <div className="rt-grid">
            {items.map(it => (
              <article key={it.id} className="card">
                <header>
                  <span className={statusClass(it.status)}>{STATUS_OPTIONS.find(s => s.value === it.status)?.label || it.status}</span>
                  <strong>#{it.code || it.id}</strong>
                </header>
                <div className="meta">
                  <div><b>KH:</b> {it.customer?.name} <span className="muted">({it.customer?.phone})</span></div>
                  <div><b>Thiết bị:</b> {it.device?.brand} {it.device?.model}</div>
                  <div><b>IMEI:</b> {it.device?.imei}</div>
                  <div className="issue"><b>Lỗi:</b> {it.initialIssue}</div>
                </div>
                <footer>
                  <div className="price">{currency(it.totalCost)} đ</div>
                  <div className="actions">
                    <button className="icon" title="Xem" onClick={() => openEdit(it.id, true)}><Eye size={16} /></button>
                    <button className="icon" title="Sửa" onClick={() => openEdit(it.id)}><Pencil size={16} /></button>
                    <button className="icon danger" title="Xóa" onClick={() => onDelete(it.id)}><Trash2 size={16} /></button>
                    {it.status !== "DONE" && (
                      <button className="icon success" title="Đánh dấu hoàn thành" onClick={() => onMarkDone(it.id)}><CheckCircle2 size={16} /></button>
                    )}
                  </div>
                </footer>
              </article>
            ))}
          </div>
        ) : (
          <div className="rt-tablewrap">
            <table className="rt-table">
              <thead>
                <tr>
                  <th>Mã phiếu</th>
                  <th>Khách hàng</th>
                  <th>Thiết bị</th>
                  <th>IMEI</th>
                  <th>Lỗi ban đầu</th>
                  <th>KTV</th>
                  <th>Ngày nhận</th>
                  <th>Hẹn trả</th>
                  <th>Trạng thái</th>
                  <th>Tổng</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {items.map(it => (
                  <tr key={it.id}>
                    <td>#{it.code || it.id}</td>
                    <td>
                      <div className="cell-2">
                        <div className="cell-strong">{it.customer?.name}</div>
                        <div className="muted">{it.customer?.phone}</div>
                      </div>
                    </td>
                    <td>{it.device?.brand} {it.device?.model}</td>
                    <td className="mono">{it.device?.imei}</td>
                    <td className="ellipsis" title={it.initialIssue}>{it.initialIssue}</td>
                    <td>{it.technicianName || "-"}</td>
                    <td>{it.createdAt?.slice(0, 10) || ""}</td>
                    <td>{it.dueDate?.slice(0, 10) || ""}</td>
                    <td><span className={statusClass(it.status)}>{STATUS_OPTIONS.find(s => s.value === it.status)?.label || it.status}</span></td>
                    <td className="mono">{currency(it.totalCost)} đ</td>
                    <td className="actions">
                      <button className="icon" title="Xem" onClick={() => openEdit(it.id, true)}><Eye size={16} /></button>
                      <button className="icon" title="Sửa" onClick={() => openEdit(it.id)}><Pencil size={16} /></button>
                      <button className="icon danger" title="Xóa" onClick={() => onDelete(it.id)}><Trash2 size={16} /></button>
                      {it.status !== "DONE" && (
                        <button className="icon success" title="Hoàn thành" onClick={() => onMarkDone(it.id)}><CheckCircle2 size={16} /></button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {items.length > 0 && (
          <div className="rt-paging">
            <div className="count">Tổng: <b>{total}</b></div>
            <div className="pager">
              <button className="btn btn-ghost" disabled={page <= 0} onClick={() => setPage(p => Math.max(0, p - 1))}>‹ Trước</button>
              <span>{page + 1} / {pageCount}</span>
              <button className="btn btn-ghost" disabled={page + 1 >= pageCount} onClick={() => setPage(p => p + 1)}>Sau ›</button>
              <div className="size">
                <span>Kích thước</span>
                <select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(0); }}>
                  {[10, 20, 50].map(n => <option key={n} value={n}>{n}</option>)}
                </select>
              </div>
            </div>
          </div>
        )}
      </div>

      {open && (
        <div className="rt-modal" onMouseDown={(e) => { if (e.target.classList.contains("rt-modal")) setOpen(false); }}>
          <form className="rt-dialog" onSubmit={onSave}>
            <div className="dlg-head">
              <h3>{mode === "create" ? "Tạo phiếu sửa chữa" : mode === "edit" ? "Cập nhật phiếu" : "Chi tiết phiếu"}</h3>
              <button type="button" className="icon" onClick={() => setOpen(false)}><X size={18} /></button>
            </div>

            <div className="dlg-body">
              <section>
                <h4>Khách hàng</h4>
                <div className="field-row">
                  <label>Tìm KH</label>
                  <input
                    disabled={mode === "view"}
                    value={custKeyword}
                    onChange={(e) => setCustKeyword(e.target.value)}
                    placeholder="Nhập tên hoặc SĐT để tìm"
                  />
                </div>
                {custLoading ? (
                  <div className="muted">Đang tìm...</div>
                ) : custResults.length > 0 ? (
                  <div className="cust-results">
                    {custResults.slice(0, 5).map(c => (
                      <button type="button" key={c.id} onClick={() => { setForm({ ...form, customer: { id: c.id, name: c.name || c.fullName || "", phone: c.phone || "" } }); setCustKeyword(""); setCustResults([]); }}>
                        <div className="name">{c.name || c.fullName}</div>
                        <div className="muted">{c.phone}</div>
                      </button>
                    ))}
                  </div>
                ) : null}

                <div className="grid-2">
                  <div className="field">
                    <label>Tên KH</label>
                    <input disabled={mode === "view"} value={form.customer.name} onChange={(e) => setForm({ ...form, customer: { ...form.customer, name: e.target.value } })} />
                  </div>
                  <div className="field">
                    <label>SĐT</label>
                    <input disabled={mode === "view"} value={form.customer.phone} onChange={(e) => setForm({ ...form, customer: { ...form.customer, phone: e.target.value } })} />
                  </div>
                </div>
              </section>

              <section>
                <h4>Thiết bị</h4>
                <div className="grid-3">
                  <div className="field"><label>Hãng</label><input disabled={mode === "view"} value={form.device.brand} onChange={(e) => setForm({ ...form, device: { ...form.device, brand: e.target.value } })} /></div>
                  <div className="field"><label>Model</label><input disabled={mode === "view"} value={form.device.model} onChange={(e) => setForm({ ...form, device: { ...form.device, model: e.target.value } })} /></div>
                  <div className="field"><label>IMEI</label><input disabled={mode === "view"} value={form.device.imei} onChange={(e) => setForm({ ...form, device: { ...form.device, imei: e.target.value } })} /></div>
                </div>
                <div className="field"><label>Lỗi ban đầu</label><textarea disabled={mode === "view"} rows={3} value={form.initialIssue} onChange={(e) => setForm({ ...form, initialIssue: e.target.value })} /></div>
              </section>

              <section>
                <h4>Sửa chữa</h4>
                <div className="grid-3">
                  <div className="field">
                    <label>Kỹ thuật viên</label>
                    <select disabled={mode === "view"} value={form.technicianId} onChange={(e) => setForm({ ...form, technicianId: e.target.value })}>
                      <option value="">-- Chọn KTV --</option>
                      {techs.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                    </select>
                  </div>
                  <div className="field">
                    <label>Ngày nhận</label>
                    <input type="date" disabled={mode === "view"} value={form.createdAt?.slice(0,10) || ""} onChange={(e) => setForm({ ...form, createdAt: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Hẹn trả</label>
                    <input type="date" disabled={mode === "view"} value={form.dueDate?.slice(0,10) || ""} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} />
                  </div>
                </div>
                <div className="grid-3">
                  <div className="field"><label>Chi phí linh kiện</label><input disabled={mode === "view"} type="number" min={0} value={form.partsCost} onChange={(e) => setForm({ ...form, partsCost: e.target.value })} /></div>
                  <div className="field"><label>Chi phí công</label><input disabled={mode === "view"} type="number" min={0} value={form.laborCost} onChange={(e) => setForm({ ...form, laborCost: e.target.value })} /></div>
                  <div className="field">
                    <label>Tổng tạm tính</label>
                    <input readOnly value={`${currency(totalCost)} đ`} />
                  </div>
                </div>
                <div className="grid-2">
                  <div className="field">
                    <label>Trạng thái</label>
                    <select disabled={mode === "view"} value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                      {STATUS_OPTIONS.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                    </select>
                  </div>
                  <div className="field">
                    <label>Mã phiếu</label>
                    <input disabled={mode === "view"} value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} placeholder="VD: RT2025-001" />
                  </div>
                </div>
                <div className="field"><label>Ghi chú</label><textarea disabled={mode === "view"} rows={3} value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></div>
              </section>
            </div>

            <div className="dlg-foot">
              <button type="button" className="btn" onClick={() => setOpen(false)}>Hủy</button>
              {mode !== "view" && (
                <button className="btn btn-primary" disabled={saving}>
                  {saving ? <Loader2 className="spin" size={16} /> : <Save size={16} />} <span>Lưu</span>
                </button>
              )}
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
