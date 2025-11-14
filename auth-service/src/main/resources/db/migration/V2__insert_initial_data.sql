-- Insert permissions
INSERT INTO permissions (name, description) VALUES
    ('READ_USER', 'Read user information'),
    ('WRITE_USER', 'Create and update users'),
    ('DELETE_USER', 'Delete users'),
    ('READ_ROLE', 'Read role information'),
    ('WRITE_ROLE', 'Create and update roles'),
    ('READ_PERMISSION', 'Read permission information'),
    ('READ_ENQUIRY', 'Read procurement enquiries'),
    ('WRITE_ENQUIRY', 'Create and update enquiries'),
    ('READ_QUOTATION', 'Read quotations'),
    ('WRITE_QUOTATION', 'Create and update quotations'),
    ('READ_PO', 'Read purchase orders'),
    ('WRITE_PO', 'Create and update purchase orders'),
    ('APPROVE_PO', 'Approve purchase orders'),
    ('READ_INVENTORY', 'Read inventory information'),
    ('WRITE_INVENTORY', 'Update inventory')
ON CONFLICT (name) DO NOTHING;

-- Insert roles
INSERT INTO roles (name, description) VALUES
    ('USER', 'Basic user role'),
    ('PROCUREMENT_OFFICER', 'Procurement officer role'),
    ('MANAGER', 'Manager role with approval rights'),
    ('ADMIN', 'Administrator role')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to roles
-- USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER'
  AND p.name IN ('READ_ENQUIRY', 'READ_QUOTATION', 'READ_PO', 'READ_INVENTORY')
ON CONFLICT DO NOTHING;

-- PROCUREMENT_OFFICER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'PROCUREMENT_OFFICER'
  AND p.name IN ('READ_ENQUIRY', 'WRITE_ENQUIRY', 'READ_QUOTATION', 'WRITE_QUOTATION', 
                 'READ_PO', 'WRITE_PO', 'READ_INVENTORY', 'WRITE_INVENTORY')
ON CONFLICT DO NOTHING;

-- MANAGER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER'
  AND p.name IN ('READ_ENQUIRY', 'WRITE_ENQUIRY', 'READ_QUOTATION', 'WRITE_QUOTATION',
                 'READ_PO', 'WRITE_PO', 'APPROVE_PO', 'READ_INVENTORY', 'WRITE_INVENTORY')
ON CONFLICT DO NOTHING;

-- ADMIN role (all permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

