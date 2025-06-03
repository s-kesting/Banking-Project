-- Create ENUM types for roles, verification status, and account types
CREATE TYPE role_enum AS ENUM ('CUSTOMER', 'EMPLOYEE');
CREATE TYPE verify_status AS ENUM ('PENDING', 'REJECTED', 'ACTIVE', 'INACTIVE');
CREATE TYPE account_type AS ENUM ('Checking', 'Saving');
CREATE TYPE transaction_type AS ENUM ('TRANSFER', 'DEPOSIT', 'WITHDRAW');
