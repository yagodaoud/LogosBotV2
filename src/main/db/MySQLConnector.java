package main.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
public class MySQLConnector {

    public static Connection getConnection() {
        final String dbConnection = "jdbc:mysql://mysql-logos:3306/discordbot";
        final String dbUser =  Dotenv.configure().load().get("DB_USER");
        final String dbPassword = Dotenv.configure().load().get("DB_PASSWORD");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
            System.out.println("Successfully connected to the database");
        } catch (SQLException e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
        }

        return connection;
    }
}
