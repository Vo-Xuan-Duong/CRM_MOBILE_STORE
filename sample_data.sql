-- ======================================
-- SAMPLE DATA FOR CRM MOBILE STORE
-- ======================================

-- 1. Brands (Thương hiệu)
INSERT INTO brands (name, code, description, logo_url, website_url, country, founded_year, contact_email, contact_phone, address, status, product_count, popularity_score, created_at, updated_at) VALUES
('Apple', 'APPLE', 'Công ty công nghệ hàng đầu thế giới chuyên về thiết bị di động và máy tính', 'https://example.com/apple-logo.png', 'https://www.apple.com', 'United States', '1976', 'support@apple.com', '+1-800-APL-CARE', '1 Apple Park Way, Cupertino, CA 95014', 'ACTIVE', 25, 95, NOW(), NOW()),
('Samsung', 'SAMSUNG', 'Tập đoàn công nghệ đa quốc gia Hàn Quốc', 'https://example.com/samsung-logo.png', 'https://www.samsung.com', 'South Korea', '1938', 'support@samsung.com', '+82-2-2255-0114', 'Samsung Tower, Seoul, South Korea', 'ACTIVE', 45, 90, NOW(), NOW()),
('Xiaomi', 'XIAOMI', 'Công ty công nghệ Trung Quốc chuyên về smartphone giá rẻ', 'https://example.com/xiaomi-logo.png', 'https://www.mi.com', 'China', '2010', 'support@mi.com', '+86-400-100-5678', 'Xiaomi Campus, Beijing, China', 'ACTIVE', 35, 85, NOW(), NOW()),
('Oppo', 'OPPO', 'Thương hiệu smartphone nổi tiếng với camera selfie', 'https://example.com/oppo-logo.png', 'https://www.oppo.com', 'China', '2004', 'support@oppo.com', '+86-769-8538-8888', 'OPPO Mobile, Dongguan, China', 'ACTIVE', 28, 80, NOW(), NOW()),
('Vivo', 'VIVO', 'Thương hiệu smartphone tập trung vào âm nhạc và nhiếp ảnh', 'https://example.com/vivo-logo.png', 'https://www.vivo.com', 'China', '2009', 'support@vivo.com', '+86-769-8228-8888', 'Vivo Mobile, Dongguan, China', 'ACTIVE', 22, 75, NOW(), NOW());

-- 2. Permissions (Quyền hạn)
INSERT INTO permissions (permission, description, created_at, updated_at) VALUES
('CUSTOMER_READ', 'Xem thông tin khách hàng', NOW(), NOW()),
('CUSTOMER_CREATE', 'Tạo khách hàng mới', NOW(), NOW()),
('CUSTOMER_UPDATE', 'Cập nhật thông tin khách hàng', NOW(), NOW()),
('CUSTOMER_DELETE', 'Xóa khách hàng', NOW(), NOW()),
('PRODUCT_READ', 'Xem thông tin sản phẩm', NOW(), NOW()),
('PRODUCT_CREATE', 'Tạo sản phẩm mới', NOW(), NOW()),
('PRODUCT_UPDATE', 'Cập nhật thông tin sản phẩm', NOW(), NOW()),
('PRODUCT_DELETE', 'Xóa sản phẩm', NOW(), NOW()),
('ORDER_READ', 'Xem đơn hàng', NOW(), NOW()),
('ORDER_CREATE', 'Tạo đơn hàng mới', NOW(), NOW()),
('ORDER_UPDATE', 'Cập nhật đơn hàng', NOW(), NOW()),
('ORDER_DELETE', 'Xóa đơn hàng', NOW(), NOW()),
('PAYMENT_READ', 'Xem thông tin thanh toán', NOW(), NOW()),
('PAYMENT_CREATE', 'Tạo thanh toán mới', NOW(), NOW()),
('WARRANTY_READ', 'Xem thông tin bảo hành', NOW(), NOW()),
('WARRANTY_CREATE', 'Tạo bảo hành mới', NOW(), NOW()),
('REPORT_VIEW', 'Xem báo cáo', NOW(), NOW()),
('USER_MANAGEMENT', 'Quản lý người dùng', NOW(), NOW()),
('SYSTEM_CONFIG', 'Cấu hình hệ thống', NOW(), NOW());

