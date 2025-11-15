-- Insert default admin user
-- Username: admin
-- Password: password (BCrypt hash)
-- Email: buryar313@gmail.com
-- Role: ADMIN

-- BCrypt hash for "password" (using default strength 10)
-- This hash is: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (username, email, password, first_name, last_name, active, created_at, updated_at)
VALUES (
    'admin',
    'buryar313@gmail.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin',
    'User',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin'
  AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

