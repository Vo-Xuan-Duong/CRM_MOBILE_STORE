-- ==============================================
-- CRM MOBILE STORE DATABASE SCHEMA
-- Enhanced version with proper indexes, constraints and consistency
-- ==============================================

-- Drop existing tables in reverse dependency order if they exist
DROP TABLE IF EXISTS campaign_target CASCADE;
DROP TABLE IF EXISTS campaign CASCADE;
DROP TABLE IF EXISTS interaction CASCADE;
DROP TABLE IF EXISTS repair_ticket CASCADE;
DROP TABLE IF EXISTS warranty CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS sales_order_item CASCADE;
DROP TABLE IF EXISTS sales_order CASCADE;
DROP TABLE IF EXISTS stock_movement CASCADE;
DROP TABLE IF EXISTS serial_unit CASCADE;
DROP TABLE IF EXISTS stock_item CASCADE;
DROP TABLE IF EXISTS spec_value CASCADE;
DROP TABLE IF EXISTS spec_field CASCADE;
DROP TABLE IF EXISTS spec_group CASCADE;
DROP TABLE IF EXISTS product_media CASCADE;
DROP TABLE IF EXISTS sku CASCADE;
DROP TABLE IF EXISTS product_model CASCADE;
DROP TABLE IF EXISTS brand CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==============================================
-- USERS + AUTH SYSTEM
-- ==============================================

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    full_name     TEXT NOT NULL,
    email         TEXT UNIQUE,
    phone         TEXT,
    username      TEXT UNIQUE,
    password_hash TEXT,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT users_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT users_phone_format CHECK (phone ~ '^\+?[0-9\s\-\(\)]{8,20}$')
);

CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    code        TEXT UNIQUE NOT NULL,
    name        TEXT NOT NULL,
    description TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE permissions (
    id          BIGSERIAL PRIMARY KEY,
    code        TEXT UNIQUE NOT NULL,
    name        TEXT NOT NULL,
    module      TEXT NOT NULL DEFAULT 'general',
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id    BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_by BIGINT REFERENCES users(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    granted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_tokens (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash         TEXT NOT NULL UNIQUE,
    issued_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at         TIMESTAMPTZ NOT NULL,
    revoked            BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at         TIMESTAMPTZ,
    replaced_by_token  BIGINT REFERENCES refresh_tokens(id),
    user_agent         TEXT,
    ip_address         INET,

    CONSTRAINT refresh_tokens_valid_dates CHECK (expires_at > issued_at),
    CONSTRAINT refresh_tokens_revoked_logic CHECK (
        (revoked = FALSE AND revoked_at IS NULL) OR
        (revoked = TRUE AND revoked_at IS NOT NULL)
    )
);

-- ==============================================
-- CUSTOMERS
-- ==============================================

CREATE TABLE customer (
    id          BIGSERIAL PRIMARY KEY,
    full_name   TEXT NOT NULL,
    phone       TEXT NOT NULL UNIQUE,
    email       TEXT,
    dob         DATE,
    address     TEXT,
    tier        TEXT NOT NULL DEFAULT 'regular' CHECK (tier IN ('regular','vip','potential')),
    notes       TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT customer_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT customer_phone_format CHECK (phone ~ '^\+?[0-9\s\-\(\)]{8,20}$'),
    CONSTRAINT customer_valid_dob CHECK (dob IS NULL OR dob <= CURRENT_DATE)
);

-- ==============================================
-- PRODUCT CATALOG
-- ==============================================

CREATE TABLE brand (
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT NOT NULL UNIQUE,
    logo_url   TEXT,
    website    TEXT,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE product_model (
    id                      BIGSERIAL PRIMARY KEY,
    brand_id                BIGINT NOT NULL REFERENCES brand(id) ON DELETE RESTRICT,
    name                    TEXT NOT NULL,
    category                TEXT NOT NULL DEFAULT 'phone' CHECK (category IN ('phone','accessory','service')),
    default_warranty_months SMALLINT NOT NULL DEFAULT 12 CHECK (default_warranty_months >= 0),
    description             TEXT,
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (brand_id, name)
);

CREATE TABLE sku (
    id            BIGSERIAL PRIMARY KEY,
    model_id      BIGINT NOT NULL REFERENCES product_model(id) ON DELETE CASCADE,
    variant_name  TEXT,
    color         TEXT,
    storage_gb    SMALLINT,
    barcode       TEXT UNIQUE,
    price         NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    cost_price    NUMERIC(12,2) CHECK (cost_price >= 0),
    is_serialized BOOLEAN NOT NULL DEFAULT TRUE,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (model_id, variant_name, color, storage_gb)
);

-- ==============================================
-- MEDIA & SPECIFICATIONS
-- ==============================================

CREATE TABLE product_media (
    id         BIGSERIAL PRIMARY KEY,
    model_id   BIGINT REFERENCES product_model(id) ON DELETE CASCADE,
    sku_id     BIGINT REFERENCES sku(id) ON DELETE CASCADE,
    media_type TEXT NOT NULL CHECK (media_type IN ('image','video','pdf','document')),
    url        TEXT NOT NULL,
    caption    TEXT,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_media_target CHECK (
        (model_id IS NOT NULL AND sku_id IS NULL) OR
        (model_id IS NULL AND sku_id IS NOT NULL)
    )
);

CREATE TABLE spec_group (
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT NOT NULL UNIQUE,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    is_active  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE spec_field (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL REFERENCES spec_group(id) ON DELETE CASCADE,
    field_key   TEXT NOT NULL,
    label       TEXT NOT NULL,
    data_type   TEXT NOT NULL CHECK (data_type IN ('text','number','boolean','json')),
    unit        TEXT,
    applies_to  TEXT NOT NULL DEFAULT 'model' CHECK (applies_to IN ('model','sku')),
    sort_order  SMALLINT NOT NULL DEFAULT 0,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (group_id, field_key)
);

CREATE TABLE spec_value (
    id            BIGSERIAL PRIMARY KEY,
    field_id      BIGINT NOT NULL REFERENCES spec_field(id) ON DELETE CASCADE,
    model_id      BIGINT REFERENCES product_model(id) ON DELETE CASCADE,
    sku_id        BIGINT REFERENCES sku(id) ON DELETE CASCADE,
    value_text    TEXT,
    value_number  NUMERIC(18,4),
    value_bool    BOOLEAN,
    value_json    JSONB,
    unit_override TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_spec_target CHECK (
        (model_id IS NOT NULL AND sku_id IS NULL) OR
        (model_id IS NULL AND sku_id IS NOT NULL)
    )
);

-- ==============================================
-- INVENTORY MANAGEMENT
-- ==============================================

CREATE TABLE stock_item (
    id            BIGSERIAL PRIMARY KEY,
    sku_id        BIGINT NOT NULL REFERENCES sku(id) ON DELETE CASCADE,
    quantity      INTEGER NOT NULL CHECK (quantity >= 0),
    reserved_qty  INTEGER NOT NULL DEFAULT 0 CHECK (reserved_qty >= 0),
    min_stock     INTEGER NOT NULL DEFAULT 0 CHECK (min_stock >= 0),
    max_stock     INTEGER CHECK (max_stock IS NULL OR max_stock >= min_stock),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (sku_id),
    CONSTRAINT stock_item_reserved_check CHECK (reserved_qty <= quantity)
);

CREATE TABLE serial_unit (
    id            BIGSERIAL PRIMARY KEY,
    sku_id        BIGINT NOT NULL REFERENCES sku(id) ON DELETE CASCADE,
    imei          TEXT NOT NULL UNIQUE,
    serial_number TEXT,
    status        TEXT NOT NULL DEFAULT 'in_stock' CHECK (status IN ('in_stock','sold','repair','lost','returned')),
    purchase_date DATE,
    notes         TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE stock_movement (
    id             BIGSERIAL PRIMARY KEY,
    sku_id         BIGINT NOT NULL REFERENCES sku(id) ON DELETE CASCADE,
    serial_unit_id BIGINT REFERENCES serial_unit(id) ON DELETE SET NULL,
    movement_type  TEXT NOT NULL CHECK (movement_type IN ('in','out')),
    quantity       INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    reason         TEXT NOT NULL CHECK (reason IN ('purchase','sale','return','repair','adjustment','transfer','damaged')),
    ref_type       TEXT,
    ref_id         BIGINT,
    notes          TEXT,
    created_by     BIGINT REFERENCES users(id),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==============================================
-- SALES SYSTEM
-- ==============================================

CREATE TABLE sales_order (
    id             BIGSERIAL PRIMARY KEY,
    customer_id    BIGINT NOT NULL REFERENCES customer(id) ON DELETE RESTRICT,
    user_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status         TEXT NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','pending','confirmed','paid','cancelled','refunded')),
    payment_method TEXT CHECK (payment_method IN ('cash','card','transfer','other')),
    subtotal       NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
    discount       NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (discount >= 0),
    total          NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    notes          TEXT,
    order_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE sales_order_item (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGINT NOT NULL REFERENCES sales_order(id) ON DELETE CASCADE,
    sku_id         BIGINT NOT NULL REFERENCES sku(id) ON DELETE RESTRICT,
    serial_unit_id BIGINT REFERENCES serial_unit(id) ON DELETE SET NULL,
    quantity       INTEGER NOT NULL CHECK (quantity > 0),
    unit_price     NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
    discount       NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (discount >= 0),
    line_total     NUMERIC(12,2) NOT NULL CHECK (line_total >= 0),
    warranty_months SMALLINT NOT NULL DEFAULT 12
);

CREATE TABLE payment (
    id             BIGSERIAL PRIMARY KEY,
    order_id       BIGINT NOT NULL REFERENCES sales_order(id) ON DELETE CASCADE,
    payment_number TEXT NOT NULL UNIQUE,
    amount         NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    method         TEXT NOT NULL CHECK (method IN ('cash','card','transfer','other')),
    reference_no   TEXT,
    status         TEXT NOT NULL DEFAULT 'completed' CHECK (status IN ('pending','completed','failed','cancelled')),
    paid_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    notes          TEXT,
    created_by     BIGINT REFERENCES users(id)
);

-- ==============================================
-- WARRANTY & REPAIR
-- ==============================================

CREATE TABLE warranty (
    id             BIGSERIAL PRIMARY KEY,
    customer_id    BIGINT NOT NULL REFERENCES customer(id) ON DELETE RESTRICT,
    order_item_id  BIGINT NOT NULL UNIQUE REFERENCES sales_order_item(id) ON DELETE CASCADE,
    serial_unit_id BIGINT REFERENCES serial_unit(id) ON DELETE SET NULL,
    warranty_code  TEXT UNIQUE NOT NULL,
    start_date     DATE NOT NULL,
    end_date       DATE NOT NULL,
    months         SMALLINT NOT NULL DEFAULT 12 CHECK (months > 0),
    status         TEXT NOT NULL DEFAULT 'active' CHECK (status IN ('active','expired','void','claimed')),
    qr_image_url   TEXT,
    notes          TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT warranty_valid_dates CHECK (end_date > start_date)
);

CREATE TABLE repair_ticket (
    id             BIGSERIAL PRIMARY KEY,
    ticket_number  TEXT NOT NULL UNIQUE,
    serial_unit_id BIGINT REFERENCES serial_unit(id) ON DELETE SET NULL,
    customer_id    BIGINT NOT NULL REFERENCES customer(id) ON DELETE RESTRICT,
    warranty_id    BIGINT REFERENCES warranty(id) ON DELETE SET NULL,
    received_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    issue_desc     TEXT NOT NULL,
    diagnosis      TEXT,
    status         TEXT NOT NULL DEFAULT 'received' CHECK (status IN ('received','diagnosing','waiting_parts','repairing','testing','done','delivered','cancelled')),
    estimate_cost  NUMERIC(12,2) CHECK (estimate_cost >= 0),
    actual_cost    NUMERIC(12,2) CHECK (actual_cost >= 0),
    under_warranty BOOLEAN NOT NULL DEFAULT FALSE,
    technician_id  BIGINT REFERENCES users(id),
    closed_at      TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ==============================================
-- CUSTOMER CARE & MARKETING
-- ==============================================

CREATE TABLE interaction (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    user_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    type        TEXT NOT NULL CHECK (type IN ('call','sms','email','chat','visit','note','complaint','feedback')),
    direction   TEXT CHECK (direction IN ('inbound','outbound')),
    channel     TEXT,
    subject     TEXT,
    content     TEXT,
    outcome     TEXT,
    follow_up_date DATE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE campaign (
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    type        TEXT NOT NULL CHECK (type IN ('email','sms','call','promotion','event')),
    description TEXT,
    start_date  DATE,
    end_date    DATE,
    budget      NUMERIC(12,2) CHECK (budget >= 0),
    status      TEXT NOT NULL DEFAULT 'draft' CHECK (status IN ('draft','active','paused','completed','cancelled')),
    created_by  BIGINT REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT campaign_valid_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE campaign_target (
    campaign_id BIGINT NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    sent_at     TIMESTAMPTZ,
    status      TEXT CHECK (status IN ('pending','sent','delivered','opened','clicked','failed','unsubscribed')),
    response    TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (campaign_id, customer_id)
);

-- ==============================================
-- INDEXES FOR PERFORMANCE
-- ==============================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- Customer indexes
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_tier ON customer(tier);
CREATE INDEX idx_customer_active ON customer(is_active);

-- Product indexes
CREATE INDEX idx_sku_model_id ON sku(model_id);
CREATE INDEX idx_sku_barcode ON sku(barcode);
CREATE INDEX idx_sku_active ON sku(is_active);
CREATE INDEX idx_product_model_brand_id ON product_model(brand_id);
CREATE INDEX idx_product_model_category ON product_model(category);

-- Inventory indexes
CREATE INDEX idx_serial_unit_sku_id ON serial_unit(sku_id);
CREATE INDEX idx_serial_unit_imei ON serial_unit(imei);
CREATE INDEX idx_serial_unit_status ON serial_unit(status);
CREATE INDEX idx_stock_movement_sku_id ON stock_movement(sku_id);
CREATE INDEX idx_stock_movement_created_at ON stock_movement(created_at);

-- Sales indexes
CREATE INDEX idx_sales_order_customer_id ON sales_order(customer_id);
CREATE INDEX idx_sales_order_user_id ON sales_order(user_id);
CREATE INDEX idx_sales_order_status ON sales_order(status);
CREATE INDEX idx_sales_order_date ON sales_order(order_date);
CREATE INDEX idx_sales_order_item_order_id ON sales_order_item(order_id);
CREATE INDEX idx_payment_order_id ON payment(order_id);

-- Warranty & Repair indexes
CREATE INDEX idx_warranty_customer_id ON warranty(customer_id);
CREATE INDEX idx_warranty_code ON warranty(warranty_code);
CREATE INDEX idx_warranty_status ON warranty(status);
CREATE INDEX idx_repair_ticket_customer_id ON repair_ticket(customer_id);
CREATE INDEX idx_repair_ticket_status ON repair_ticket(status);

-- CRM indexes
CREATE INDEX idx_interaction_customer_id ON interaction(customer_id);
CREATE INDEX idx_interaction_created_at ON interaction(created_at);
CREATE INDEX idx_campaign_status ON campaign(status);
CREATE INDEX idx_campaign_dates ON campaign(start_date, end_date);

-- ==============================================
-- FUNCTIONS AND TRIGGERS
-- ==============================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply update triggers to relevant tables
CREATE TRIGGER trigger_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_customer_updated_at BEFORE UPDATE ON customer FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_brand_updated_at BEFORE UPDATE ON brand FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_product_model_updated_at BEFORE UPDATE ON product_model FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_sku_updated_at BEFORE UPDATE ON sku FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_serial_unit_updated_at BEFORE UPDATE ON serial_unit FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_sales_order_updated_at BEFORE UPDATE ON sales_order FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_warranty_updated_at BEFORE UPDATE ON warranty FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_repair_ticket_updated_at BEFORE UPDATE ON repair_ticket FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_campaign_updated_at BEFORE UPDATE ON campaign FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to generate order numbers
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TEXT AS $$
BEGIN
    RETURN 'SO' || TO_CHAR(NOW(), 'YYYYMMDD') || LPAD(NEXTVAL('sales_order_id_seq')::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;

-- Function to generate warranty codes
CREATE OR REPLACE FUNCTION generate_warranty_code()
RETURNS TEXT AS $$
BEGIN
    RETURN 'WR' || TO_CHAR(NOW(), 'YYYYMMDD') || LPAD(NEXTVAL('warranty_id_seq')::TEXT, 6, '0');
END;
$$ LANGUAGE plpgsql;

-- ==============================================
-- INITIAL DATA SETUP
-- ==============================================

-- Default roles
INSERT INTO roles (code, name, description) VALUES
('ADMIN', 'Administrator', 'Full system access'),
('MANAGER', 'Manager', 'Management level access'),
('SALES', 'Sales Staff', 'Sales operations access'),
('TECHNICIAN', 'Technician', 'Repair and technical operations'),
('CASHIER', 'Cashier', 'Point of sale operations');

-- Default permissions
INSERT INTO permissions (code, name, module) VALUES
('USER_CREATE', 'Create Users', 'user_management'),
('USER_READ', 'View Users', 'user_management'),
('USER_UPDATE', 'Update Users', 'user_management'),
('USER_DELETE', 'Delete Users', 'user_management'),
('CUSTOMER_CREATE', 'Create Customers', 'customer_management'),
('CUSTOMER_READ', 'View Customers', 'customer_management'),
('CUSTOMER_UPDATE', 'Update Customers', 'customer_management'),
('CUSTOMER_DELETE', 'Delete Customers', 'customer_management'),
('PRODUCT_CREATE', 'Create Products', 'product_management'),
('PRODUCT_READ', 'View Products', 'product_management'),
('PRODUCT_UPDATE', 'Update Products', 'product_management'),
('PRODUCT_DELETE', 'Delete Products', 'product_management'),
('INVENTORY_READ', 'View Inventory', 'inventory_management'),
('INVENTORY_UPDATE', 'Update Inventory', 'inventory_management'),
('SALES_CREATE', 'Create Sales', 'sales_management'),
('SALES_READ', 'View Sales', 'sales_management'),
('SALES_UPDATE', 'Update Sales', 'sales_management'),
('PAYMENT_CREATE', 'Process Payments', 'payment_management'),
('PAYMENT_READ', 'View Payments', 'payment_management'),
('WARRANTY_CREATE', 'Create Warranties', 'warranty_management'),
('WARRANTY_READ', 'View Warranties', 'warranty_management'),
('REPAIR_CREATE', 'Create Repair Tickets', 'repair_management'),
('REPAIR_READ', 'View Repair Tickets', 'repair_management'),
('REPAIR_UPDATE', 'Update Repair Tickets', 'repair_management'),
('REPORT_VIEW', 'View Reports', 'reporting'),
('CAMPAIGN_CREATE', 'Create Campaigns', 'marketing'),
('CAMPAIGN_READ', 'View Campaigns', 'marketing'),
('CAMPAIGN_UPDATE', 'Update Campaigns', 'marketing');

-- Default spec groups
INSERT INTO spec_group (name, sort_order) VALUES
('Display', 1),
('Performance', 2),
('Camera', 3),
('Battery', 4),
('Connectivity', 5),
('Physical', 6),
('Software', 7);

COMMENT ON DATABASE IS 'CRM Mobile Store Database - Enhanced Schema with comprehensive features for mobile retail management';