-- 3. Roles (Vai trò)
INSERT INTO roles (role, description) VALUES
('ADMIN', 'Quản trị viên hệ thống có toàn quyền'),
('MANAGER', 'Quản lý cửa hàng'),
('SALES_STAFF', 'Nhân viên bán hàng'),
('CUSTOMER_SERVICE', 'Nhân viên chăm sóc khách hàng');

-- 4. Role-Permission mapping
INSERT INTO role_permissions (role_id, permission_id) VALUES
-- ADMIN có tất cả quyền
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19),
-- MANAGER có hầu hết quyền trừ system config
(2, 1), (2, 2), (2, 3), (2, 5), (2, 6), (2, 7), (2, 9), (2, 10), (2, 11), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18),
-- SALES_STAFF có quyền cơ bản về khách hàng và đơn hàng
(3, 1), (3, 2), (3, 3), (3, 5), (3, 9), (3, 10), (3, 11), (3, 13), (3, 14), (3, 15), (3, 16),
-- CUSTOMER_SERVICE có quyền về khách hàng và bảo hành
(4, 1), (4, 2), (4, 3), (4, 5), (4, 9), (4, 13), (4, 15), (4, 16);

-- 5. Users (Người dùng)
INSERT INTO users (username, email, full_name, password, phone_number, profile_picture_url, is_enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, created_at, updated_at) VALUES
('admin', 'admin@crm-mobile.com', 'Quản trị viên hệ thống', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '0901000001', 'https://example.com/admin.jpg', true, true, true, true, NOW(), NOW()),
('manager', 'manager@crm-mobile.com', 'Nguyễn Văn Quản Lý', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '0901000002', 'https://example.com/manager.jpg', true, true, true, true, NOW(), NOW()),
('sales01', 'sales01@crm-mobile.com', 'Trần Thị Bán Hàng', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '0901000003', 'https://example.com/sales01.jpg', true, true, true, true, NOW(), NOW()),
('service01', 'service01@crm-mobile.com', 'Lê Văn Chăm Sóc', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '0901000004', 'https://example.com/service01.jpg', true, true, true, true, NOW(), NOW());

-- 6. User-Role mapping
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- manager -> MANAGER
(3, 3), -- sales01 -> SALES_STAFF
(4, 4); -- service01 -> CUSTOMER_SERVICE

-- 7. Customers (Khách hàng)
INSERT INTO customers (first_name, last_name, phone_number, email, address, date_of_birth, gender, status, city, district, postal_code, notes, created_at, updated_at) VALUES
('Nguyễn', 'Văn An', '0901234567', 'nguyenvanan@email.com', '123 Đường ABC, Phường 1', '1990-05-15', 'MALE', 'ACTIVE', 'Hồ Chí Minh', 'Quận 1', '70000', 'Khách hàng thân thiết, hay mua iPhone', NOW(), NOW()),
('Trần', 'Thị Bình', '0901234568', 'tranthibinh@email.com', '456 Đường XYZ, Phường 2', '1985-08-20', 'FEMALE', 'VIP', 'Hồ Chí Minh', 'Quận 3', '70000', 'Khách VIP, mua nhiều sản phẩm cao cấp', NOW(), NOW()),
('Lê', 'Văn Cường', '0901234569', 'levancuong@email.com', '789 Đường DEF, Phường 3', '1992-12-10', 'MALE', 'ACTIVE', 'Hà Nội', 'Quận Ba Đình', '10000', 'Khách hàng mới, quan tâm Samsung', NOW(), NOW()),
('Phạm', 'Thị Dung', '0901234570', 'phamthidung@email.com', '321 Đường GHI, Phường 4', '1988-03-25', 'FEMALE', 'ACTIVE', 'Đà Nẵng', 'Quận Hải Châu', '50000', 'Thích sản phẩm Xiaomi giá rẻ', NOW(), NOW()),
('Hoàng', 'Văn Em', '0901234571', 'hoangvanem@email.com', '654 Đường JKL, Phường 5', '1995-07-18', 'MALE', 'INACTIVE', 'Hồ Chí Minh', 'Quận 7', '70000', 'Lâu không mua hàng, cần chăm sóc', NOW(), NOW());

