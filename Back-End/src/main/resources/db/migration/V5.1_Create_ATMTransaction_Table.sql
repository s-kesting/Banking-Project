-- Create ATMTransaction table
CREATE TABLE ATMTransaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sessionID INT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL') NOT NULL,
    amount INT NOT NULL,
    status ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS',
    reason TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sessionID) REFERENCES ATMSession(sessionID)
);
