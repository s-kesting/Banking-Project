INSERT INTO "users" (password, phoneNumber, username, role, bsn, email, verify_user) VALUES
-- Password: admin123
('$2a$12$LQv3c1yqBwLVHpEuBpwjOufrWmYs6djLUtB8H6e8/GvQfG2bFqW1u', '06-12345678', 'admin_user', 'CUSTOMER', NULL, 'admin@example.com', 'ACTIVE'),

-- Password: password123
('$2a$12$8Pf2fKHE5QztGSJ8FrCMpOxhgxF8RJMWk6LqHqN1Rq1YgPzL9CJTG', '06-23456789', 'john_doe', 'CUSTOMER', '123456782', 'john.doe@example.com', 'ACTIVE'),

-- Password: jane2024
('$2a$12$9Kg3dHF6QxuGRK9ErBNnqOyigzG9SMNXl7MrIrO2Sr2ZhQaM0DLUS', '06-34567890', 'jane_smith', 'CUSTOMER', '987654323', 'jane.smith@example.com', 'PENDING'),

-- Password: tech123
('$2a$12$7Hd2cGE5PwsFQH8DrAMmrNxfhyF7RKLWk5LpGpM1Qp1XfPzK8BJSF', '06-45678901', 'technician1', 'EMPLOYEE', NULL, 'tech1@example.com', 'ACTIVE'),

-- Password: service123
('$2a$12$6Gc1bFD4OvrERG7CqZLlsMyegxE6QJKVj4KoFoL0Po0WeOyJ7AIRF', '06-56789012', 'customer_service', 'CUSTOMER', NULL, 'cs@example.com', 'ACTIVE'),

-- Password: sarah456
('$2a$12$5Fb0aED3NuqDQF6BpYKkrLxdhwD5PKJUi3JnEnK9On9VdNxI6ZHQE', '06-67890123', 'sarah_jones', 'CUSTOMER', '567890124', 'sarah.jones@example.com', 'ACTIVE'),

-- Password: tech456
('$2a$12$4Ea9ZDC2MtpCPE5AoXJjqKwcgvC4OIJTh2ImDmJ8Nm8UcMxH5YGPD', '06-78901234', 'technician2', 'EMPLOYEE', NULL, 'tech2@example.com', 'PENDING'),

-- Password: robert789
('$2a$12$3DZ8YBC1LsoBOD4ZnWIipJvbfuB3NIISg1HlClI7Ml7TbLwG4XFOC', '06-89012345', 'robert_brown', 'CUSTOMER', '345678905', 'robert.brown@example.com', 'REJECTED'),

-- Password: manager456
('$2a$12$2CY7XAB0KrnAND3YmVHhoIuaetA2MHHRf0GkBkH6Lk6SaKvF3WENB', '06-90123456', 'manager_user', 'CUSTOMER', NULL, 'manager@example.com', 'ACTIVE'),

-- Password: emily789
('$2a$12$1BX6WZA9JqmZMC2XlUGgnHtZdsZ1LGGQe9FjAjG5Kj5RZJuE2VDMA', '06-01234567', 'emily_wilson', 'CUSTOMER', '234567896', 'emily.wilson@example.com', 'ACTIVE'),

-- Password: agent123
('$2a$12$9AW5VYZ8IplYLB1WkTFfmGsYcrY0KFFPd8EiZiF4Ji4QYItD1UCKZ', '06-21345678', 'cs_agent1', 'CUSTOMER', NULL, 'agent1@example.com', 'ACTIVE'),

-- Password: david123
('$2a$12$8ZV4UXY7HokXKA0VjSEelFrXbqX9JEEOc7DhYhE3Ih3PXHsC0TBLY', '06-32456789', 'david_clark', 'CUSTOMER', '456789017', 'david.clark@example.com', 'PENDING'),

-- Password: tech789
('$2a$12$7YU3TWX6GnjWJZ9UiRDdkEqWapW8IDDNb6CgXgD2Hg2OWGrB9SAKX', '06-43567890', 'technician3', 'EMPLOYEE', NULL, 'tech3@example.com', 'ACTIVE'),

-- Password: jennifer456
('$2a$12$6XT2SVW5FmiVIY8ThQCckDpVZoV7HCCMa5BfWfC1Gf1NVFqA8RZJW', '06-54678901', 'jennifer_lee', 'CUSTOMER', '678901238', 'jennifer.lee@example.com', 'ACTIVE'),

-- Password: michael789
('$2a$12$5WS1RUV4EliUIX7SgPBbjCoUYnU6GBBLa4AeVeB0Fe0MUEpZ7QYIV', '06-65789012', 'michael_taylor', 'CUSTOMER', '789012349', 'michael.taylor@example.com', 'REJECTED');