-- 8. Products (Sản phẩm)
INSERT INTO products (name, sku, imei, description, brand_id, model, price, cost, original_price, discount_percent, stock_quantity, min_stock_level, status, category, color, storage, operating_system, screen_size, chip, ram, battery_capacity, camera, front_camera, back_camera, image_url, created_at, updated_at) VALUES
('iPhone 15 Pro Max', 'IP15PM-256-BLU', '123456789012345', 'iPhone 15 Pro Max với camera 48MP và chip A17 Pro', 1, 'A3108', 29990000, 25000000, 32990000, 10, 50, 10, 'ACTIVE', 'SMARTPHONE', 'Blue Titanium', '256GB', 'iOS 17', '6.7 inch', 'A17 Pro', '8GB', '4441mAh', '48MP Main + 12MP Ultra Wide + 12MP Telephoto', '12MP TrueDepth', '48MP + 12MP + 12MP', 'https://example.com/iphone15promax.jpg', NOW(), NOW()),
('Samsung Galaxy S24 Ultra', 'SGS24U-512-BLK', '123456789012346', 'Galaxy S24 Ultra với S Pen và camera 200MP', 2, 'SM-S928B', 27990000, 23000000, 29990000, 7, 35, 8, 'ACTIVE', 'SMARTPHONE', 'Titanium Black', '512GB', 'Android 14', '6.8 inch', 'Snapdragon 8 Gen 3', '12GB', '5000mAh', '200MP Main + 50MP Periscope + 12MP Ultra Wide + 10MP Telephoto', '12MP', '200MP + 50MP + 12MP + 10MP', 'https://example.com/galaxys24ultra.jpg', NOW(), NOW()),
('Xiaomi 14 Ultra', 'XM14U-512-WHT', '123456789012347', 'Xiaomi 14 Ultra với Leica camera và sạc nhanh 90W', 3, '2405CPX3DG', 19990000, 16000000, 21990000, 9, 25, 5, 'ACTIVE', 'SMARTPHONE', 'White', '512GB', 'Android 14', '6.73 inch', 'Snapdragon 8 Gen 3', '16GB', '5300mAh', '50MP Main + 50MP Ultra Wide + 50MP Telephoto + 50MP Periscope', '32MP', '50MP + 50MP + 50MP + 50MP', 'https://example.com/xiaomi14ultra.jpg', NOW(), NOW()),
('OPPO Find X7 Ultra', 'OPX7U-256-BLU', '123456789012348', 'OPPO Find X7 Ultra với Hasselblad camera', 4, 'CPH2581', 22990000, 18500000, 24990000, 8, 20, 5, 'ACTIVE', 'SMARTPHONE', 'Ocean Blue', '256GB', 'Android 14', '6.82 inch', 'Snapdragon 8 Gen 3', '12GB', '5400mAh', '50MP Main + 50MP Ultra Wide + 50MP Telephoto + 50MP Periscope', '32MP', '50MP + 50MP + 50MP + 50MP', 'https://example.com/oppofindx7ultra.jpg', NOW(), NOW()),
('Vivo X100 Pro', 'VVX100P-256-BLK', '123456789012349', 'Vivo X100 Pro với ZEISS camera và chip Dimensity 9300', 5, 'V2309A', 18990000, 15500000, 20990000, 10, 30, 8, 'ACTIVE', 'SMARTPHONE', 'Asteroid Black', '256GB', 'Android 14', '6.78 inch', 'Dimensity 9300', '12GB', '5400mAh', '50MP Main + 50MP Ultra Wide + 50MP Telephoto', '32MP', '50MP + 50MP + 50MP', 'https://example.com/vivox100pro.jpg', NOW(), NOW());

