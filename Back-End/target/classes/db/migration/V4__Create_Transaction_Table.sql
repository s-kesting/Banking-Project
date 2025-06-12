
-- Create Transaction table
CREATE TABLE transaction (
    transactionID INT AUTO_INCREMENT PRIMARY KEY,
    sender_account INTEGER NULL,
    transaction_type transaction_type NULL,
    receiver_account INTEGER NULL,
    amount DOUBLE PRECISION NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_account) REFERENCES account(accountID),
    FOREIGN KEY (receiver_account) REFERENCES account(accountID)
);
