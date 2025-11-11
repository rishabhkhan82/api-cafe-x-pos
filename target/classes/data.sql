-- Insert default system settings
INSERT INTO system_settings (id, name, description, category, type, value, default_value, options, validation, requires_restart, is_public, created_at, updated_at) VALUES
-- General Settings
('platform_name', 'Platform Name', 'The name displayed across the platform', 'general', 'text', '"CafeX POS"', '"CafeX POS"', NULL, '{"required": true}', false, true, NOW(), NOW()),
('platform_url', 'Platform URL', 'Base URL for the platform', 'general', 'text', '"https://cafex.com"', '"https://cafex.com"', NULL, '{"required": true, "pattern": "^https?://.+"}', false, true, NOW(), NOW()),
('currency', 'Default Currency', 'Default currency for transactions', 'general', 'select', '"INR"', '"INR"', '["INR", "USD", "EUR", "GBP"]', NULL, false, true, NOW(), NOW()),
('platform_logo', 'Platform Logo URL', 'URL for the platform logo', 'general', 'text', '""', '""', NULL, '{"pattern": "^https?://.+"}', false, true, NOW(), NOW()),
('default_language', 'Default Language', 'Default language for the platform', 'general', 'select', '"en-IN"', '"en-IN"', '["en-IN", "hi-IN", "mr-IN"]', NULL, false, true, NOW(), NOW()),
('maintenance_message', 'Maintenance Message', 'Message displayed during maintenance', 'general', 'textarea', '""', '""', NULL, NULL, false, true, NOW(), NOW()),
('file_upload_max_size', 'Max File Upload Size (MB)', 'Maximum file size for uploads', 'general', 'number', '10', '10', NULL, '{"min": 1, "max": 100}', false, true, NOW(), NOW()),
('backup_enabled', 'Enable Backups', 'Automatically backup system data', 'general', 'boolean', 'true', 'true', NULL, NULL, false, true, NOW(), NOW()),
('backup_frequency', 'Backup Frequency', 'How often to perform backups', 'general', 'select', '"daily"', '"daily"', '["daily", "weekly", "monthly"]', NULL, false, true, NOW(), NOW()),
('support_email', 'Support Email', 'Email address for support', 'general', 'text', '""', '""', NULL, '{"pattern": "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"}', false, true, NOW(), NOW()),
('support_phone', 'Support Phone', 'Phone number for support', 'general', 'text', '""', '""', NULL, '{"pattern": "^[+\\d\\s\\-\\(\\)]+$"}', false, true, NOW(), NOW()),
('terms_url', 'Terms of Service URL', 'URL for terms of service', 'general', 'text', '""', '""', NULL, '{"pattern": "^https?://.+"}', false, true, NOW(), NOW()),
('privacy_url', 'Privacy Policy URL', 'URL for privacy policy', 'general', 'text', '""', '""', NULL, '{"pattern": "^https?://.+"}', false, true, NOW(), NOW()),

-- Performance Settings
('max_concurrent_users', 'Max Concurrent Users', 'Maximum number of users that can be active simultaneously', 'performance', 'number', '1000', '1000', NULL, '{"required": true, "min": 10, "max": 10000}', true, true, NOW(), NOW()),
('cache_enabled', 'Enable Caching', 'Enable system-wide caching for better performance', 'performance', 'boolean', 'true', 'true', NULL, NULL, true, true, NOW(), NOW()),
('cache_ttl', 'Cache TTL (seconds)', 'Time-to-live for cached data', 'performance', 'number', '3600', '3600', NULL, '{"required": true, "min": 60, "max": 86400}', false, true, NOW(), NOW()),

-- Security Settings
('session_timeout', 'Session Timeout (minutes)', 'Automatic logout after inactivity', 'security', 'number', '30', '30', NULL, '{"required": true, "min": 5, "max": 480}', false, true, NOW(), NOW()),
('password_min_length', 'Minimum Password Length', 'Minimum characters required for passwords', 'security', 'number', '8', '8', NULL, '{"required": true, "min": 6, "max": 128}', false, true, NOW(), NOW()),
('two_factor_required', 'Require 2FA', 'Require two-factor authentication for all users', 'security', 'boolean', 'false', 'false', NULL, NULL, false, true, NOW(), NOW()),

-- Notification Settings
('email_notifications', 'Email Notifications', 'Enable email notifications system-wide', 'notifications', 'boolean', 'true', 'true', NULL, NULL, false, true, NOW(), NOW()),
('sms_notifications', 'SMS Notifications', 'Enable SMS notifications for critical alerts', 'notifications', 'boolean', 'false', 'false', NULL, NULL, false, true, NOW(), NOW()),
('notification_batch_size', 'Notification Batch Size', 'Maximum notifications sent per batch', 'notifications', 'number', '100', '100', NULL, '{"required": true, "min": 10, "max": 1000}', false, true, NOW(), NOW()),

-- Integration Settings
('api_rate_limit', 'API Rate Limit', 'Maximum API requests per minute', 'integrations', 'number', '1000', '1000', NULL, '{"required": true, "min": 10, "max": 10000}', false, true, NOW(), NOW()),
('webhook_retries', 'Webhook Retry Attempts', 'Number of retry attempts for failed webhooks', 'integrations', 'number', '3', '3', NULL, '{"required": true, "min": 0, "max": 10}', false, true, NOW(), NOW()),
('maintenance_mode', 'Maintenance Mode', 'Put the platform in maintenance mode', 'general', 'boolean', 'false', 'false', NULL, NULL, true, true, NOW(), NOW());