-- 9. Customer Loyalty (Chương trình thân thiết)
INSERT INTO customer_loyalty (customer_id, total_points, used_points, available_points, total_spent, tier, last_purchase_date, tier_upgrade_date, created_at, updated_at) VALUES
(1, 1500, 300, 1200, 75000000, 'GOLD', '2024-12-10', '2024-11-01 10:00:00', NOW(), NOW()),
(2, 3000, 500, 2500, 150000000, 'PLATINUM', '2024-12-08', '2024-10-15 14:30:00', NOW(), NOW()),
(3, 800, 100, 700, 35000000, 'SILVER', '2024-12-05', '2024-09-20 16:45:00', NOW(), NOW()),
(4, 400, 0, 400, 20000000, 'BRONZE', '2024-11-28', NULL, NOW(), NOW()),
(5, 200, 50, 150, 8000000, 'BRONZE', '2024-10-15', NULL, NOW(), NOW());

-- 10. Orders (Đơn hàng)
INSERT INTO orders (order_number, customer_id, total_amount, discount_amount, final_amount, status, payment_method, payment_status, shipping_address, notes, sales_person_id, created_at, updated_at, delivered_at) VALUES
('ORD-20241210-001', 1, 29990000, 2999000, 26991000, 'DELIVERED', 'CREDIT_CARD', 'PAID', '123 Đường ABC, Phường 1, Quận 1, TP.HCM', 'Giao hàng trong giờ hành chính', 3, '2024-12-10 09:30:00', '2024-12-12 16:45:00', '2024-12-12 16:45:00'),
('ORD-20241208-002', 2, 55980000, 5598000, 50382000, 'DELIVERED', 'BANK_TRANSFER', 'PAID', '456 Đường XYZ, Phường 2, Quận 3, TP.HCM', 'Khách VIP, ưu tiên giao nhanh', 3, '2024-12-08 14:20:00', '2024-12-10 11:30:00', '2024-12-10 11:30:00'),
('ORD-20241205-003', 3, 19990000, 999500, 18990500, 'DELIVERED', 'E_WALLET', 'PAID', '789 Đường DEF, Phường 3, Quận Ba Đình, Hà Nội', 'Giao hàng cuối tuần', 3, '2024-12-05 16:15:00', '2024-12-07 09:20:00', '2024-12-07 09:20:00'),
('ORD-20241128-004', 4, 18990000, 0, 18990000, 'PROCESSING', 'CASH', 'PENDING', '321 Đường GHI, Phường 4, Quận Hải Châu, Đà Nẵng', 'Thanh toán khi nhận hàng', 4, '2024-11-28 10:45:00', '2024-11-28 10:45:00', NULL);

-- 11. Order Items (Chi tiết đơn hàng)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price, discount_amount, created_at) VALUES
(1, 1, 1, 29990000, 29990000, 2999000, '2024-12-10 09:30:00'),
(2, 1, 1, 29990000, 29990000, 2999000, '2024-12-08 14:20:00'),
(2, 2, 1, 27990000, 27990000, 2799000, '2024-12-08 14:20:00'),
(3, 3, 1, 19990000, 19990000, 999500, '2024-12-05 16:15:00'),
(4, 5, 1, 18990000, 18990000, 0, '2024-11-28 10:45:00');

