import React, { useState, useMemo } from 'react';
import {
  LineChart, Line, AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  ScatterChart, Scatter
} from 'recharts';

// Router Component
const Router = ({ children }) => children;

// Route Component
const Route = ({ path, component: Component, currentPath }) => {
  return currentPath === path ? <Component /> : null;
};

// Link Component for Navigation
const Link = ({ to, children, currentPath, onNavigate, className = "", icon = "" }) => {
  const isActive = currentPath === to;
  
  return (
    <button
      onClick={() => onNavigate(to)}
      className={`
        flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 text-left w-full
        ${isActive 
          ? 'bg-blue-600 text-white shadow-lg transform scale-105' 
          : 'text-gray-700 hover:bg-blue-50 hover:text-blue-600 hover:transform hover:scale-102'
        }
        ${className}
      `}
    >
      <span className="text-xl">{icon}</span>
      <span className="font-medium">{children}</span>
      {isActive && (
        <span className="ml-auto text-sm bg-white/20 px-2 py-1 rounded">
          ●
        </span>
      )}
    </button>
  );
};

// Home Page Component
const HomePage = () => (
  <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
    <div className="max-w-7xl mx-auto">
      {/* Hero Section */}
      <div className="text-center mb-12">
        <h1 className="text-5xl font-bold text-gray-900 mb-4">
          🚀 <span className="bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
            eCommerce Management
          </span>
        </h1>
        <p className="text-xl text-gray-600 max-w-2xl mx-auto">
          Hệ thống quản lý toàn diện cho doanh nghiệp thương mại điện tử
        </p>
      </div>

      {/* Feature Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
        {/* Dashboard Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-blue-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">📊</div>
            <div className="bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-sm font-semibold">
              Analytics
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Dashboard Analytics
          </h3>
          <p className="text-gray-600 mb-4">
            Theo dõi KPI, phân tích hành vi khách hàng, doanh thu và retention
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ Real-time metrics</div>
            <div>✓ RFM Analysis</div>
            <div>✓ Cohort tracking</div>
          </div>
        </div>

        {/* Orders Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-green-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">📦</div>
            <div className="bg-green-100 text-green-600 px-3 py-1 rounded-full text-sm font-semibold">
              Orders
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Quản lý Đơn hàng
          </h3>
          <p className="text-gray-600 mb-4">
            Xử lý đơn hàng, theo dõi trạng thái và quản lý giao hàng
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ Order tracking</div>
            <div>✓ Status management</div>
            <div>✓ Shipping integration</div>
          </div>
        </div>

        {/* Customers Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-purple-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">👥</div>
            <div className="bg-purple-100 text-purple-600 px-3 py-1 rounded-full text-sm font-semibold">
              CRM
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Quản lý Khách hàng
          </h3>
          <p className="text-gray-600 mb-4">
            CRM, phân khúc khách hàng và chiến lược marketing
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ Customer profiles</div>
            <div>✓ Segmentation</div>
            <div>✓ Marketing automation</div>
          </div>
        </div>

        {/* Products Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-yellow-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">🛍️</div>
            <div className="bg-yellow-100 text-yellow-600 px-3 py-1 rounded-full text-sm font-semibold">
              Inventory
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Quản lý Sản phẩm
          </h3>
          <p className="text-gray-600 mb-4">
            Catalog sản phẩm, quản lý kho và pricing
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ Product catalog</div>
            <div>✓ Inventory tracking</div>
            <div>✓ Price management</div>
          </div>
        </div>

        {/* Marketing Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-red-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">📢</div>
            <div className="bg-red-100 text-red-600 px-3 py-1 rounded-full text-sm font-semibold">
              Marketing
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Marketing & Ads
          </h3>
          <p className="text-gray-600 mb-4">
            Chiến dịch quảng cáo, email marketing và social media
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ Campaign management</div>
            <div>✓ Email automation</div>
            <div>✓ Social integration</div>
          </div>
        </div>

        {/* Settings Card */}
        <div className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:transform hover:scale-105 border-l-4 border-gray-500">
          <div className="flex items-center justify-between mb-6">
            <div className="text-4xl">⚙️</div>
            <div className="bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm font-semibold">
              System
            </div>
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">
            Cài đặt Hệ thống
          </h3>
          <p className="text-gray-600 mb-4">
            Cấu hình hệ thống, permissions và integrations
          </p>
          <div className="space-y-2 text-sm text-gray-500">
            <div>✓ User management</div>
            <div>✓ System config</div>
            <div>✓ API integrations</div>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">📈 Tổng quan nhanh</h2>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-600">1,234</div>
            <div className="text-gray-600">Đơn hàng hôm nay</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-green-600">$45,678</div>
            <div className="text-gray-600">Doanh thu tuần</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-purple-600">89</div>
            <div className="text-gray-600">Khách hàng mới</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-yellow-600">95%</div>
            <div className="text-gray-600">Satisfaction rate</div>
          </div>
        </div>
      </div>
    </div>
  </div>
);

