-- Add additional constraints
-- Ensure transaction amount is positive
ALTER TABLE transaction ADD CONSTRAINT positive_amount CHECK (amount > 0);
