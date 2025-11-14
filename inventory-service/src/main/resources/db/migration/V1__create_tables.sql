CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    city VARCHAR(255),
    country VARCHAR(255),
    type VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255),
    unit VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL DEFAULT 0,
    reserved_quantity DECIMAL(19, 2) DEFAULT 0,
    available_quantity DECIMAL(19, 2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE,
    CONSTRAINT uk_stock_product_location UNIQUE (product_id, location_id)
);

CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    from_location_id BIGINT,
    to_location_id BIGINT,
    movement_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    reference_number VARCHAR(255),
    reason TEXT,
    performed_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_movement_from_location FOREIGN KEY (from_location_id) REFERENCES locations(id) ON DELETE SET NULL,
    CONSTRAINT fk_movement_to_location FOREIGN KEY (to_location_id) REFERENCES locations(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS inventory_adjustments (
    id BIGSERIAL PRIMARY KEY,
    adjustment_number VARCHAR(255) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    quantity_change DECIMAL(19, 2) NOT NULL,
    previous_quantity DECIMAL(19, 2),
    new_quantity DECIMAL(19, 2),
    reason TEXT,
    adjusted_by VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_adjustment_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_adjustment_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

CREATE INDEX idx_stock_product_id ON stock(product_id);
CREATE INDEX idx_stock_location_id ON stock(location_id);
CREATE INDEX idx_movement_product_id ON stock_movements(product_id);
CREATE INDEX idx_movement_from_location ON stock_movements(from_location_id);
CREATE INDEX idx_movement_to_location ON stock_movements(to_location_id);
CREATE INDEX idx_adjustment_product_id ON inventory_adjustments(product_id);
CREATE INDEX idx_adjustment_location_id ON inventory_adjustments(location_id);

