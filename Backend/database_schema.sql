-- =====================================================
-- CRM MOBILE STORE DATABASE SCHEMA
-- PostgreSQL Database Schema
-- Generated from Spring Boot JPA Models
-- =====================================================

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- 1. AUTHENTICATION & AUTHORIZATION TABLES
-- =====================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    full_name VARCHAR(255),
    password VARCHAR(255),
    profile_picture_url VARCHAR(500)
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(255),
    description VARCHAR(500)
);

-- Permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    permission VARCHAR(255),
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    username VARCHAR(255),
    email VARCHAR(255),
    full_name VARCHAR(255),
    password VARCHAR(255),
    profile_picture_url VARCHAR(500)

-- =====================================================
-- 2. CUSTOMER MANAGEMENT TABLES
-- =====================================================

-- Customers table
    role VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    address TEXT,
    date_of_birth DATE,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'VIP', 'BLACKLISTED')),
    city VARCHAR(50),
    district VARCHAR(50),
    postal_code VARCHAR(10),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customer Loyalty table (One-to-One with Customer)
CREATE TABLE customer_loyalty (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT UNIQUE NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    total_points INTEGER NOT NULL DEFAULT 0,
    used_points INTEGER NOT NULL DEFAULT 0,
    permission VARCHAR(255),
    total_spent DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE' CHECK (tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'VIP')),
    last_purchase_date TIMESTAMP,
    tier_upgrade_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customer Interactions table
CREATE TABLE customer_interactions (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('PHONE_CALL', 'EMAIL', 'SMS', 'IN_PERSON', 'COMPLAINT', 'INQUIRY', 'SUPPORT', 'FOLLOW_UP')),
    subject VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED')),
    follow_up_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- Refresh tokens table

-- Customer Care Tasks table
    token VARCHAR(255),

-- Customer Feedback table
CREATE TABLE customer_feedback (
-- Customer Notifications table
CREATE TABLE customer_notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(30) NOT NULL CHECK (type IN ('PROMOTION', 'BIRTHDAY_GREETING', 'WARRANTY_REMINDER', 'LOYALTY_REWARD', 'NEW_PRODUCT', 'ORDER_UPDATE', 'PAYMENT_REMINDER', 'SATISFACTION_SURVEY', 'GENERAL_INFO')),
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('EMAIL', 'SMS', 'PUSH_NOTIFICATION', 'IN_APP')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED')),
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    campaign_id BIGINT REFERENCES marketing_campaigns(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. CUSTOMER MANAGEMENT TABLES
-- 3. PRODUCT MANAGEMENT TABLES
-- =====================================================

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    -- Thông tin cơ bản
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    -- Thông tin giá cả
    price DECIMAL(12,2) NOT NULL,
    cost DECIMAL(12,2) NOT NULL,
    original_price DECIMAL(12,2),
    discount_percent INTEGER,
-- Customers table
    utility8 VARCHAR(100),
    utility9 VARCHAR(100),
    product_url VARCHAR(500),
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments for products table
COMMENT ON TABLE products IS 'Bảng lưu trữ thông tin sản phẩm điện thoại và phụ kiện';
COMMENT ON COLUMN products.id IS 'Khóa chính của bảng sản phẩm';
COMMENT ON COLUMN products.name IS 'Tên đầy đủ của sản phẩm';
COMMENT ON COLUMN products.sku IS 'Mã SKU duy nhất của sản phẩm';
COMMENT ON COLUMN products.description IS 'Mô tả chi tiết sản phẩm';
COMMENT ON COLUMN products.brand IS 'Thương hiệu sản phẩm (Apple, Samsung, etc.)';
COMMENT ON COLUMN products.model IS 'Model sản phẩm (iPhone 16 Pro Max, etc.)';
COMMENT ON COLUMN products.price IS 'Giá bán hiện tại (VND)';
COMMENT ON COLUMN products.cost IS 'Giá vốn sản phẩm (VND)';
COMMENT ON COLUMN products.original_price IS 'Giá gốc trước khi giảm giá (VND)';
COMMENT ON COLUMN products.discount_percent IS 'Phần trăm giảm giá (-12%, -20%, etc.)';
COMMENT ON COLUMN products.stock_quantity IS 'Số lượng tồn kho hiện tại';
COMMENT ON COLUMN products.min_stock_level IS 'Mức tồn kho tối thiểu để cảnh báo';
COMMENT ON COLUMN products.status IS 'Trạng thái sản phẩm (ACTIVE, INACTIVE, DISCONTINUED, OUT_OF_STOCK)';
COMMENT ON COLUMN products.category IS 'Danh mục sản phẩm (SMARTPHONE, TABLET, ACCESSORY, etc.)';
COMMENT ON COLUMN products.color IS 'Màu sắc sản phẩm (Đen Titan, Xanh, etc.)';
COMMENT ON COLUMN products.storage IS 'Dung lượng lưu trữ (128GB, 256GB, etc.)';
COMMENT ON COLUMN products.operating_system IS 'Hệ điều hành (iOS 18, Android, etc.)';
COMMENT ON COLUMN products.screen_size IS 'Kích thước màn hình (6.9 inch, etc.)';
COMMENT ON COLUMN products.camera IS 'Thông tin camera cơ bản';
COMMENT ON COLUMN products.front_camera IS 'Thông tin camera trước (12 MP, etc.)';
COMMENT ON COLUMN products.back_camera IS 'Thông tin camera sau (Chính 48 MP & Phụ 48 MP, 12 MP)';
COMMENT ON COLUMN products.battery IS 'Thông tin pin cơ bản';
COMMENT ON COLUMN products.warranty IS 'Thông tin bảo hành (12 tháng, etc.)';
COMMENT ON COLUMN products.imei IS 'Mã IMEI duy nhất của điện thoại (15 ký tự)';
COMMENT ON COLUMN products.chip IS 'Thông tin chip xử lý (Apple A18 Pro, etc.)';
COMMENT ON COLUMN products.ram IS 'Dung lượng RAM (8 GB, 6 GB, etc.)';
COMMENT ON COLUMN products.screen_type IS 'Loại màn hình (Super Retina XDR, etc.)';
COMMENT ON COLUMN products.battery_capacity IS 'Dung lượng pin (33 giờ, 3349 mAh, etc.)';
COMMENT ON COLUMN products.charging_speed IS 'Tốc độ sạc (20 W, 25 W, etc.)';
COMMENT ON COLUMN products.sold_count IS 'Số lượng đã bán (232500, 25800, etc.)';
COMMENT ON COLUMN products.rating IS 'Đánh giá sản phẩm (4.9, 4.8, etc.)';
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'VIP', 'BLACKLISTED')),
COMMENT ON COLUMN products.utility8 IS 'Thông tin camera trước (Camera trước: 12 MP)';
COMMENT ON COLUMN products.utility9 IS 'Thông tin pin (Pin 33 giờ, Sạc 20 W)';
COMMENT ON COLUMN products.image_url IS 'URL hình ảnh sản phẩm từ CDN';
COMMENT ON COLUMN products.product_url IS 'URL sản phẩm từ website gốc (TGDD)';
COMMENT ON COLUMN products.updated_at IS 'Thời gian cập nhật sản phẩm';





    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
);