-- 12. Payments (Thanh toán)
INSERT INTO payments (payment_number, order_id, customer_id, amount, method, status, type, transaction_id, reference_number, card_last_four_digits, card_type, bank_name, gateway_response, notes, processed_at, processed_by, created_at, updated_at) VALUES
('PAY-20241210-001', 1, 1, 26991000, 'CREDIT_CARD', 'COMPLETED', 'FULL_PAYMENT', 'TXN123456789', 'REF987654321', '1234', 'VISA', 'Vietcombank', 'SUCCESS', 'Thanh toán thành công', '2024-12-10 10:30:00', 3, '2024-12-10 10:30:00', '2024-12-10 10:30:00'),
('PAY-20241208-002', 2, 2, 50382000, 'BANK_TRANSFER', 'COMPLETED', 'FULL_PAYMENT', 'TXN123456790', 'REF987654322', NULL, NULL, 'Techcombank', 'SUCCESS', 'Chuyển khoản thành công', '2024-12-08 15:45:00', 3, '2024-12-08 15:45:00', '2024-12-08 15:45:00'),
('PAY-20241205-003', 3, 3, 18990500, 'E_WALLET', 'COMPLETED', 'FULL_PAYMENT', 'TXN123456791', 'REF987654323', NULL, NULL, 'MoMo', 'SUCCESS', 'Thanh toán ví điện tử', '2024-12-05 16:20:00', 4, '2024-12-05 16:20:00', '2024-12-05 16:20:00');

-- 13. Warranties (Bảo hành)
INSERT INTO warranties (warranty_number, product_id, customer_id, order_id, purchase_date, warranty_start_date, warranty_end_date, warranty_period_months, status, type, warranty_terms, serial_number, imei, issued_by, created_at, updated_at) VALUES
('WRT-20241210-001', 1, 1, 1, '2024-12-10', '2024-12-10', '2025-12-10', 12, 'ACTIVE', 'MANUFACTURER', 'Bảo hành 1 đổi 1 trong 12 tháng đầu, bảo hành sửa chữa miễn phí', 'SN123456789', '123456789012345', 3, '2024-12-10 10:30:00', '2024-12-10 10:30:00'),
('WRT-20241208-002', 1, 2, 2, '2024-12-08', '2024-12-08', '2025-12-08', 12, 'ACTIVE', 'MANUFACTURER', 'Bảo hành 1 đổi 1 trong 12 tháng đầu, bảo hành sửa chữa miễn phí', 'SN123456790', '123456789012345', 3, '2024-12-08 15:45:00', '2024-12-08 15:45:00'),
('WRT-20241208-003', 2, 2, 2, '2024-12-08', '2024-12-08', '2025-12-08', 12, 'ACTIVE', 'MANUFACTURER', 'Bảo hành chính hãng Samsung 12 tháng', 'SN123456791', '123456789012346', 3, '2024-12-08 15:45:00', '2024-12-08 15:45:00'),
('WRT-20241205-004', 3, 3, 3, '2024-12-05', '2024-12-05', '2025-12-05', 12, 'ACTIVE', 'MANUFACTURER', 'Bảo hành chính hãng Xiaomi 12 tháng', 'SN123456792', '123456789012347', 4, '2024-12-05 16:20:00', '2024-12-05 16:20:00');

-- 14. Customer Interactions (Tương tác khách hàng)
INSERT INTO customer_interactions (customer_id, user_id, type, subject, description, status, follow_up_date, created_at) VALUES
(1, 3, 'PHONE_CALL', 'Tư vấn sản phẩm iPhone 15', 'Khách hàng hỏi về thông số kỹ thuật, giá cả và chương trình khuyến mãi', 'RESOLVED', '2024-12-15 10:00:00', '2024-12-10 14:30:00'),
(2, 4, 'EMAIL', 'Hỗ trợ kỹ thuật Galaxy S24', 'Khách VIP cần hỗ trợ cài đặt tính năng S Pen', 'RESOLVED', NULL, '2024-12-09 09:15:00'),
(3, 3, 'IN_PERSON', 'Tư vấn mua sản phẩm mới', 'Khách hàng đến cửa hàng xem trực tiếp Xiaomi 14 Ultra', 'RESOLVED', '2024-12-20 14:00:00', '2024-12-05 16:45:00'),
(4, 4, 'SMS', 'Xác nhận đơn hàng', 'Gửi SMS xác nhận đơn hàng và thông tin giao hàng', 'RESOLVED', NULL, '2024-11-28 11:00:00'),
(5, 4, 'PHONE_CALL', 'Chăm sóc khách hàng', 'Gọi điện chăm sóc khách hàng lâu không mua hàng', 'IN_PROGRESS', '2024-12-25 09:00:00', '2024-12-01 10:30:00');

