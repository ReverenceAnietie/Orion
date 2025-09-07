package orion;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String URL = "jdbc:postgresql://localhost:5432/ORION_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Admin";
    
    static {
        try {
            Class.forName("org.postgresql.Driver");
            LOGGER.info("PostgreSQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("Failed to load PostgreSQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void ensureDatabaseSchema() {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            String createAccountTable = """
                CREATE TABLE IF NOT EXISTS AccountDetails (
                    AccountNumber BIGINT PRIMARY KEY,
                    FirstName VARCHAR(50) NOT NULL,
                    LastName VARCHAR(50) NOT NULL,
                    Email VARCHAR(100) UNIQUE NOT NULL,
                    PhoneNumber VARCHAR(15),
                    Password VARCHAR(255) NOT NULL,
                    Balance DECIMAL(15,2) DEFAULT 0.0,
                    ProfileImage VARCHAR(255),
                    WithdrawalBank VARCHAR(100),
                    WithdrawalAccount VARCHAR(20),
                    CardType VARCHAR(20),
                    CardBank VARCHAR(100),
                    CardNumber VARCHAR(20),
                    CardExpiry VARCHAR(7),
                    CardCVV VARCHAR(4)
                )
                """;
            stmt.executeUpdate(createAccountTable);
            
            String createTransactionTable = """
                CREATE TABLE IF NOT EXISTS Transactions (
                    TransactionID SERIAL PRIMARY KEY,
                    AccountNumber BIGINT REFERENCES AccountDetails(AccountNumber),
                    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    Type VARCHAR(50) NOT NULL,
                    Amount DECIMAL(15,2) NOT NULL,
                    RecipientAccount VARCHAR(20),
                    ReceiverBank VARCHAR(100),
                    Description TEXT
                )
                """;
            stmt.executeUpdate(createTransactionTable);
            
            String createAddMoneyTable = """
                CREATE TABLE IF NOT EXISTS AddMoney (
                    TransactionID SERIAL PRIMARY KEY,
                    AccountNumber BIGINT REFERENCES AccountDetails(AccountNumber),
                    Amount DECIMAL(15,2) NOT NULL,
                    PaymentMethod VARCHAR(50) NOT NULL,
                    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createAddMoneyTable);
            
            LOGGER.info("Database schema verified/created successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create database schema", e);
            throw new RuntimeException("Database schema creation failed", e);
        }
    }
}