CREATE TABLE IF NOT EXISTS purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    po_number VARCHAR(255) UNIQUE NOT NULL,
    quotation_id BIGINT NOT NULL,
    supplier_id BIGINT,
    supplier_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    total_amount DECIMAL(19, 2) DEFAULT 0,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchase_order_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    description TEXT,
    quantity INTEGER,
    unit_price DECIMAL(19, 2),
    total_price DECIMAL(19, 2),
    unit VARCHAR(50),
    CONSTRAINT fk_po_item_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_po_quotation_id ON purchase_orders(quotation_id);
CREATE INDEX idx_po_status ON purchase_orders(status);
CREATE INDEX idx_po_item_po_id ON purchase_order_items(purchase_order_id);