// EcommerceDashboard Component (your existing dashboard)
const EcommerceDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [dateRange, setDateRange] = useState('7d');

  // Mock Data - Trong thực tế sẽ fetch từ API
  const dailyMetrics = [
    { date: '2024-01-01', newCustomers: 45, activeCustomers: 234, orders: 89, revenue: 12450000, aov: 139887 },
    { date: '2024-01-02', newCustomers: 52, activeCustomers: 267, orders: 95, revenue: 13200000, aov: 138947 },
    { date: '2024-01-03', newCustomers: 38, activeCustomers: 245, orders: 82, revenue: 11800000, aov: 143902 },
    { date: '2024-01-04', newCustomers: 61, activeCustomers: 289, orders: 105, revenue: 15600000, aov: 148571 },
    { date: '2024-01-05', newCustomers: 48, activeCustomers: 256, orders: 91, revenue: 13100000, aov: 143956 },
    { date: '2024-01-06', newCustomers: 67, activeCustomers: 312, orders: 118, revenue: 17200000, aov: 145763 },
    { date: '2024-01-07', newCustomers: 54, activeCustomers: 278, orders: 102, revenue: 14800000, aov: 145098 }
  ];

  const channelData = [
    { channel: 'Facebook', newCustomers: 45, color: '#1877F2' },
    { channel: 'Zalo', newCustomers: 32, color: '#0084FF' },
    { channel: 'Tự nhiên', newCustomers: 28, color: '#10B981' },
    { channel: 'Giới thiệu', newCustomers: 21, color: '#F59E0B' },
    { channel: 'Google', newCustomers: 18, color: '#EA4335' }
  ];

  const funnelData = [
    { stage: 'Đăng ký', count: 1000, percentage: 100 },
    { stage: 'Có đơn đầu', count: 650, percentage: 65 },
    { stage: 'Đơn thứ 2', count: 280, percentage: 28 },
    { stage: 'Khách VIP (5+ đơn)', count: 120, percentage: 12 }
  ];

  const orderFrequencyData = [
    { orders: '1 đơn', customers: 520, color: '#EF4444' },
    { orders: '2-3 đơn', customers: 280, color: '#F59E0B' },
    { orders: '4-6 đơn', customers: 150, color: '#10B981' },
    { orders: '7-10 đơn', customers: 85, color: '#3B82F6' },
    { orders: '10+ đơn', customers: 65, color: '#8B5CF6' }
  ];

  const rfmData = [
    { recency: 5, frequency: 8, monetary: 2400000, segment: 'VIP', color: '#10B981' },
    { recency: 12, frequency: 5, monetary: 1800000, segment: 'VIP', color: '#10B981' },
    { recency: 8, frequency: 6, monetary: 2100000, segment: 'VIP', color: '#10B981' },
    { recency: 25, frequency: 3, monetary: 950000, segment: 'Hứa hẹn', color: '#3B82F6' },
    { recency: 18, frequency: 4, monetary: 1200000, segment: 'Hứa hẹn', color: '#3B82F6' },
    { recency: 45, frequency: 2, monetary: 600000, segment: 'Cần kích hoạt', color: '#F59E0B' },
    { recency: 60, frequency: 1, monetary: 350000, segment: 'Có nguy cơ rời bỏ', color: '#EF4444' },
    { recency: 15, frequency: 7, monetary: 2800000, segment: 'VIP', color: '#10B981' },
    { recency: 30, frequency: 4, monetary: 1100000, segment: 'Hứa hẹn', color: '#3B82F6' },
    { recency: 52, frequency: 2, monetary: 480000, segment: 'Cần kích hoạt', color: '#F59E0B' }
  ];

  const topCustomers = [
    { name: 'Nguyễn Văn A', spent: 15600000, orders: 12 },
    { name: 'Trần Thị B', spent: 12800000, orders: 8 },
    { name: 'Lê Văn C', spent: 11200000, orders: 15 },
    { name: 'Phạm Thị D', spent: 9800000, orders: 7 },
    { name: 'Hoàng Văn E', spent: 8900000, orders: 6 },
    { name: 'Vũ Thị F', spent: 8200000, orders: 9 },
    { name: 'Đặng Văn G', spent: 7500000, orders: 5 },
    { name: 'Bùi Thị H', spent: 6800000, orders: 8 },
    { name: 'Đinh Văn I', spent: 6200000, orders: 4 },
    { name: 'Mai Thị K', spent: 5900000, orders: 7 }
  ];

  const paymentMethodData = [
    { method: 'COD', value: 45, color: '#EF4444' },
    { method: 'VNPAY', value: 25, color: '#3B82F6' },
    { method: 'MOMO', value: 20, color: '#EC4899' },
    { method: 'Chuyển khoản', value: 10, color: '#10B981' }
  ];

  const unpaidOrdersData = [
    { range: '0-7 ngày', count: 25, color: '#10B981' },
    { range: '8-14 ngày', count: 18, color: '#F59E0B' },
    { range: '15-30 ngày', count: 12, color: '#EF4444' },
    { range: '>30 ngày', count: 8, color: '#7F1D1D' }
  ];

  const warrantyClaimsData = [
    { date: '2024-01-01', iPhone: 5, Samsung: 3, Xiaomi: 2 },
    { date: '2024-01-02', iPhone: 7, Samsung: 4, Xiaomi: 1 },
    { date: '2024-01-03', iPhone: 3, Samsung: 6, Xiaomi: 3 },
    { date: '2024-01-04', iPhone: 8, Samsung: 2, Xiaomi: 4 },
    { date: '2024-01-05', iPhone: 4, Samsung: 5, Xiaomi: 2 },
    { date: '2024-01-06', iPhone: 6, Samsung: 3, Xiaomi: 5 },
    { date: '2024-01-07', iPhone: 9, Samsung: 4, Xiaomi: 1 }
  ];

  const cohortData = [
    { month: 'T1/2024', m0: 100, m1: 65, m2: 45, m3: 32, m4: 25, m5: 20 },
    { month: 'T2/2024', m0: 100, m1: 68, m2: 48, m3: 35, m4: 28, m5: null },
    { month: 'T3/2024', m0: 100, m1: 72, m2: 52, m3: 38, m4: null, m5: null },
    { month: 'T4/2024', m0: 100, m1: 70, m2: 49, m3: null, m4: null, m5: null },
    { month: 'T5/2024', m0: 100, m1: 75, m2: null, m3: null, m4: null, m5: null },
    { month: 'T6/2024', m0: 100, m1: null, m2: null, m3: null, m4: null, m5: null }
  ];

  // Tính toán tổng hợp
  const totalStats = useMemo(() => {
    const total = dailyMetrics.reduce((acc, day) => ({
      newCustomers: acc.newCustomers + day.newCustomers,
      activeCustomers: Math.max(acc.activeCustomers, day.activeCustomers),
      orders: acc.orders + day.orders,
      revenue: acc.revenue + day.revenue
    }), { newCustomers: 0, activeCustomers: 0, orders: 0, revenue: 0 });
    
    return {
      ...total,
      aov: total.revenue / total.orders,
      conversionRate: (total.orders / total.activeCustomers * 100).toFixed(1)
    };
  }, [dailyMetrics]);

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(value);
  };

  const formatNumber = (value) => {
    return new Intl.NumberFormat('vi-VN').format(value);
  };

  const tabs = [
    { key: 'overview', label: '📊 Tổng quan', icon: '📊' },
    { key: 'growth', label: '📈 Tăng trưởng', icon: '📈' },
    { key: 'behavior', label: '👥 Hành vi KH', icon: '👥' },
    { key: 'revenue', label: '💰 Doanh thu', icon: '💰' },
    { key: 'retention', label: '🔄 Giữ chân', icon: '🔄' },
    { key: 'operations', label: '⚙️ Vận hành', icon: '⚙️' }
  ];

  const renderOverview = () => (
    <div className="space-y-6">
      {/* KPIs Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <div className="bg-white p-4 rounded-lg shadow-md border-l-4 border-blue-500">
          <h3 className="text-sm font-medium text-gray-600">Khách mới (7 ngày)</h3>
          <p className="text-2xl font-bold text-gray-900">{formatNumber(totalStats.newCustomers)}</p>
          <p className="text-green-600 text-xs">↗️ +12.5%</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md border-l-4 border-green-500">
          <h3 className="text-sm font-medium text-gray-600">Khách hoạt động</h3>
          <p className="text-2xl font-bold text-gray-900">{formatNumber(totalStats.activeCustomers)}</p>
          <p className="text-green-600 text-xs">↗️ +8.3%</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md border-l-4 border-purple-500">
          <h3 className="text-sm font-medium text-gray-600">Đơn hàng</h3>
          <p className="text-2xl font-bold text-gray-900">{formatNumber(totalStats.orders)}</p>
          <p className="text-red-600 text-xs">↘️ -2.1%</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md border-l-4 border-yellow-500">
          <h3 className="text-sm font-medium text-gray-600">Doanh thu</h3>
          <p className="text-xl font-bold text-gray-900">{formatCurrency(totalStats.revenue)}</p>
          <p className="text-green-600 text-xs">↗️ +15.7%</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md border-l-4 border-red-500">
          <h3 className="text-sm font-medium text-gray-600">AOV</h3>
          <p className="text-xl font-bold text-gray-900">{formatCurrency(totalStats.aov)}</p>
          <p className="text-green-600 text-xs">↗️ +3.2%</p>
        </div>
      </div>

      {/* Main Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-4">Khách mới theo ngày</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={dailyMetrics}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" tickFormatter={(date) => new Date(date).getDate()} />
              <YAxis />
              <Tooltip labelFormatter={(date) => new Date(date).toLocaleDateString('vi-VN')} />
              <Line type="monotone" dataKey="newCustomers" stroke="#3B82F6" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-4">Doanh thu theo ngày</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={dailyMetrics}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" tickFormatter={(date) => new Date(date).getDate()} />
              <YAxis tickFormatter={(value) => `${value/1000000}M`} />
              <Tooltip 
                labelFormatter={(date) => new Date(date).toLocaleDateString('vi-VN')}
                formatter={(value) => [formatCurrency(value), 'Doanh thu']}
              />
              <Area type="monotone" dataKey="revenue" stroke="#10B981" fill="#86EFAC" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );

  const renderGrowth = () => (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-4">Khách mới theo kênh</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={channelData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="channel" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="newCustomers" fill="#3B82F6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold mb-4">Tỉ trọng kênh</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={channelData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ channel, newCustomers, percent }) => 
                  `${channel}: ${(percent * 100).toFixed(0)}%`
                }
                outerRadius={100}
                fill="#8884d8"
                dataKey="newCustomers"
              >
                {channelData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Funnel Chart */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-4">Phễu chuyển đổi khách hàng</h3>
        <div className="space-y-2">
          {funnelData.map((stage, index) => (
            <div key={stage.stage} className="flex items-center">
              <div className="w-32 text-sm">{stage.stage}</div>
              <div className="flex-1 bg-gray-200 rounded-full h-8 relative">
                <div
                  className="bg-gradient-to-r from-blue-500 to-blue-600 h-8 rounded-full flex items-center justify-center text-white text-sm font-medium"
                  style={{ width: `${stage.percentage}%` }}
                >
                  {formatNumber(stage.count)} ({stage.percentage}%)
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );

  const renderContent = () => {
    switch (activeTab) {
      case 'overview': return renderOverview();
      case 'growth': return renderGrowth();
      default: return renderOverview();
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            📊 Dashboard Analytics eCommerce
          </h1>
          <p className="text-gray-600">
            Theo dõi toàn diện khách hàng, doanh thu và vận hành
          </p>
        </div>

        {/* Date Range Selector */}
        <div className="mb-6 flex space-x-2">
          {['7d', '30d', '90d'].map((range) => (
            <button
              key={range}
              onClick={() => setDateRange(range)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                dateRange === range
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-700 hover:bg-gray-50'
              }`}
            >
              {range === '7d' ? '7 ngày' : range === '30d' ? '30 ngày' : '90 ngày'}
            </button>
          ))}
        </div>

        {/* Tabs Navigation */}
        <div className="mb-8 overflow-x-auto">
          <div className="flex space-x-1 bg-white p-1 rounded-lg shadow-sm min-w-max">
            {tabs.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 whitespace-nowrap ${
                  activeTab === tab.key
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Content */}
        {renderContent()}

        {/* Footer */}
        <div className="mt-8 text-center text-gray-500 text-sm">
          <p>Dashboard được cập nhật lúc: {new Date().toLocaleString('vi-VN')}</p>
        </div>
      </div>
    </div>
  );
};

// Orders Page Component
const OrdersPage = () => (
  <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 p-6">
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">📦 Quản lý Đơn hàng</h1>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <p className="text-lg text-gray-600">Trang quản lý đơn hàng đang được phát triển...</p>
      </div>
    </div>
  </div>
);

// Customers Page Component
const CustomersPage = () => (
  <div className="min-h-screen bg-gradient-to-br from-purple-50 to-indigo-100 p-6">
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">👥 Quản lý Khách hàng</h1>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <p className="text-lg text-gray-600">Trang quản lý khách hàng đang được phát triển...</p>
      </div>
    </div>
  </div>
);

// Products Page Component
const ProductsPage = () => (
  <div className="min-h-screen bg-gradient-to-br from-yellow-50 to-orange-100 p-6">
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">🛍️ Quản lý Sản phẩm</h1>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <p className="text-lg text-gray-600">Trang quản lý sản phẩm đang được phát triển...</p>
      </div>
    </div>
  </div>
);

// Marketing Page Component
const MarketingPage = () => (
  <div className="min-h-screen bg-gradient-to-br from-red-50 to-pink-100 p-6">
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">📢 Marketing & Ads</h1>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <p className="text-lg text-gray-600">Trang marketing đang được phát triển...</p>
      </div>
    </div>
  </div>
);

// Settings Page Component
const SettingsPage = () => (
  <div className="min-h-screen bg-gradient-to-br from-gray-50 to-slate-100 p-6">
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">⚙️ Cài đặt Hệ thống</h1>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <p className="text-lg text-gray-600">Trang cài đặt đang được phát triển...</p>
      </div>
    </div>
  </div>
);

// Layout Component with Sidebar
const Layout = ({ currentPath, onNavigate, children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  
  const navigationItems = [
    { path: '/', label: 'Trang chủ', icon: '🏠' },
    { path: '/dashboard', label: 'Thống kê & Báo cáo', icon: '📊' },
    { path: '/orders', label: 'Quản lý Đơn hàng', icon: '📦' },
    { path: '/customers', label: 'Quản lý Khách hàng', icon: '👥' },
    { path: '/products', label: 'Quản lý Sản phẩm', icon: '🛍️' },
    { path: '/marketing', label: 'Marketing & Ads', icon: '📢' },
    { path: '/settings', label: 'Cài đặt Hệ thống', icon: '⚙️' }
  ];

  if (currentPath === '/') {
    return children;
  }

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <div className={`${sidebarOpen ? 'w-64' : 'w-16'} bg-white shadow-lg transition-all duration-300 flex flex-col`}>
        {/* Logo & Toggle */}
        <div className="p-4 border-b border-gray-200 flex items-center justify-between">
          {sidebarOpen && (
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center text-white font-bold">
                E
              </div>
              <span className="font-bold text-lg text-gray-800">eCommerce</span>
            </div>
          )}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <span className="text-xl">{sidebarOpen ? '◀' : '▶'}</span>
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          {navigationItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              currentPath={currentPath}
              onNavigate={onNavigate}
              icon={item.icon}
              className={!sidebarOpen ? 'justify-center' : ''}
            >
              {sidebarOpen && item.label}
            </Link>
          ))}
        </nav>

        {/* User Profile */}
        <div className="p-4 border-t border-gray-200">
          <div className={`flex items-center space-x-3 ${!sidebarOpen ? 'justify-center' : ''}`}>
            <div className="w-10 h-10 bg-gradient-to-br from-green-400 to-blue-500 rounded-full flex items-center justify-center text-white font-bold">
              A
            </div>
            {sidebarOpen && (
              <div>
                <p className="font-medium text-gray-800">Admin</p>
                <p className="text-sm text-gray-500">admin@ecommerce.com</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        {children}
      </div>
    </div>
  );
};

// Main App Component
const App = () => {
  const [currentPath, setCurrentPath] = useState('/');

  const navigate = (path) => {
    setCurrentPath(path);
  };

  return (
    <Layout currentPath={currentPath} onNavigate={navigate}>
      <Router>
        <Route path="/" component={HomePage} currentPath={currentPath} />
        <Route path="/dashboard" component={EcommerceDashboard} currentPath={currentPath} />
        <Route path="/orders" component={OrdersPage} currentPath={currentPath} />
        <Route path="/customers" component={CustomersPage} currentPath={currentPath} />
        <Route path="/products" component={ProductsPage} currentPath={currentPath} />
        <Route path="/marketing" component={MarketingPage} currentPath={currentPath} />
        <Route path="/settings" component={SettingsPage} currentPath={currentPath} />
      </Router>
    </Layout>
  );
};

export default App;