-- 15. Customer Care Tasks (Nhiệm vụ chăm sóc khách hàng)
INSERT INTO customer_care_tasks (customer_id, title, description, type, status, priority, due_date, assigned_to, created_by, notes, created_at, updated_at) VALUES
(1, 'Gọi điện chúc mừng sinh nhật', 'Chúc mừng sinh nhật khách hàng và giới thiệu sản phẩm mới', 'BIRTHDAY_GREETING', 'PENDING', 'HIGH', '2025-05-15 09:00:00', 4, 2, 'Khách hàng thân thiết, cần chăm sóc đặc biệt', NOW(), NOW()),
(2, 'Khảo sát hài lòng sau mua hàng', 'Gọi điện khảo sát độ hài lòng về sản phẩm và dịch vụ', 'SATISFACTION_SURVEY', 'COMPLETED', 'MEDIUM', '2024-12-15 14:00:00', 4, 2, 'Khách VIP đã mua 2 sản phẩm', '2024-12-13 10:00:00', '2024-12-15 15:30:00'),
(3, 'Theo dõi sử dụng sản phẩm', 'Gọi điện hỏi thăm về trải nghiệm sử dụng Xiaomi 14 Ultra', 'FOLLOW_UP_CALL', 'IN_PROGRESS', 'MEDIUM', '2024-12-20 10:00:00', 4, 2, 'Khách hàng mới, cần theo dõi', NOW(), NOW()),
(5, 'Liên hệ thu hồi khách hàng', 'Gọi điện chăm sóc khách hàng lâu không mua hàng, giới thiệu sản phẩm mới', 'WINBACK_CONTACT', 'PENDING', 'HIGH', '2024-12-25 09:00:00', 4, 2, 'Khách hàng từng mua hàng nhưng lâu không quay lại', NOW(), NOW());

-- 16. Customer Feedback (Phản hồi khách hàng)
INSERT INTO customer_feedback (customer_id, order_id, subject, content, type, rating, status, response, responded_at, responded_by, created_at, updated_at) VALUES
(1, 1, 'Hài lòng với sản phẩm và dịch vụ', 'iPhone 15 Pro Max chất lượng tuyệt vời, nhân viên tư vấn nhiệt tình. Giao hàng nhanh chóng.', 'COMPLIMENT', 5, 'RESPONDED', 'Cảm ơn anh đã đánh giá tích cực. Chúng tôi sẽ tiếp tục cải thiện dịch vụ.', '2024-12-11 09:00:00', 4, '2024-12-11 08:30:00', '2024-12-11 09:00:00'),
(2, 2, 'Rất hài lòng với dịch vụ VIP', 'Được chăm sóc rất tốt, sản phẩm chất lượng cao. Sẽ tiếp tục ủng hộ cửa hàng.', 'COMPLIMENT', 5, 'RESPONDED', 'Cảm ơn chị đã tin tường và ủng hộ. Chúng tôi luôn sẵn sàng phục vụ chị.', '2024-12-09 14:00:00', 2, '2024-12-09 13:45:00', '2024-12-09 14:00:00'),
(3, 3, 'Sản phẩm tốt nhưng giao hàng hơi chậm', 'Xiaomi 14 Ultra chạy mượt, camera đẹp. Tuy nhiên giao hàng chậm hơn dự kiến 1 ngày.', 'COMPLAINT', 4, 'RESPONDED', 'Cảm ơn anh đã phản hồi. Chúng tôi xin lỗi về việc giao hàng chậm và sẽ cải thiện.', '2024-12-06 10:30:00', 3, '2024-12-06 09:15:00', '2024-12-06 10:30:00');

