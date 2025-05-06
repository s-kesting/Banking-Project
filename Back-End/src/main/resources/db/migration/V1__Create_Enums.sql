-- Create ENUM types for roles, verification status, and account types
CREATE TYPE role_enum AS ENUM ('Customer', 'Employee');
CREATE TYPE verify_status AS ENUM ('Pending', 'Rejected', 'Active', 'Inactive');
CREATE TYPE account_type AS ENUM ('Checking', 'Saving');
