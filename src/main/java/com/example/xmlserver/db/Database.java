package com.example.xmlserver.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static final String URL = "jdbc:sqlite:messages.db";
    private static final Object DB_LOCK = new Object();
    private static PreparedStatement preparedStatement;

    static {
        init();
    }

    private static void init() {
        try {
            Connection conn = DriverManager.getConnection(URL);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user TEXT,
                    text TEXT,
                    code INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            }

            preparedStatement = conn.prepareStatement(
                    "INSERT INTO messages(user, text, code) VALUES (?, ?, ?)"
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
        }
    }

    public static void save(String user, String text, int code) {
        synchronized (DB_LOCK) {
            try {
                preparedStatement.setString(1,user);
                preparedStatement.setString(2,text);
                preparedStatement.setInt(3,code);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to save message", e);
            }
        }
    }
}