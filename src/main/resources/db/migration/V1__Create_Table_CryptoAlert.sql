CREATE TABLE crypto_alert (
    id INT PRIMARY KEY AUTO_INCREMENT,
    UserName VARCHAR(255) NOT NULL,
    UserId BIGINT NOT NULL,
    GuildId BIGINT NOT NULL,
    TextChannelId BIGINT NOT NULL,
    DateAdded DATETIME NOT NULL,
    Type VARCHAR(50),
    Threshold VARCHAR(50),
    PreviousThreshold DECIMAL(10, 5),
    PriceTrendDesired TINYINT
);