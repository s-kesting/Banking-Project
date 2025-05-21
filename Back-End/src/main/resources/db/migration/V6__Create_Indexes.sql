-- Create indexes for foreign keys to improve query performance
CREATE INDEX idx_account_userID ON "account"(userID);
CREATE INDEX idx_transaction_sender ON "transaction"(sender_account);
CREATE INDEX idx_transaction_receiver ON "transaction"(receiver_account);
CREATE INDEX idx_atmsession_userID ON "ATMSession"(userID);