-- =====================================================
-- 5. PAYMENT MANAGEMENT TABLES
-- =====================================================

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    payment_number VARCHAR(30) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    method VARCHAR(20) NOT NULL CHECK (method IN ('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'E_WALLET', 'QR_CODE', 'INSTALLMENT', 'CRYPTO', 'CHECK')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIAL_REFUNDED', 'CHARGEBACK')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('FULL_PAYMENT', 'PARTIAL_PAYMENT', 'DEPOSIT', 'INSTALLMENT', 'REFUND', 'ADVANCE_PAYMENT')),
    transaction_id VARCHAR(100),
    reference_number VARCHAR(100),
    card_last_four_digits VARCHAR(100),
    card_type VARCHAR(50),
    bank_name VARCHAR(100),
    gateway_response VARCHAR(100),
    notes TEXT,
    processed_at TIMESTAMP,
    refunded_at TIMESTAMP,
    refund_amount DECIMAL(12,2),
    refund_reason TEXT,
    processed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Customer Loyalty table (One-to-One with Customer)
-- =====================================================

-- Warranties table
CREATE TABLE warranties (
    customer_id BIGINT UNIQUE NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    purchase_date DATE NOT NULL,
    total_spent DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE' CHECK (tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'VIP')),
    last_purchase_date TIMESTAMP,
    issued_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customer Interactions table
CREATE TABLE customer_interactions (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('PHONE_CALL', 'EMAIL', 'SMS', 'IN_PERSON', 'COMPLAINT', 'INQUIRY', 'SUPPORT', 'FOLLOW_UP')),
    subject VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED')),
-- 3. PRODUCT MANAGEMENT TABLES
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Products table
-- Customer Care Tasks table
CREATE TABLE customer_care_tasks (
    -- Thông tin cơ bản
    title VARCHAR(200) NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'OVERDUE')),
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    brand VARCHAR(50) NOT NULL,
    completed_at TIMESTAMP,
    notes TEXT,
    -- Thông tin giá cả
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
-- Warranty Claims table
CREATE TABLE warranty_claims (
    id BIGSERIAL PRIMARY KEY,
    -- Thông tin kho hàng
    stock_quantity INTEGER NOT NULL,
    min_stock_level INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED', 'OUT_OF_STOCK')),
    category VARCHAR(20) NOT NULL DEFAULT 'SMARTPHONE' CHECK (category IN ('SMARTPHONE', 'TABLET', 'ACCESSORY', 'CASE', 'CHARGER', 'HEADPHONE', 'SMARTWATCH')),
    -- Thông tin kỹ thuật cơ bản
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    subject VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('COMPLAINT', 'COMPLIMENT', 'SUGGESTION', 'REVIEW', 'SUPPORT_REQUEST', 'WARRANTY_ISSUE', 'PRODUCT_INQUIRY', 'GENERAL')),
    rating INTEGER NOT NULL DEFAULT 5 CHECK (rating >= 1 AND rating <= 5),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    response TEXT,
    responded_at TIMESTAMP,
    responded_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customer Notifications table
