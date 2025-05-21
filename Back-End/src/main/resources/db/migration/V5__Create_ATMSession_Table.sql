-- Create ATMSession table
CREATE TABLE "ATMSession" (
    sessionID SERIAL PRIMARY KEY,
    userID INTEGER NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES "users"(userID)
);
