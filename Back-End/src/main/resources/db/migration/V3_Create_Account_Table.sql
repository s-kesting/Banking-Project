-- Create Account table
CREATE TABLE account (
    accountID SERIAL PRIMARY KEY,
    userID INTEGER NOT NULL,
    IBAN VARCHAR(34) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    daily_limit DOUBLE PRECISION,
    absolute_limit DOUBLE PRECISION,
    verify_account verify_status NOT NULL,
    account_type account_type NOT NULL,
    FOREIGN KEY (userID) REFERENCES "user"(userID)
);
