// CustomerDetailModal.jsx
import './CustomerDetailModal.css';
import React from 'react';
import { 
  X, Edit, Phone, Mail, MapPin, Calendar, Building, 
  User, TrendingUp, ShoppingBag, Clock, Star 
} from 'lucide-react';

const CustomerDetailModal = ({ isOpen, onClose, customer, onEdit }) => {
  if (!isOpen || !customer) return null;

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Chưa có';
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getStatusColor = (status) => {
    return status === 'active' 
      ? 'bg-green-100 text-green-800' 
      : 'bg-red-100 text-red-800';
  };

  const getStatusText = (status) => {
    return status === 'active' ? 'Hoạt động' : 'Không hoạt động';
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center">
            <div className="h-12 w-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
              <User className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">{customer.name}</h2>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(customer.status)}`}>
                {getStatusText(customer.status)}
              </span>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => onEdit(customer)}
              className="flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
            >
              <Edit className="h-4 w-4 mr-2" />
              Chỉnh sửa
            </button>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="h-5 w-5 text-gray-500" />
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="p-6">
          {/* Thông tin cơ bản */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            {/* Thông tin liên hệ */}
            <div className="bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Thông tin liên hệ
              </h3>
              <div className="space-y-3">
                <div className="flex items-center">
                  <Mail className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="text-gray-900">{customer.email}</p>
                  </div>
                </div>
                <div className="flex items-center">
                  <Phone className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="text-sm text-gray-500">Số điện thoại</p>
                    <p className="text-gray-900">{customer.phone}</p>
                  </div>
                </div>
                <div className="flex items-start">
                  <MapPin className="h-5 w-5 text-gray-400 mr-3 mt-1" />
                  <div>
                    <p className="text-sm text-gray-500">Địa chỉ</p>
                    <p className="text-gray-900">{customer.address || 'Chưa có'}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Thông tin cá nhân */}
            <div className="bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Thông tin cá nhân
              </h3>
              <div className="space-y-3">
                <div className="flex items-center">
                  <Calendar className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="text-sm text-gray-500">Ngày sinh</p>
                    <p className="text-gray-900">{formatDate(customer.dateOfBirth)}</p>
                  </div>
                </div>
                <div className="flex items-center">
                  <Building className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="text-sm text-gray-500">Công ty</p>
                    <p className="text-gray-900">{customer.company || 'Chưa có'}</p>
                  </div>
                </div>
                <div className="flex items-center">
                  <Clock className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="text-sm text-gray-500">Ngày tạo</p>
                    <p className="text-gray-900">{formatDate(customer.createdAt)}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Thống kê mua hàng */}
          <div className="mb-8">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              Thống kê mua hàng
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-blue-50 rounded-lg p-4">
                <div className="flex items-center">
                  <div className="p-2 bg-blue-100 rounded-lg">
                    <ShoppingBag className="h-6 w-6 text-blue-600" />
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-blue-600">Tổng đơn hàng</p>
                    <p className="text-2xl font-bold text-blue-900">{customer.totalOrders}</p>
                  </div>
                </div>
              </div>

              <div className="bg-green-50 rounded-lg p-4">
                <div className="flex items-center">
                  <div className="p-2 bg-green-100 rounded-lg">
                    <TrendingUp className="h-6 w-6 text-green-600" />
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-green-600">Tổng chi tiêu</p>
                    <p className="text-2xl font-bold text-green-900">
                      {formatCurrency(customer.totalSpent)}
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-purple-50 rounded-lg p-4">
                <div className="flex items-center">
                  <div className="p-2 bg-purple-100 rounded-lg">
                    <Star className="h-6 w-6 text-purple-600" />
                  </div>
                  <div className="ml-4">
                    <p className="text-sm text-purple-600">Chi tiêu trung bình</p>
                    <p className="text-2xl font-bold text-purple-900">
                      {customer.totalOrders > 0 
                        ? formatCurrency(customer.totalSpent / customer.totalOrders)
                        : formatCurrency(0)
                      }
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Lần mua cuối */}
          {customer.lastPurchase && (
            <div className="mb-8">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Hoạt động gần đây
              </h3>
              <div className="bg-yellow-50 rounded-lg p-4">
                <div className="flex items-center">
                  <Clock className="h-5 w-5 text-yellow-600 mr-3" />
                  <div>
                    <p className="text-sm text-yellow-600">Lần mua cuối</p>
                    <p className="text-yellow-900 font-medium">{formatDate(customer.lastPurchase)}</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Ghi chú */}
          {customer.notes && (
            <div className="mb-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Ghi chú
              </h3>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-gray-700 leading-relaxed">{customer.notes}</p>
              </div>
            </div>
          )}

          {/* Lịch sử đơn hàng (mẫu) */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              Lịch sử đơn hàng gần đây
            </h3>
            <div className="bg-gray-50 rounded-lg p-6 text-center">
              <ShoppingBag className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">
                Chưa có dữ liệu lịch sử đơn hàng
              </p>
              <p className="text-sm text-gray-400 mt-1">
                Tính năng này sẽ được cập nhật trong phiên bản tiếp theo
              </p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-end space-x-4 p-6 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            className="px-4 py-2 text-gray-700 bg-white border border-gray-300 hover:bg-gray-50 rounded-lg transition-colors"
          >
            Đóng
          </button>
          <button
            onClick={() => onEdit(customer)}
            className="flex items-center px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
          >
            <Edit className="h-4 w-4 mr-2" />
            Chỉnh sửa thông tin
          </button>
        </div>
      </div>
    </div>
  );
};

export default CustomerDetailModal;