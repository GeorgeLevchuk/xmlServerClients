package com.example.xmlserver.db;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:messages.db";

    static {
        init();
    }

    private static void init() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user TEXT,
                    text TEXT,
                    code INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(String user, String text, int code) {

        String sql = "INSERT INTO messages(user, text, code) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, text);
            ps.setInt(3, code);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}