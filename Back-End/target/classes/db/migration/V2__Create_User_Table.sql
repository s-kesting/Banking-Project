-- Create User table
CREATE TABLE users (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(225),
    username VARCHAR(100) NOT NULL,
    role role_enum NOT NULL,
    bsn VARCHAR(50),
    email VARCHAR(100),
    verify_user verify_status NOT NULL
);
