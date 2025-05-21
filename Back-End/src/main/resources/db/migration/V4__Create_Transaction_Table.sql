
-- Create Transaction table
CREATE TABLE "transaction" (
    transactionID SERIAL PRIMARY KEY,
    sender_account INTEGER NOT NULL,
    receiver_account INTEGER NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    description VARCHAR(255),
    initiated_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_account) REFERENCES "account"(accountID),
    FOREIGN KEY (receiver_account) REFERENCES "account"(accountID)
);