CREATE TABLE customer_notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    camera VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    back_camera VARCHAR(100),
    technician_notes TEXT,
    submitted_at TIMESTAMP,

-- =====================================================
-- 7. MARKETING TABLES
    -- IMEI cho điện thoại
    imei VARCHAR(15) UNIQUE,
    -- Thông tin kỹ thuật chi tiết
    chip VARCHAR(100),
    ram VARCHAR(20),
    screen_type VARCHAR(100),
    battery_capacity VARCHAR(100),
    charging_speed VARCHAR(20),
    -- Thông tin bán hàng và đánh giá
    sold_count INTEGER,
    rating DECIMAL(2,1),
    vote_text VARCHAR(50),
    title10 VARCHAR(100),
    -- Thông tin bổ sung từ CSV
    utility VARCHAR(100),
    utility5 VARCHAR(100),
    utility6 VARCHAR(100),
    utility7 VARCHAR(200),
    utility8 VARCHAR(100),
    utility9 VARCHAR(100),
    -- Thông tin hình ảnh và URL
CREATE TABLE marketing_campaigns (
    id BIGSERIAL PRIMARY KEY,



-- Orders indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Payments indexes
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_customer_id ON payments(customer_id);
CREATE INDEX idx_payments_status ON payments(status);

-- Warranties indexes
CREATE INDEX idx_warranties_customer_id ON warranties(customer_id);
CREATE INDEX idx_warranties_product_id ON warranties(product_id);
CREATE INDEX idx_warranties_status ON warranties(status);

-- Customer interactions indexes
CREATE INDEX idx_customer_interactions_customer_id ON customer_interactions(customer_id);
CREATE INDEX idx_customer_interactions_user_id ON customer_interactions(user_id);

-- Customer care tasks indexes
CREATE INDEX idx_customer_care_tasks_customer_id ON customer_care_tasks(customer_id);
CREATE INDEX idx_customer_care_tasks_assigned_to ON customer_care_tasks(assigned_to);
CREATE INDEX idx_customer_care_tasks_status ON customer_care_tasks(status);
CREATE INDEX idx_customer_care_tasks_due_date ON customer_care_tasks(due_date);

-- =====================================================
-- TRIGGERS FOR AUTOMATIC UPDATES
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
-- Comments for products table
COMMENT ON TABLE products IS 'Bảng lưu trữ thông tin sản phẩm điện thoại và phụ kiện';
COMMENT ON COLUMN products.id IS 'Khóa chính của bảng sản phẩm';
COMMENT ON COLUMN products.name IS 'Tên đầy đủ của sản phẩm';
COMMENT ON COLUMN products.sku IS 'Mã SKU duy nhất của sản phẩm';
COMMENT ON COLUMN products.description IS 'Mô tả chi tiết sản phẩm';
COMMENT ON COLUMN products.brand IS 'Thương hiệu sản phẩm (Apple, Samsung, etc.)';
COMMENT ON COLUMN products.model IS 'Model sản phẩm (iPhone 16 Pro Max, etc.)';
COMMENT ON COLUMN products.price IS 'Giá bán hiện tại (VND)';
COMMENT ON COLUMN products.cost IS 'Giá vốn sản phẩm (VND)';
COMMENT ON COLUMN products.original_price IS 'Giá gốc trước khi giảm giá (VND)';
COMMENT ON COLUMN products.discount_percent IS 'Phần trăm giảm giá (-12%, -20%, etc.)';
COMMENT ON COLUMN products.stock_quantity IS 'Số lượng tồn kho hiện tại';
COMMENT ON COLUMN products.min_stock_level IS 'Mức tồn kho tối thiểu để cảnh báo';
COMMENT ON COLUMN products.status IS 'Trạng thái sản phẩm (ACTIVE, INACTIVE, DISCONTINUED, OUT_OF_STOCK)';
COMMENT ON COLUMN products.category IS 'Danh mục sản phẩm (SMARTPHONE, TABLET, ACCESSORY, etc.)';
COMMENT ON COLUMN products.color IS 'Màu sắc sản phẩm (Đen Titan, Xanh, etc.)';
COMMENT ON COLUMN products.storage IS 'Dung lượng lưu trữ (128GB, 256GB, etc.)';
COMMENT ON COLUMN products.operating_system IS 'Hệ điều hành (iOS 18, Android, etc.)';
COMMENT ON COLUMN products.screen_size IS 'Kích thước màn hình (6.9 inch, etc.)';
COMMENT ON COLUMN products.camera IS 'Thông tin camera cơ bản';
COMMENT ON COLUMN products.front_camera IS 'Thông tin camera trước (12 MP, etc.)';
COMMENT ON COLUMN products.back_camera IS 'Thông tin camera sau (Chính 48 MP & Phụ 48 MP, 12 MP)';
COMMENT ON COLUMN products.battery IS 'Thông tin pin cơ bản';
COMMENT ON COLUMN products.warranty IS 'Thông tin bảo hành (12 tháng, etc.)';
COMMENT ON COLUMN products.imei IS 'Mã IMEI duy nhất của điện thoại (15 ký tự)';
COMMENT ON COLUMN products.chip IS 'Thông tin chip xử lý (Apple A18 Pro, etc.)';
COMMENT ON COLUMN products.ram IS 'Dung lượng RAM (8 GB, 6 GB, etc.)';
COMMENT ON COLUMN products.screen_type IS 'Loại màn hình (Super Retina XDR, etc.)';
COMMENT ON COLUMN products.battery_capacity IS 'Dung lượng pin (33 giờ, 3349 mAh, etc.)';
COMMENT ON COLUMN products.charging_speed IS 'Tốc độ sạc (20 W, 25 W, etc.)';
COMMENT ON COLUMN products.sold_count IS 'Số lượng đã bán (232500, 25800, etc.)';
COMMENT ON COLUMN products.rating IS 'Đánh giá sản phẩm (4.9, 4.8, etc.)';

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
-- 4. ORDER MANAGEMENT TABLES
CREATE TRIGGER update_warranty_claims_updated_at BEFORE UPDATE ON warranty_claims FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_customer_care_tasks_updated_at BEFORE UPDATE ON customer_care_tasks FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_customer_feedback_updated_at BEFORE UPDATE ON customer_feedback FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_customer_loyalty_updated_at BEFORE UPDATE ON customer_loyalty FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    -- Timestamps
('USER_UPDATE', 'Update user information'),
('USER_DELETE', 'Delete users'),
('CUSTOMER_CREATE', 'Create customers'),
('CUSTOMER_READ', 'Read customer information'),
('CUSTOMER_UPDATE', 'Update customer information'),
('CUSTOMER_DELETE', 'Delete customers'),
('ORDER_CREATE', 'Create orders'),
('ORDER_READ', 'Read order information'),
('ORDER_UPDATE', 'Update order information'),
-- Orders table
('PRODUCT_CREATE', 'Create products'),
('PRODUCT_READ', 'Read product information'),
    order_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
-- Assign all permissions to admin role
    discount_amount DECIMAL(12,2) NOT NULL,
-- Insert sample products with detailed iPhone data from CSV
INSERT INTO products (name, sku, description, brand, model, price, cost, stock_quantity, min_stock_level, category, color, storage, operating_system, screen_size, camera, front_camera, back_camera, battery, warranty, image_url, imei, chip, ram, screen_type, battery_capacity, charging_speed, sold_count, rating, product_url, discount_percent, original_price, utility, utility5, utility6, utility7, utility8, utility9, vote_text, title10) VALUES 
('iPhone 16 Pro Max 256GB', 'IP16PM-256-BLK', 'iPhone 16 Pro Max 256GB Đen Titan', 'Apple', 'iPhone 16 Pro Max', 30090000, 24000000, 15, 5, 'SMARTPHONE', 'Đen Titan', '256GB', 'iOS 18', '6.9 inch', 'Camera sau: Chính 48 MP & Phụ 48 MP, 12 MP', '12 MP', 'Chính 48 MP & Phụ 48 MP, 12 MP', 'Pin 33 giờ', '12 tháng', 'https://cdn.tgdd.vn/Products/Images/42/329149/iphone-16-pro-max-sa-mac-thumb-1-600x600.jpg', '123456789012345', 'Chip Apple A18 Pro 6 nhân', '8 GB', 'Super Retina XDR', '33 giờ', '20 W', 232500, 4.9, 'https://www.thegioididong.com/dtdd/iphone-16-pro-max', -12, 34290000, 'Super Retina XDR', 'RAM: 8 GB', 'Dung lượng: 256 GB', 'Camera sau: Chính 48 MP & Phụ 48 MP, 12 MP', 'Camera trước: 12 MP', 'Pin 33 giờ,  Sạc 20 W', '4.9', '• Đã bán 232,5k'),
COMMENT ON COLUMN users.full_name IS 'Họ và tên đầy đủ của nhân viên';
COMMENT ON COLUMN users.password IS 'Mật khẩu đã mã hóa';
COMMENT ON COLUMN users.profile_picture_url IS 'Đường dẫn ảnh đại diện';

COMMENT ON TABLE roles IS 'Bảng định nghĩa các vai trò trong hệ thống';
COMMENT ON COLUMN roles.id IS 'ID duy nhất của vai trò';
COMMENT ON COLUMN roles.role IS 'Tên vai trò (ADMIN, MANAGER, SALES, etc.)';
COMMENT ON COLUMN roles.description IS 'Mô tả chi tiết về vai trò';

COMMENT ON TABLE permissions IS 'Bảng quản lý các quyền hạn chi tiết';
COMMENT ON COLUMN permissions.id IS 'ID duy nhất của quyền';
COMMENT ON COLUMN permissions.permission IS 'Tên quyền (USER_CREATE, CUSTOMER_READ, etc.)';
COMMENT ON COLUMN permissions.description IS 'Mô tả chi tiết về quyền';
COMMENT ON COLUMN permissions.created_at IS 'Thời gian tạo quyền';
COMMENT ON COLUMN permissions.updated_at IS 'Thời gian cập nhật quyền';

    sales_person_id BIGINT REFERENCES users(id) ON DELETE SET NULL,

-- =====================================================
-- 2. BẢNG QUẢN LÝ KHÁCH HÀNG
-- =====================================================

COMMENT ON TABLE customers IS 'Bảng lưu thông tin chi tiết của khách hàng';
COMMENT ON COLUMN customers.id IS 'ID duy nhất của khách hàng';
COMMENT ON COLUMN customers.first_name IS 'Tên của khách hàng';
COMMENT ON COLUMN customers.last_name IS 'Họ của khách hàng';
COMMENT ON COLUMN customers.phone_number IS 'Số điện thoại liên lạc (duy nhất)';
COMMENT ON COLUMN customers.email IS 'Email liên lạc (duy nhất)';
COMMENT ON COLUMN customers.address IS 'Địa chỉ chi tiết của khách hàng';
COMMENT ON COLUMN customers.date_of_birth IS 'Ngày sinh của khách hàng';
COMMENT ON COLUMN customers.gender IS 'Giới tính (MALE/FEMALE/OTHER)';
COMMENT ON COLUMN customers.status IS 'Trạng thái khách hàng (ACTIVE/INACTIVE/VIP/BLACKLISTED)';
COMMENT ON COLUMN customers.city IS 'Thành phố';
COMMENT ON COLUMN customers.district IS 'Quận/Huyện';
    delivered_at TIMESTAMP

COMMENT ON TABLE customer_loyalty IS 'Bảng quản lý chương trình tích điểm và phân cấp khách hàng';
COMMENT ON COLUMN customer_loyalty.id IS 'ID duy nhất của loyalty record';
COMMENT ON COLUMN customer_loyalty.customer_id IS 'ID của khách hàng (quan hệ 1-1)';
COMMENT ON COLUMN customer_loyalty.total_points IS 'Tổng điểm đã tích lũy';
COMMENT ON COLUMN customer_loyalty.used_points IS 'Số điểm đã sử dụng';
COMMENT ON COLUMN customer_loyalty.available_points IS 'Số điểm có thể sử dụng';
COMMENT ON COLUMN customer_loyalty.total_spent IS 'Tổng tiền đã chi tiêu';
COMMENT ON COLUMN customer_loyalty.tier IS 'Cấp độ khách hàng (BRONZE/SILVER/GOLD/PLATINUM/VIP)';
COMMENT ON COLUMN customer_loyalty.last_purchase_date IS 'Ngày mua hàng gần nhất';
COMMENT ON COLUMN customer_loyalty.tier_upgrade_date IS 'Ngày nâng cấp cấp độ';

COMMENT ON TABLE customer_interactions IS 'Bảng lưu lịch sử tương tác với khách hàng';
-- Order Items table
COMMENT ON COLUMN customer_interactions.customer_id IS 'ID của khách hàng';
COMMENT ON COLUMN customer_interactions.user_id IS 'ID của nhân viên tương tác';
COMMENT ON COLUMN customer_interactions.type IS 'Loại tương tác (PHONE_CALL/EMAIL/SMS/IN_PERSON/COMPLAINT/INQUIRY/SUPPORT/FOLLOW_UP)';
COMMENT ON COLUMN customer_interactions.subject IS 'Tiêu đề của tương tác';
COMMENT ON COLUMN customer_interactions.description IS 'Nội dung chi tiết của tương tác';
COMMENT ON COLUMN customer_interactions.status IS 'Trạng thái tương tác (OPEN/IN_PROGRESS/RESOLVED/CLOSED/CANCELLED)';
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,

COMMENT ON TABLE customer_care_tasks IS 'Bảng quản lý nhiệm vụ chăm sóc khách hàng';
COMMENT ON COLUMN customer_care_tasks.id IS 'ID duy nhất của task';
COMMENT ON COLUMN customer_care_tasks.customer_id IS 'ID của khách hàng cần chăm sóc';
COMMENT ON COLUMN customer_care_tasks.title IS 'Tiêu đề nhiệm vụ';
COMMENT ON COLUMN customer_care_tasks.description IS 'Mô tả chi tiết nhiệm vụ';
COMMENT ON COLUMN customer_care_tasks.type IS 'Loại nhiệm vụ (FOLLOW_UP_CALL/BIRTHDAY_GREETING/WARRANTY_REMINDER/SATISFACTION_SURVEY/WINBACK_CONTACT/LOYALTY_REWARD/PRODUCT_INTRODUCTION/COMPLAINT_RESOLUTION/SERVICE_UPGRADE/GENERAL_CARE)';
COMMENT ON COLUMN customer_care_tasks.status IS 'Trạng thái nhiệm vụ (PENDING/IN_PROGRESS/COMPLETED/CANCELLED/OVERDUE)';
-- 5. PAYMENT MANAGEMENT TABLES
COMMENT ON COLUMN customer_care_tasks.due_date IS 'Hạn hoàn thành nhiệm vụ';
COMMENT ON COLUMN customer_care_tasks.completed_at IS 'Thời gian hoàn thành';
-- Payments table
COMMENT ON COLUMN customer_care_tasks.completion_notes IS 'Ghi chú khi hoàn thành';
COMMENT ON COLUMN customer_care_tasks.assigned_to IS 'ID nhân viên được giao nhiệm vụ';
    payment_number VARCHAR(30) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
COMMENT ON COLUMN customer_feedback.order_id IS 'ID đơn hàng liên quan (nếu có)';
COMMENT ON COLUMN customer_feedback.subject IS 'Tiêu đề phản hồi';
COMMENT ON COLUMN customer_feedback.content IS 'Nội dung chi tiết phản hồi';
COMMENT ON COLUMN customer_feedback.type IS 'Loại phản hồi (COMPLAINT/COMPLIMENT/SUGGESTION/REVIEW/SUPPORT_REQUEST/WARRANTY_ISSUE/PRODUCT_INQUIRY/GENERAL)';
COMMENT ON COLUMN customer_feedback.response IS 'Phản hồi của nhân viên';
COMMENT ON COLUMN customer_feedback.responded_at IS 'Thời gian phản hồi';
COMMENT ON COLUMN customer_feedback.responded_by IS 'ID nhân viên phản hồi';

COMMENT ON TABLE customer_notifications IS 'Bảng quản lý thông báo gửi đến khách hàng';
COMMENT ON COLUMN customer_notifications.id IS 'ID duy nhất của notification';
COMMENT ON COLUMN customer_notifications.customer_id IS 'ID khách hàng nhận thông báo';
COMMENT ON COLUMN customer_notifications.title IS 'Tiêu đề thông báo';
COMMENT ON COLUMN customer_notifications.channel IS 'Kênh gửi thông báo (EMAIL/SMS/PUSH_NOTIFICATION/IN_APP)';
COMMENT ON COLUMN customer_notifications.status IS 'Trạng thái gửi (PENDING/SENT/DELIVERED/READ/FAILED)';
COMMENT ON COLUMN customer_notifications.scheduled_at IS 'Thời gian lên lịch gửi';
COMMENT ON COLUMN customer_notifications.sent_at IS 'Thời gian đã gửi';
COMMENT ON COLUMN customer_notifications.read_at IS 'Thời gian khách hàng đọc';
COMMENT ON COLUMN customer_notifications.error_message IS 'Thông báo lỗi nếu gửi thất bại';
COMMENT ON COLUMN customer_notifications.campaign_id IS 'ID chiến dịch marketing liên quan';
    processed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
-- =====================================================

COMMENT ON TABLE products IS 'Bảng quản lý kho sản phẩm điện thoại và phụ kiện';
COMMENT ON COLUMN products.id IS 'ID duy nhất của sản phẩm';
COMMENT ON COLUMN products.name IS 'Tên sản phẩm';
COMMENT ON COLUMN products.sku IS 'Mã SKU duy nhất của sản phẩm';
COMMENT ON COLUMN products.description IS 'Mô tả chi tiết sản phẩm';
-- 6. WARRANTY MANAGEMENT TABLES
COMMENT ON COLUMN products.model IS 'Model sản phẩm';
COMMENT ON COLUMN products.price IS 'Giá bán ra';
COMMENT ON COLUMN products.cost IS 'Giá vốn';
COMMENT ON COLUMN products.stock_quantity IS 'Số lượng tồn kho';
-- Warranties table
COMMENT ON COLUMN products.status IS 'Trạng thái sản phẩm (ACTIVE/INACTIVE/DISCONTINUED/OUT_OF_STOCK)';
COMMENT ON COLUMN products.category IS 'Danh mục sản phẩm (SMARTPHONE/TABLET/ACCESSORY/CASE/CHARGER/HEADPHONE/SMARTWATCH)';
    warranty_number VARCHAR(30) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
COMMENT ON COLUMN products.warranty IS 'Thông tin bảo hành';
COMMENT ON COLUMN products.image_url IS 'Đường dẫn ảnh sản phẩm';
COMMENT ON COLUMN products.imei IS 'Số IMEI của sản phẩm (15 chữ số)';
    warranty_period_months INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'CLAIMED', 'VOIDED', 'TRANSFERRED')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('MANUFACTURER', 'EXTENDED', 'STORE', 'PREMIUM')),
