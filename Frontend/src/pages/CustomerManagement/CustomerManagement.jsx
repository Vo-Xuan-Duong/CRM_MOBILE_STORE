import React, { useState, useEffect } from 'react';
import { 
  Users, UserPlus, QrCode, Search, Filter, Edit, Eye, Trash2, 
  Plus, Download, Upload, MoreVertical, X, ChevronLeft, ChevronRight, 
  Phone, Mail, MapPin, Calendar, TrendingUp, AlertCircle, FileText,
  Building, Star, Clock, CheckCircle, XCircle
} from 'lucide-react';
import CustomerForm from './CustomerForm/CustomerForm';
import CustomerDetailModal from './CustomerDetailModal/CustomerDetailModal';

const CustomerManagement = () => {
  // States chính
  const [customers, setCustomers] = useState([]);
  const [filteredCustomers, setFilteredCustomers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFilter, setSelectedFilter] = useState('all');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [formMode, setFormMode] = useState('add'); // 'add' or 'edit'

  // UI states
  const [showFilters, setShowFilters] = useState(false);
  const [selectedCustomers, setSelectedCustomers] = useState([]);
  const [sortBy, setSortBy] = useState('name');
  const [sortOrder, setSortOrder] = useState('asc');

  // Dữ liệu mẫu ban đầu
  useEffect(() => {
    const sampleCustomers = [
      {
        id: 1,
        name: 'Nguyễn Văn An',
        email: 'nguyenvanan@email.com',
        phone: '0912345678',
        address: '123 Đường ABC, Quận 1, TP.HCM',
        company: 'Công ty TNHH ABC',
        dateOfBirth: '1990-05-15',
        status: 'active',
        totalOrders: 15,
        totalSpent: 25000000,
        lastPurchase: '2024-01-10',
        createdAt: '2023-06-15',
        notes: 'Khách hàng VIP, thường mua điện thoại cao cấp'
      },
      {
        id: 2,
        name: 'Trần Thị Bình',
        email: 'tranthibinh@email.com',
        phone: '0987654321',
        address: '456 Đường XYZ, Quận 3, TP.HCM',
        company: 'Freelancer',
        dateOfBirth: '1985-08-20',
        status: 'active',
        totalOrders: 8,
        totalSpent: 12000000,
        lastPurchase: '2023-12-25',
        createdAt: '2023-03-10',
        notes: 'Quan tâm đến phụ kiện điện thoại'
      },
      {
        id: 3,
        name: 'Lê Văn Cường',
        email: 'levancuong@email.com',
        phone: '0901234567',
        address: '789 Đường DEF, Quận 5, TP.HCM',
        company: 'Công ty XYZ',
        dateOfBirth: '1988-12-03',
        status: 'inactive',
        totalOrders: 3,
        totalSpent: 5500000,
        lastPurchase: '2023-08-15',
        createdAt: '2023-01-20',
        notes: 'Chưa mua hàng trong thời gian dài'
      }
    ];
    setCustomers(sampleCustomers);
    setFilteredCustomers(sampleCustomers);
  }, []);

  // Tìm kiếm và lọc
  useEffect(() => {
    let filtered = [...customers];

    // Tìm kiếm
    if (searchTerm) {
      filtered = filtered.filter(customer =>
        customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        customer.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        customer.phone.includes(searchTerm) ||
        customer.company.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Lọc theo trạng thái
    if (selectedFilter !== 'all') {
      filtered = filtered.filter(customer => customer.status === selectedFilter);
    }

    // Sắp xếp
    filtered.sort((a, b) => {
      let aValue = a[sortBy];
      let bValue = b[sortBy];
      
      if (typeof aValue === 'string') {
        aValue = aValue.toLowerCase();
        bValue = bValue.toLowerCase();
      }
      
      if (sortOrder === 'asc') {
        return aValue < bValue ? -1 : aValue > bValue ? 1 : 0;
      } else {
        return aValue > bValue ? -1 : aValue < bValue ? 1 : 0;
      }
    });

    setFilteredCustomers(filtered);
    setCurrentPage(1);
  }, [customers, searchTerm, selectedFilter, sortBy, sortOrder]);

  // Pagination
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentCustomers = filteredCustomers.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredCustomers.length / itemsPerPage);

  // Handlers
  const handleAddCustomer = () => {
    setSelectedCustomer(null);
    setFormMode('add');
    setIsFormOpen(true);
  };

  const handleEditCustomer = (customer) => {
    setSelectedCustomer(customer);
    setFormMode('edit');
    setIsFormOpen(true);
  };

  const handleViewCustomer = (customer) => {
    setSelectedCustomer(customer);
    setIsDetailOpen(true);
  };

  const handleDeleteCustomer = (customerId) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa khách hàng này?')) {
      setCustomers(prev => prev.filter(customer => customer.id !== customerId));
      setSelectedCustomers(prev => prev.filter(id => id !== customerId));
    }
  };

  const handleSaveCustomer = (customerData) => {
    if (formMode === 'add') {
      const newCustomer = {
        ...customerData,
        id: Date.now(),
        status: 'active',
        totalOrders: 0,
        totalSpent: 0,
        lastPurchase: null,
        createdAt: new Date().toISOString().split('T')[0]
      };
      setCustomers(prev => [...prev, newCustomer]);
    } else {
      setCustomers(prev => prev.map(customer =>
        customer.id === customerData.id ? { ...customer, ...customerData } : customer
      ));
    }
  };

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedCustomers(currentCustomers.map(customer => customer.id));
    } else {
      setSelectedCustomers([]);
    }
  };

  const handleSelectCustomer = (customerId) => {
    setSelectedCustomers(prev =>
      prev.includes(customerId)
        ? prev.filter(id => id !== customerId)
        : [...prev, customerId]
    );
  };

  const handleBulkDelete = () => {
    if (selectedCustomers.length === 0) return;
    
    if (window.confirm(`Bạn có chắc chắn muốn xóa ${selectedCustomers.length} khách hàng đã chọn?`)) {
      setCustomers(prev => prev.filter(customer => !selectedCustomers.includes(customer.id)));
      setSelectedCustomers([]);
    }
  };

  const handleExportData = () => {
    const csvContent = [
      ['Tên', 'Email', 'Số điện thoại', 'Địa chỉ', 'Công ty', 'Trạng thái', 'Tổng đơn hàng', 'Tổng chi tiêu'],
      ...filteredCustomers.map(customer => [
        customer.name,
        customer.email,
        customer.phone,
        customer.address,
        customer.company,
        customer.status === 'active' ? 'Hoạt động' : 'Không hoạt động',
        customer.totalOrders,
        customer.totalSpent.toLocaleString('vi-VN')
      ])
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `danh-sach-khach-hang-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
  };

  const handleImportData = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const csv = event.target.result;
        const lines = csv.split('\n');
        const headers = lines[0].split(',');
        
        const importedCustomers = lines.slice(1)
          .filter(line => line.trim())
          .map((line, index) => {
            const values = line.split(',');
            return {
              id: Date.now() + index,
              name: values[0] || '',
              email: values[1] || '',
              phone: values[2] || '',
              address: values[3] || '',
              company: values[4] || '',
              status: 'active',
              totalOrders: 0,
              totalSpent: 0,
              lastPurchase: null,
              createdAt: new Date().toISOString().split('T')[0],
              notes: ''
            };
          });

        setCustomers(prev => [...prev, ...importedCustomers]);
        alert(`Đã nhập thành công ${importedCustomers.length} khách hàng!`);
      } catch (error) {
        alert('Có lỗi xảy ra khi nhập dữ liệu. Vui lòng kiểm tra định dạng file.');
      }
    };
    reader.readAsText(file);
    e.target.value = '';
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('vi-VN');
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      active: { color: 'bg-green-100 text-green-800', icon: CheckCircle, text: 'Hoạt động' },
      inactive: { color: 'bg-red-100 text-red-800', icon: XCircle, text: 'Không hoạt động' }
    };
    
    const config = statusConfig[status] || statusConfig.inactive;
    const IconComponent = config.icon;
    
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}>
        <IconComponent className="h-3 w-3 mr-1" />
        {config.text}
      </span>
    );
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen" id="customer-management-page">
      {/* Header */}
      <div className="mb-8" id="customer-header-section">
        <div className="flex items-center justify-between mb-4" id="customer-title-row">
          <div id="customer-title-group">
            <h1 className="text-3xl font-bold text-gray-900 flex items-center" id="customer-main-title">
              <Users className="h-8 w-8 mr-3 text-blue-600" id="customer-icon" />
              Quản lý Khách hàng
            </h1>
            <p className="text-gray-600 mt-1" id="customer-count-text">
              Tổng cộng {filteredCustomers.length} khách hàng
            </p>
          </div>
          
          <div className="flex items-center space-x-3" id="customer-action-buttons">
            <button
              onClick={handleAddCustomer}
              className="flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
              id="add-customer-button"
            >
              <UserPlus className="h-4 w-4 mr-2" />
              Thêm khách hàng
            </button>
            
            <div className="flex items-center space-x-2" id="import-export-buttons">
              <input
                type="file"
                accept=".csv"
                onChange={handleImportData}
                className="hidden"
                id="import-file"
              />
              <label
                htmlFor="import-file"
                className="flex items-center px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg cursor-pointer transition-colors"
                id="import-button"
              >
                <Upload className="h-4 w-4 mr-2" />
                Nhập
              </label>
              
              <button
                onClick={handleExportData}
                className="flex items-center px-3 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-lg transition-colors"
                id="export-button"
              >
                <Download className="h-4 w-4 mr-2" />
                Xuất
              </button>
            </div>
          </div>
        </div>

        {/* Thống kê nhanh */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6" id="customer-stats-grid">
          <div className="bg-white p-4 rounded-lg shadow" id="total-customers-stat">
            <div className="flex items-center">
              <div className="p-2 bg-blue-100 rounded-lg" id="total-customers-icon">
                <Users className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4" id="total-customers-text">
                <p className="text-sm text-gray-600">Tổng khách hàng</p>
                <p className="text-xl font-semibold">{customers.length}</p>
              </div>
            </div>
          </div>
          
          <div className="bg-white p-4 rounded-lg shadow" id="active-customers-stat">
            <div className="flex items-center">
              <div className="p-2 bg-green-100 rounded-lg" id="active-customers-icon">
                <CheckCircle className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4" id="active-customers-text">
                <p className="text-sm text-gray-600">Đang hoạt động</p>
                <p className="text-xl font-semibold">
                  {customers.filter(c => c.status === 'active').length}
                </p>
              </div>
            </div>
          </div>
          
          <div className="bg-white p-4 rounded-lg shadow" id="revenue-stat">
            <div className="flex items-center">
              <div className="p-2 bg-yellow-100 rounded-lg" id="revenue-icon">
                <TrendingUp className="h-6 w-6 text-yellow-600" />
              </div>
              <div className="ml-4" id="revenue-text">
                <p className="text-sm text-gray-600">Doanh thu</p>
                <p className="text-xl font-semibold">
                  {formatCurrency(customers.reduce((sum, c) => sum + c.totalSpent, 0))}
                </p>
              </div>
            </div>
          </div>
          
          <div className="bg-white p-4 rounded-lg shadow" id="orders-stat">
            <div className="flex items-center">
              <div className="p-2 bg-purple-100 rounded-lg" id="orders-icon">
                <Star className="h-6 w-6 text-purple-600" />
              </div>
              <div className="ml-4" id="orders-text">
                <p className="text-sm text-gray-600">Đơn hàng</p>
                <p className="text-xl font-semibold">
                  {customers.reduce((sum, c) => sum + c.totalOrders, 0)}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Tìm kiếm và lọc */}
        <div className="bg-white p-4 rounded-lg shadow mb-6" id="search-filter-section">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0" id="search-filter-container">
            {/* Tìm kiếm */}
            <div className="flex-1 max-w-md" id="search-container">
              <div className="relative" id="search-input-wrapper">
                <Search className="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" id="search-icon" />
                <input
                  type="text"
                  placeholder="Tìm kiếm khách hàng..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  id="customer-search-input"
                />
              </div>
            </div>

            {/* Bộ lọc */}
            <div className="flex items-center space-x-4" id="filter-container">
              <select
                value={selectedFilter}
                onChange={(e) => setSelectedFilter(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                id="status-filter-select"
              >
                <option value="all">Tất cả trạng thái</option>
                <option value="active">Hoạt động</option>
                <option value="inactive">Không hoạt động</option>
              </select>

              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                id="sort-by-select"
              >
                <option value="name">Sắp xếp theo tên</option>
                <option value="createdAt">Sắp xếp theo ngày tạo</option>
                <option value="totalSpent">Sắp xếp theo chi tiêu</option>
                <option value="totalOrders">Sắp xếp theo số đơn</option>
              </select>

              <button
                onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
                className="p-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                title={sortOrder === 'asc' ? 'Tăng dần' : 'Giảm dần'}
                id="sort-order-button"
              >
                {sortOrder === 'asc' ? '↑' : '↓'}
              </button>
            </div>
          </div>
        </div>

        {/* Hành động hàng loạt */}
        {selectedCustomers.length > 0 && (
          <div className="bg-blue-50 border border-blue-200 p-4 rounded-lg mb-4" id="bulk-actions-section">
            <div className="flex items-center justify-between" id="bulk-actions-container">
              <span className="text-blue-800" id="selected-count-text">
                Đã chọn {selectedCustomers.length} khách hàng
              </span>
              <div className="flex items-center space-x-2" id="bulk-action-buttons">
                <button
                  onClick={handleBulkDelete}
                  className="flex items-center px-3 py-1 bg-red-600 hover:bg-red-700 text-white rounded text-sm transition-colors"
                  id="bulk-delete-button"
                >
                  <Trash2 className="h-4 w-4 mr-1" />
                  Xóa đã chọn
                </button>
                <button
                  onClick={() => setSelectedCustomers([])}
                  className="flex items-center px-3 py-1 bg-gray-500 hover:bg-gray-600 text-white rounded text-sm transition-colors"
                  id="deselect-all-button"
                >
                  <X className="h-4 w-4 mr-1" />
                  Bỏ chọn
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Bảng dữ liệu */}
      <div className="bg-white rounded-lg shadow overflow-hidden" id="customer-table-container">
        <div className="overflow-x-auto" id="customer-table-wrapper">
          <table className="w-full" id="customer-table">
            <thead className="bg-gray-50 border-b border-gray-200" id="customer-table-header">
              <tr id="customer-table-header-row">
                <th className="px-6 py-3 text-left" id="select-all-header">
                  <input
                    type="checkbox"
                    checked={selectedCustomers.length === currentCustomers.length && currentCustomers.length > 0}
                    onChange={handleSelectAll}
                    className="rounded border-gray-300 focus:ring-blue-500"
                    id="select-all-checkbox"
                  />
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="customer-name-header">
                  Khách hàng
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="contact-info-header">
                  Liên hệ
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="company-header">
                  Công ty
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="status-header">
                  Trạng thái
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="orders-header">
                  Đơn hàng
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="spending-header">
                  Chi tiêu
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" id="actions-header">
                  Hành động
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200" id="customer-table-body">
              {currentCustomers.map((customer) => (
                <tr key={customer.id} className="hover:bg-gray-50" id={`customer-row-${customer.id}`}>
                  <td className="px-6 py-4 whitespace-nowrap" id={`select-cell-${customer.id}`}>
                    <input
                      type="checkbox"
                      checked={selectedCustomers.includes(customer.id)}
                      onChange={() => handleSelectCustomer(customer.id)}
                      className="rounded border-gray-300 focus:ring-blue-500"
                      id={`select-customer-${customer.id}`}
                    />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap" id={`customer-info-cell-${customer.id}`}>
                    <div className="flex items-center" id={`customer-info-container-${customer.id}`}>
                      <div className="h-10 w-10 flex-shrink-0" id={`customer-avatar-${customer.id}`}>
                        <div className="h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center" id={`customer-avatar-circle-${customer.id}`}>
                          <span className="text-blue-600 font-medium text-sm" id={`customer-avatar-initial-${customer.id}`}>
                            {customer.name.charAt(0).toUpperCase()}
                          </span>
                        </div>
                      </div>
                      <div className="ml-4" id={`customer-text-info-${customer.id}`}>
                        <div className="text-sm font-medium text-gray-900" id={`customer-name-${customer.id}`}>{customer.name}</div>
                        <div className="text-sm text-gray-500" id={`customer-id-${customer.id}`}>ID: {customer.id}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap" id={`contact-info-cell-${customer.id}`}>
                    <div className="text-sm text-gray-900" id={`customer-email-${customer.id}`}>{customer.email}</div>
                    <div className="text-sm text-gray-500" id={`customer-phone-${customer.id}`}>{customer.phone}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900" id={`company-cell-${customer.id}`}>
                    {customer.company}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap" id={`status-cell-${customer.id}`}>
                    {getStatusBadge(customer.status)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900" id={`orders-cell-${customer.id}`}>
                    {customer.totalOrders}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900" id={`spending-cell-${customer.id}`}>
                    {formatCurrency(customer.totalSpent)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium" id={`actions-cell-${customer.id}`}>
                    <div className="flex items-center space-x-2" id={`action-buttons-${customer.id}`}>
                      <button
                        onClick={() => handleViewCustomer(customer)}
                        className="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50"
                        title="Xem chi tiết"
                        id={`view-button-${customer.id}`}
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleEditCustomer(customer)}
                        className="text-green-600 hover:text-green-900 p-1 rounded hover:bg-green-50"
                        title="Chỉnh sửa"
                        id={`edit-button-${customer.id}`}
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDeleteCustomer(customer.id)}
                        className="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50"
                        title="Xóa"
                        id={`delete-button-${customer.id}`}
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="bg-white px-6 py-3 border-t border-gray-200" id="pagination-section">
            <div className="flex items-center justify-between" id="pagination-container">
              <div className="text-sm text-gray-700" id="pagination-info">
                Hiển thị {indexOfFirstItem + 1} đến {Math.min(indexOfLastItem, filteredCustomers.length)} 
                trong tổng số {filteredCustomers.length} khách hàng
              </div>
              <div className="flex items-center space-x-2" id="pagination-controls">
                <button
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  disabled={currentPage === 1}
                  className="p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  id="prev-page-button"
                >
                  <ChevronLeft className="h-4 w-4" />
                </button>
                
                <div className="flex items-center space-x-1" id="page-buttons">
                  {[...Array(totalPages)].map((_, index) => (
                    <button
                      key={index + 1}
                      onClick={() => setCurrentPage(index + 1)}
                      className={`px-3 py-1 rounded-lg ${
                        currentPage === index + 1
                          ? 'bg-blue-600 text-white'
                          : 'border border-gray-300 hover:bg-gray-50'
                      }`}
                      id={`page-button-${index + 1}`}
                    >
                      {index + 1}
                    </button>
                  ))}
                </div>
                
                <button
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                  disabled={currentPage === totalPages}
                  className="p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  id="next-page-button"
                >
                  <ChevronRight className="h-4 w-4" />
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Modals */}
      <CustomerForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSave={handleSaveCustomer}
        customer={selectedCustomer}
        title={formMode === 'add' ? 'Thêm khách hàng mới' : 'Chỉnh sửa khách hàng'}
        id={formMode === 'add' ? 'add-customer-modal' : 'edit-customer-modal'}
      />

      <CustomerDetailModal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        customer={selectedCustomer}
        onEdit={(customer) => {
          setIsDetailOpen(false);
          handleEditCustomer(customer);
        }}
        id="customer-detail-modal"
      />
    </div>
  );
};

export default CustomerManagement;