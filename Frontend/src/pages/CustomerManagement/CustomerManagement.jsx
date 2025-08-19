import React, { useState, useEffect } from 'react';
import {
  Users, UserPlus, QrCode, Search, Edit, Eye, Trash2,
  Download, Upload, X, ChevronLeft, ChevronRight,
  Phone, Mail, MapPin, TrendingUp, Star, CheckCircle, XCircle
} from 'lucide-react';
import CustomerForm from './CustomerForm/CustomerForm';
import CustomerDetailModal from './CustomerDetailModal/CustomerDetailModal';
import './CustomerManagement.css';

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
    const isActive = status === 'active';
    const IconComponent = isActive ? CheckCircle : XCircle;
    return (
      <span className={`status-badge ${isActive ? 'active' : 'inactive'}`}>
        <IconComponent />
        {isActive ? 'VIP' : 'Thường'}
      </span>
    );
  };

  return (
    <div className="customer-management" id="customer-management-page">
      <div className="customer-header">
        <div className="header-section" id="customer-title-row">
          <div className="title-group" id="customer-title-group">
            <h1 id="customer-main-title">
              <Users />
              Quản lý khách hàng
            </h1>
            <p id="customer-count-text">Tổng số: {filteredCustomers.length} khách hàng</p>
          </div>
          <div className="action-buttons" id="customer-action-buttons">
            <button className="btn btn-success" onClick={() => window.location.href = '/qr-scan'}>
              <QrCode /> Quét QR
            </button>
            <input
              type="file"
              accept=".csv"
              onChange={handleImportData}
              id="import-file"
              style={{ display: 'none' }}
            />
            <label htmlFor="import-file" className="btn btn-secondary" id="import-button">
              <Upload /> Import
            </label>
            <button className="btn btn-secondary" onClick={handleExportData} id="export-button">
              <Download /> Export
            </button>
            <button className="btn btn-primary" onClick={handleAddCustomer} id="add-customer-button">
              <UserPlus /> Thêm khách hàng
            </button>
          </div>
        </div>
        <div className="stats-grid" id="customer-stats-grid">
          <div className="stats-card" id="total-customers-stat">
            <div className="stats-content">
              <div className="stats-icon blue" id="total-customers-icon">
                <Users />
              </div>
              <div className="stats-info" id="total-customers-text">
                <h3>{customers.length}</h3>
                <p>Tổng khách hàng</p>
              </div>
            </div>
          </div>
          <div className="stats-card" id="active-customers-stat">
            <div className="stats-content">
              <div className="stats-icon green" id="active-customers-icon">
                <CheckCircle />
              </div>
              <div className="stats-info" id="active-customers-text">
                <h3>{customers.filter(c => c.status === 'active').length}</h3>
                <p>Đang hoạt động</p>
              </div>
            </div>
          </div>
          <div className="stats-card" id="revenue-stat">
            <div className="stats-content">
              <div className="stats-icon yellow" id="revenue-icon">
                <TrendingUp />
              </div>
              <div className="stats-info" id="revenue-text">
                <h3>{formatCurrency(customers.reduce((sum, c) => sum + c.totalSpent, 0))}</h3>
                <p>Doanh thu</p>
              </div>
            </div>
          </div>
          <div className="stats-card" id="orders-stat">
            <div className="stats-content">
              <div className="stats-icon purple" id="orders-icon">
                <Star />
              </div>
              <div className="stats-info" id="orders-text">
                <h3>{customers.reduce((sum, c) => sum + c.totalOrders, 0)}</h3>
                <p>Đơn hàng</p>
              </div>
            </div>
          </div>
        </div>
        <div className="search-filter-section" id="search-filter-section">
          <div className="search-filter-content" id="search-filter-container">
            <div className="search-box" id="search-container">
              <Search id="search-icon" />
              <input
                type="text"
                placeholder="Tìm kiếm theo tên, SĐT, email, mã KH..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
                id="customer-search-input"
              />
            </div>
            <div className="filter-controls" id="filter-container">
              <select
                value={selectedFilter}
                onChange={(e) => setSelectedFilter(e.target.value)}
                className="select-control"
                id="status-filter-select"
              >
                <option value="all">Tất cả trạng thái</option>
                <option value="active">Hoạt động</option>
                <option value="inactive">Không hoạt động</option>
              </select>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="select-control"
                id="sort-by-select"
              >
                <option value="name">Tên A-Z</option>
                <option value="createdAt">Ngày tạo</option>
                <option value="totalSpent">Chi tiêu</option>
                <option value="totalOrders">Số đơn</option>
              </select>
              <button
                onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
                className="sort-button"
                title={sortOrder === 'asc' ? 'Tăng dần' : 'Giảm dần'}
                id="sort-order-button"
              >
                {sortOrder === 'asc' ? '↑' : '↓'}
              </button>
            </div>
          </div>
        </div>
        {selectedCustomers.length > 0 && (
          <div className="bulk-actions" id="bulk-actions-section">
            <span className="bulk-actions-text" id="selected-count-text">
              Đã chọn {selectedCustomers.length} khách hàng
            </span>
            <div className="bulk-actions-buttons" id="bulk-action-buttons">
              <button className="btn btn-danger btn-sm" onClick={handleBulkDelete} id="bulk-delete-button">
                <Trash2 /> Xóa đã chọn
              </button>
              <button className="btn btn-gray btn-sm" onClick={() => setSelectedCustomers([])} id="deselect-all-button">
                <X /> Bỏ chọn
              </button>
            </div>
          </div>
        )}
      </div>

      <div className="data-table-container" id="customer-table-container">
        <div className="data-table-wrapper" id="customer-table-wrapper">
          <table className="data-table" id="customer-table">
            <thead className="table-header" id="customer-table-header">
              <tr id="customer-table-header-row">
                <th id="select-all-header">
                  <input
                    type="checkbox"
                    checked={selectedCustomers.length === currentCustomers.length && currentCustomers.length > 0}
                    onChange={handleSelectAll}
                    id="select-all-checkbox"
                  />
                </th>
                <th id="customer-name-header">Khách hàng</th>
                <th id="contact-info-header">Liên hệ</th>
                <th id="stats-header">Thống kê</th>
                <th id="status-header">Trạng thái</th>
                <th id="actions-header">Thao tác</th>
              </tr>
            </thead>
            <tbody className="table-body" id="customer-table-body">
              {currentCustomers.map((customer) => (
                <tr key={customer.id} id={`customer-row-${customer.id}`}>
                  <td id={`select-cell-${customer.id}`}>
                    <input
                      type="checkbox"
                      checked={selectedCustomers.includes(customer.id)}
                      onChange={() => handleSelectCustomer(customer.id)}
                      id={`select-customer-${customer.id}`}
                    />
                  </td>
                  <td id={`customer-info-cell-${customer.id}`}>
                    <div className="customer-info" id={`customer-info-container-${customer.id}`}>
                      <div className="customer-avatar" id={`customer-avatar-${customer.id}`}>
                        {customer.name.charAt(0).toUpperCase()}
                      </div>
                      <div className="customer-details" id={`customer-text-info-${customer.id}`}>
                        <h4 id={`customer-name-${customer.id}`}>{customer.name}</h4>
                        <p id={`customer-id-${customer.id}`}>Mã: KH{String(customer.id).slice(-3).padStart(3, '0')}</p>
                      </div>
                    </div>
                  </td>
                  <td id={`contact-info-cell-${customer.id}`}>
                    <div className="contact-info">
                      <div className="primary" id={`customer-phone-${customer.id}`}>{customer.phone}</div>
                      <div className="secondary" id={`customer-email-${customer.id}`}>{customer.email}</div>
                    </div>
                  </td>
                  <td id={`stats-cell-${customer.id}`}>
                    <div className="contact-info">
                      <div className="primary" id={`spending-cell-${customer.id}`}>{formatCurrency(customer.totalSpent)}</div>
                      <div className="secondary" id={`orders-cell-${customer.id}`}>{customer.totalOrders} đơn hàng</div>
                    </div>
                  </td>
                  <td id={`status-cell-${customer.id}`}>
                    {getStatusBadge(customer.status)}
                  </td>
                  <td id={`actions-cell-${customer.id}`}>
                    <div className="action-buttons-cell" id={`action-buttons-${customer.id}`}>
                      <button
                        onClick={() => handleViewCustomer(customer)}
                        className="action-btn view"
                        title="Xem chi tiết"
                        id={`view-button-${customer.id}`}
                      >
                        <Eye />
                      </button>
                      <button
                        onClick={() => handleEditCustomer(customer)}
                        className="action-btn edit"
                        title="Chỉnh sửa"
                        id={`edit-button-${customer.id}`}
                      >
                        <Edit />
                      </button>
                      <button
                        onClick={() => handleDeleteCustomer(customer.id)}
                        className="action-btn delete"
                        title="Xóa"
                        id={`delete-button-${customer.id}`}
                      >
                        <Trash2 />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {totalPages > 1 && (
          <div className="pagination" id="pagination-section">
            <div className="pagination-info" id="pagination-info">
              Hiển thị {indexOfFirstItem + 1} đến {Math.min(indexOfLastItem, filteredCustomers.length)} trong tổng số {filteredCustomers.length} khách hàng
            </div>
            <div className="pagination-controls" id="pagination-controls">
              <button
                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                disabled={currentPage === 1}
                className="pagination-btn"
                id="prev-page-button"
              >
                <ChevronLeft />
              </button>
              <div className="pagination-numbers" id="page-buttons">
                {[...Array(totalPages)].map((_, index) => (
                  <button
                    key={index + 1}
                    onClick={() => setCurrentPage(index + 1)}
                    className={`pagination-number ${currentPage === index + 1 ? 'active' : ''}`}
                    id={`page-button-${index + 1}`}
                  >
                    {index + 1}
                  </button>
                ))}
              </div>
              <button
                onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                disabled={currentPage === totalPages}
                className="pagination-btn"
                id="next-page-button"
              >
                <ChevronRight />
              </button>
            </div>
          </div>
        )}
      </div>

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