COMMENT ON COLUMN products.utility7 IS 'Thông tin camera sau (Camera sau: Chính 48 MP & Phụ 48 MP, 12 MP)';
COMMENT ON COLUMN products.utility8 IS 'Thông tin camera trước (Camera trước: 12 MP)';
COMMENT ON COLUMN products.utility9 IS 'Thông tin pin (Pin 33 giờ, Sạc 20 W)';
COMMENT ON COLUMN products.vote_text IS 'Đánh giá text (4.9)';
    serial_number VARCHAR(100),
    imei VARCHAR(100),
    issued_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
COMMENT ON COLUMN order_items.product_id IS 'ID sản phẩm';
COMMENT ON COLUMN order_items.quantity IS 'Số lượng sản phẩm';
COMMENT ON COLUMN order_items.unit_price IS 'Đơn giá sản phẩm';
COMMENT ON COLUMN order_items.total_price IS 'Tổng tiền cho sản phẩm này';
COMMENT ON COLUMN order_items.discount_amount IS 'Số tiền giảm giá cho sản phẩm này';

-- =====================================================
-- 5. BẢNG QUẢN LÝ THANH TOÁN
-- =====================================================

COMMENT ON TABLE payments IS 'Bảng quản lý các giao dịch thanh toán';
COMMENT ON COLUMN payments.id IS 'ID duy nhất của payment';
COMMENT ON COLUMN payments.payment_number IS 'Mã giao dịch thanh toán duy nhất';
COMMENT ON COLUMN payments.order_id IS 'ID đơn hàng liên quan';
-- Warranty Claims table
COMMENT ON COLUMN payments.amount IS 'Số tiền thanh toán';
COMMENT ON COLUMN payments.method IS 'Phương thức thanh toán (CASH/CREDIT_CARD/DEBIT_CARD/BANK_TRANSFER/E_WALLET/QR_CODE/INSTALLMENT/CRYPTO/CHECK)';
COMMENT ON COLUMN payments.status IS 'Trạng thái thanh toán (PENDING/PROCESSING/COMPLETED/FAILED/CANCELLED/REFUNDED/PARTIAL_REFUNDED/CHARGEBACK)';
COMMENT ON COLUMN payments.type IS 'Loại thanh toán (FULL_PAYMENT/PARTIAL_PAYMENT/DEPOSIT/INSTALLMENT/REFUND/ADVANCE_PAYMENT)';
COMMENT ON COLUMN payments.transaction_id IS 'Mã giao dịch từ cổng thanh toán';
COMMENT ON COLUMN payments.reference_number IS 'Số tham chiếu';
COMMENT ON COLUMN payments.card_last_four_digits IS '4 số cuối thẻ';
COMMENT ON COLUMN payments.card_type IS 'Loại thẻ (VISA/MASTERCARD/etc.)';
COMMENT ON COLUMN payments.bank_name IS 'Tên ngân hàng';
COMMENT ON COLUMN payments.gateway_response IS 'Phản hồi từ cổng thanh toán';
COMMENT ON COLUMN payments.processed_at IS 'Thời gian xử lý thanh toán';
COMMENT ON COLUMN payments.refunded_at IS 'Thời gian hoàn tiền';
COMMENT ON COLUMN payments.refund_amount IS 'Số tiền hoàn';
COMMENT ON COLUMN payments.refund_reason IS 'Lý do hoàn tiền';
COMMENT ON COLUMN payments.processed_by IS 'ID nhân viên xử lý thanh toán';

