import React, { useMemo } from "react";
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from "recharts";
import "./EcommerceDashboard.css";

// ===== MOCK DATA =====
const useDashboardData = () => {
  const kpis = useMemo(() => ({
    customers: 3,
    activeCustomers: 2,
    revenueMonth: 42500000,
    orders: 26,
  }), []);

  const revenueByWeek = useMemo(() => ([
    { week: "W1", revenue: 12 },
    { week: "W2", revenue: 18 },
    { week: "W3", revenue: 15 },
    { week: "W4", revenue: 22 },
    { week: "W5", revenue: 25 },
  ]), []);

  const categoryShares = useMemo(() => ([
    { name: "Điện thoại", value: 62 },
    { name: "Phụ kiện", value: 23 },
    { name: "Dịch vụ", value: 15 },
  ]), []);

  const topProducts = useMemo(() => ([
    { name: "iPhone 15", qty: 35 },
    { name: "Galaxy S24", qty: 30 },
    { name: "Redmi 13", qty: 22 },
    { name: "OPPO A79", qty: 18 },
    { name: "Vivo Y36", qty: 15 },
  ]), []);

  const recentOrders = useMemo(() => ([
    { code: "DH-00026", customer: "Lê Văn Cường", amount: 5500000, status: "Chờ xử lý" },
    { code: "DH-00025", customer: "Nguyễn Văn An", amount: 25000000, status: "Hoàn tất" },
    { code: "DH-00024", customer: "Trần Thị Bình", amount: 12000000, status: "Hoàn tất" },
    { code: "DH-00023", customer: "Phạm Đức", amount: 3200000, status: "Hoàn tất" },
  ]), []);

  return { kpis, revenueByWeek, categoryShares, topProducts, recentOrders };
};

const formatVND = (n) =>
  n.toLocaleString("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 });

const badgeClass = (status) => {
  if (status === "Hoàn tất") return "badge badge--success";
  if (status === "Chờ xử lý") return "badge badge--warning";
  return "badge";
};

const EcommerceDashboard = () => {
  const { kpis, revenueByWeek, categoryShares, topProducts, recentOrders } = useDashboardData();
  const pieColors = ["#4F46E5", "#22C55E", "#F59E0B"];

  return (
    <div className="dash">
      {/* Header actions */}
      <div className="dash__head">
        <h1 className="dash__title">Trang tổng quan</h1>
        <div className="dash__actions">
          <button className="btn btn--primary">Quét QR</button>
          <button className="btn btn--primary">Import</button>
          <button className="btn btn--primary">Export</button>
          <button className="btn btn--primary">Thêm khách hàng</button>
        </div>
      </div>

      {/* Charts row */}
      <div className="grid">
        <Card title="Doanh thu theo tuần" span="2">
          <div className="chart">
            <ResponsiveContainer>
              <LineChart data={revenueByWeek} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="week" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="revenue" name="Doanh thu (triệu)" stroke="#10B981" strokeWidth={2} dot />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </Card>

        <Card title="Tỷ trọng danh mục">
          <div className="chart">
            <ResponsiveContainer>
              <PieChart>
                <Pie data={categoryShares} dataKey="value" nameKey="name" outerRadius={110} label>
                  {categoryShares.map((_, i) => (
                    <Cell key={i} fill={pieColors[i % pieColors.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </Card>
      </div>

      {/* Bar + recent orders */}
      <div className="grid">
        <Card title="Top sản phẩm bán chạy">
          <div className="chart">
            <ResponsiveContainer>
              <BarChart data={topProducts} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="qty" name="Số lượng" fill="#6366F1" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card>

        <Card title="Đơn hàng gần đây">
          <div className="tablewrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Mã đơn</th>
                  <th>Khách hàng</th>
                  <th>Giá trị</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {recentOrders.map((o) => (
                  <tr key={o.code}>
                    <td className="fw-md">{o.code}</td>
                    <td>{o.customer}</td>
                    <td>{formatVND(o.amount)}</td>
                    <td><span className={badgeClass(o.status)}>{o.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      </div>
    </div>
  );
};

const Card = ({ title, span, children }) => (
  <div className={`card ${span === "2" ? "card--span2" : ""}`}>
    <div className="card__head">
      <h3 className="card__title">{title}</h3>
    </div>
    {children}
  </div>
);

const KpiCard = ({ icon, label, value }) => (
  <div className="kpi">
    <div className="kpi__icon">{icon}</div>
    <div className="kpi__content">
      <div className="kpi__label">{label}</div>
      <div className="kpi__value">{value}</div>
    </div>
  </div>
);

export default EcommerceDashboard;
