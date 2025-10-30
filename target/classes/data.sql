-- ============================================
-- Cafe-X POS Database Initialization
-- ============================================
-- Initial data for development and testing
-- ============================================

-- Insert default users
INSERT INTO users (username, password, email, name, phone, role, restaurant_id, is_active, created_at, updated_at) VALUES
('rishabh@cafex.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'rishabh@cafex.com', 'Rishabh Khandekar', '+91 98765 43210', 'RESTAURANT_OWNER', 'restaurant-1', true, NOW(), NOW()),
('priya@cafex.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'priya@cafex.com', 'Priya Sharma', '+91 98765 43211', 'RESTAURANT_MANAGER', 'restaurant-1', true, NOW(), NOW()),
('amit@cafex.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'amit@cafex.com', 'Amit Kumar', '+91 98765 43212', 'CASHIER', 'restaurant-1', true, NOW(), NOW()),
('sneha@cafex.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'sneha@cafex.com', 'Sneha Patel', '+91 98765 43213', 'KITCHEN_MANAGER', 'restaurant-1', true, NOW(), NOW()),
('rahul@cafex.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'rahul@cafex.com', 'Rahul Singh', '+91 98765 43214', 'WAITER', 'restaurant-1', true, NOW(), NOW()),
('amit.patil@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'amit.patil@email.com', 'Amit Patil', '+91 98765 43215', 'CUSTOMER', NULL, true, NOW(), NOW()),
('sarah.j@email.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8lWZbGJCe', 'sarah.j@email.com', 'Sarah Johnson', '+91 98765 43216', 'CUSTOMER', NULL, true, NOW(), NOW());

-- Note: Default password for all users is 'password'
-- In production, use proper password hashing and unique passwords