-- =====================================================
-- 6. BẢNG QUẢN LÝ BẢO HÀNH
-- =====================================================

COMMENT ON TABLE warranties IS 'Bảng quản lý thông tin bảo hành sản phẩm';
COMMENT ON COLUMN warranties.id IS 'ID duy nhất của warranty';
COMMENT ON COLUMN warranties.warranty_number IS 'Mã bảo hành duy nhất';
    claim_number VARCHAR(30) UNIQUE NOT NULL,
    warranty_id BIGINT NOT NULL REFERENCES warranties(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
COMMENT ON COLUMN warranties.warranty_end_date IS 'Ngày kết thúc bảo hành';
COMMENT ON COLUMN warranties.warranty_period_months IS 'Thời hạn bảo hành (tháng)';
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'IN_PROGRESS', 'COMPLETED', 'REJECTED', 'CANCELLED', 'ON_HOLD')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('REPAIR', 'REPLACEMENT', 'REFUND', 'PARTS_REPLACEMENT', 'SOFTWARE_ISSUE', 'HARDWARE_ISSUE')),
    priority VARCHAR(10) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
COMMENT ON TABLE warranty_claims IS 'Bảng quản lý khiếu nại bảo hành';
COMMENT ON COLUMN warranty_claims.id IS 'ID duy nhất của claim';
COMMENT ON COLUMN warranty_claims.claim_number IS 'Mã khiếu nại duy nhất';
COMMENT ON COLUMN warranty_claims.warranty_id IS 'ID bảo hành liên quan';
COMMENT ON COLUMN warranty_claims.detailed_description IS 'Mô tả chi tiết vấn đề';
COMMENT ON COLUMN warranty_claims.status IS 'Trạng thái khiếu nại (SUBMITTED/UNDER_REVIEW/APPROVED/IN_PROGRESS/COMPLETED/REJECTED/CANCELLED/ON_HOLD)';
COMMENT ON COLUMN warranty_claims.type IS 'Loại khiếu nại (REPAIR/REPLACEMENT/REFUND/PARTS_REPLACEMENT/SOFTWARE_ISSUE/HARDWARE_ISSUE)';
COMMENT ON COLUMN warranty_claims.priority IS 'Mức độ ưu tiên (LOW/MEDIUM/HIGH/URGENT)';
COMMENT ON COLUMN warranty_claims.estimated_cost IS 'Chi phí ước tính';
    technician_notes TEXT,
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    approved_at TIMESTAMP,
COMMENT ON COLUMN warranty_claims.actual_cost IS 'Chi phí thực tế';
COMMENT ON COLUMN warranty_claims.resolution IS 'Giải pháp xử lý';
COMMENT ON COLUMN warranty_claims.technician_notes IS 'Ghi chú của kỹ thuật viên';
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    submitted_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reviewed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    assigned_technician BIGINT REFERENCES users(id) ON DELETE SET NULL,
COMMENT ON COLUMN warranty_claims.submitted_by IS 'ID người gửi khiếu nại';
-- 7. MARKETING TABLES
COMMENT ON COLUMN warranty_claims.assigned_technician IS 'ID kỹ thuật viên được giao';

-- =====================================================
-- Marketing Campaigns table