-- 17. Marketing Campaigns (Chiến dịch marketing)
INSERT INTO marketing_campaigns (name, description, start_date, end_date, status, budget, target_audience, created_by, created_at, updated_at) VALUES
('Khuyến mãi cuối năm 2024', 'Giảm giá 10-15% tất cả sản phẩm iPhone và Samsung, tặng phụ kiện', '2024-12-01', '2024-12-31', 'ACTIVE', 100000000, 'ALL_CUSTOMERS', 2, NOW(), NOW()),
('Chương trình tri ân khách hàng VIP', 'Ưu đãi đặc biệt cho khách hàng VIP: giảm 20%, tặng bảo hiểm', '2024-11-15', '2025-01-15', 'ACTIVE', 50000000, 'VIP_CUSTOMERS', 2, NOW(), NOW()),
('Khuyến mãi Black Friday', 'Giảm giá sốc các sản phẩm Xiaomi và Oppo', '2024-11-24', '2024-11-30', 'COMPLETED', 80000000, 'ALL_CUSTOMERS', 2, NOW(), NOW());

-- 18. Customer Notifications (Thông báo khách hàng)
INSERT INTO customer_notifications (customer_id, title, content, type, channel, status, scheduled_at, sent_at, campaign_id, created_at) VALUES
(1, 'Khuyến mãi đặc biệt cuối năm', 'Giảm ngay 10% cho iPhone 16 series. Ưu đãi có hạn đến 31/12/2024.', 'PROMOTION', 'EMAIL', 'SENT', '2024-12-12 09:00:00', '2024-12-12 09:05:00', 1, NOW()),
(2, 'Ưu đãi đặc biệt cho khách VIP', 'Chị được giảm 20% và tặng kèm bảo hiểm cho đơn hàng tiếp theo.', 'PROMOTION', 'SMS', 'SENT', '2024-12-11 14:00:00', '2024-12-11 14:02:00', 2, NOW()),
(1, 'Nhắc nhở bảo hành sắp hết hạn', 'iPhone 15 Pro Max của anh sẽ hết hạn bảo hành vào 10/12/2025. Liên hệ nếu cần hỗ trợ.', 'WARRANTY_REMINDER', 'EMAIL', 'SCHEDULED', '2025-11-10 09:00:00', NULL, NULL, NOW()),
(3, 'Chúc mừng sinh nhật', 'Chúc mừng sinh nhật! Tặng voucher giảm 15% cho lần mua hàng tiếp theo.', 'BIRTHDAY', 'SMS', 'SENT', '2024-12-10 08:00:00', '2024-12-10 08:01:00', NULL, NOW());

-- Update product stock after orders
UPDATE products SET stock_quantity = stock_quantity - 1 WHERE id = 1; -- iPhone 15 Pro Max sold 2 units
UPDATE products SET stock_quantity = stock_quantity - 1 WHERE id = 1;
UPDATE products SET stock_quantity = stock_quantity - 1 WHERE id = 2; -- Galaxy S24 Ultra sold 1 unit
UPDATE products SET stock_quantity = stock_quantity - 1 WHERE id = 3; -- Xiaomi 14 Ultra sold 1 unit
UPDATE products SET stock_quantity = stock_quantity - 1 WHERE id = 5; -- Vivo X100 Pro sold 1 unit

-- Update brand product counts
UPDATE brands SET product_count = (SELECT COUNT(*) FROM products WHERE brand_id = brands.id);
