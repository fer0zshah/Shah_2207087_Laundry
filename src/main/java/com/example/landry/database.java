package com.example.landry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class database {


    private static final String URL = "jdbc:sqlite:laundry.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDB() {
        // SQL to create the USERS table
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "phone TEXT PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "password TEXT NOT NULL, "
                + "address TEXT, "
                + "role TEXT NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Database initialized and Users table checked.");


            createDefaultAdmin(conn);

        } catch (SQLException e) {
            System.out.println("Error initializing DB: " + e.getMessage());
        }
    }

    private static void createDefaultAdmin(Connection conn) {
        String checkSql = "SELECT count(*) FROM users WHERE phone = 'admin'";
        try {
            Statement stmt = conn.createStatement();
            if (stmt.executeQuery(checkSql).getInt(1) == 0) {

                String insertSql = "INSERT INTO users(phone, name, password, address, role) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, "admin");
                pstmt.setString(2, "Shop Owner");
                pstmt.setString(3, "1234");
                pstmt.setString(4, "Shop Office");
                pstmt.setString(5, "Admin");
                pstmt.executeUpdate();
                System.out.println("Default Admin created (User: admin, Pass: 1234)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}