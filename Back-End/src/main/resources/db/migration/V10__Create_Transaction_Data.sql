INSERT INTO "transaction" (sender_account, receiver_account, amount, description, initiated_by, created_at) VALUES
-- Recent transactions (last 30 days)
(1, 3, 150.00, 'Monthly rent payment', 'admin_user', '2025-05-20 14:30:00'),
(3, 5, 75.50, 'Grocery shopping reimbursement', 'john_doe', '2025-05-20 09:15:00'),
(9, 1, 500.00, 'Salary transfer', 'manager_user', '2025-05-19 16:45:00'),
(14, 16, 25.00, 'Coffee money', 'jennifer_lee', '2025-05-19 12:20:00'),
(7, 9, 200.00, 'Freelance work payment', 'technician1', '2025-05-18 18:30:00'),
(19, 21, 1000.00, 'Monthly savings transfer', 'technician3', '2025-05-18 08:00:00'),
(3, 8, 45.75, 'Shared dinner bill', 'john_doe', '2025-05-17 20:15:00'),
(12, 14, 300.00, 'Equipment purchase', 'emily_wilson', '2025-05-17 11:45:00'),
(1, 7, 125.00, 'Service fee payment', 'admin_user', '2025-05-16 15:20:00'),
(16, 3, 80.00, 'Book purchase refund', 'cs_agent1', '2025-05-16 13:10:00'),

-- Medium timeframe transactions (1-3 months ago)
(9, 19, 2500.00, 'Investment transfer', 'manager_user', '2025-04-15 10:30:00'),
(5, 1, 350.00, 'Consulting fee', 'customer_service', '2025-04-12 14:20:00'),
(14, 8, 120.00, 'Subscription payment', 'jennifer_lee', '2025-04-10 09:45:00'),
(21, 12, 750.00, 'Quarterly bonus', 'michael_taylor', '2025-04-08 16:00:00'),
(3, 16, 90.50, 'Utility bill split', 'john_doe', '2025-03-28 11:30:00'),
(7, 14, 275.00, 'Equipment maintenance', 'technician1', '2025-03-25 13:15:00'),
(1, 9, 1200.00, 'Business loan payment', 'admin_user', '2025-03-20 08:45:00'),
(12, 5, 65.00, 'Gift money', 'emily_wilson', '2025-03-18 19:20:00'),
(19, 3, 400.00, 'Contract payment', 'technician3', '2025-03-15 12:00:00'),
(8, 21, 55.25, 'Parking fee reimbursement', 'sarah_jones', '2025-03-12 17:30:00'),

-- Older transactions (3-6 months ago)
(9, 7, 800.00, 'Equipment purchase', 'manager_user', '2025-02-20 14:15:00'),
(1, 12, 180.00, 'Training course fee', 'admin_user', '2025-02-18 10:20:00'),
(14, 19, 950.00, 'Project milestone payment', 'jennifer_lee', '2025-02-15 16:45:00'),
(5, 8, 110.75, 'Team lunch expense', 'customer_service', '2025-02-10 12:30:00'),
(21, 1, 325.00, 'License renewal fee', 'michael_taylor', '2025-01-25 09:15:00'),
(3, 14, 220.00, 'Software subscription', 'john_doe', '2025-01-20 15:40:00'),
(16, 7, 75.00, 'Travel expense', 'cs_agent1', '2025-01-18 11:25:00'),
(12, 9, 600.00, 'Consulting project', 'emily_wilson', '2025-01-15 13:50:00'),
(19, 5, 145.50, 'Office supplies', 'technician3', '2025-01-12 08:30:00'),
(7, 21, 385.00, 'Maintenance contract', 'technician1', '2025-01-08 14:20:00'),

-- High-value transactions
(2, 4, 5000.00, 'Investment opportunity', 'admin_user', '2025-01-05 10:00:00'),
(10, 6, 3250.00, 'Business partnership', 'manager_user', '2024-12-28 15:30:00'),
(22, 20, 2750.00, 'Equipment upgrade', 'michael_taylor', '2024-12-20 11:45:00'),
(4, 10, 1850.00, 'Quarterly payment', 'technician1', '2024-12-15 09:20:00'),
(6, 22, 4100.00, 'Annual contract', 'sarah_jones', '2024-12-10 16:15:00'),

-- Small daily transactions
(1, 5, 12.50, 'ATM withdrawal fee', 'admin_user', '2025-05-21 08:00:00'),
(8, 12, 35.00, 'Online purchase', 'sarah_jones', '2025-05-21 10:30:00'),
(16, 19, 18.75, 'Transportation cost', 'cs_agent1', '2025-05-21 07:45:00'),
(14, 3, 42.00, 'Subscription service', 'jennifer_lee', '2025-05-20 22:15:00'),
(7, 1, 28.50, 'Service charge', 'technician1', '2025-05-20 19:30:00'),
(21, 16, 67.25, 'Utility payment', 'michael_taylor', '2025-05-20 16:20:00');
