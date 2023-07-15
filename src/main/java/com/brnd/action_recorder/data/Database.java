package com.brnd.action_recorder.data;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static final Logger logger = LogManager.getLogger(Database.class);
    private static final String SQLITE_JDBC_CLASS = "org.sqlite.JDBC";
    private static String DB_FILE = "\\action_recorder.db";
    private static String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection sqliteConnection;

    /**
     * Method to return a singleton {@link Connection} instance for the sqlite database
     *
     * @return {@link Connection}
     */
    public static synchronized Connection getSqliteConnection() throws SQLException, ClassNotFoundException {

        String.format("");
        try {
            if (sqliteConnection == null || sqliteConnection.isClosed()) {
                Class.forName(SQLITE_JDBC_CLASS);
                sqliteConnection = DriverManager.getConnection(DB_URL);
            }
            return sqliteConnection;
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, "Could not load the JDBC driver {}", SQLITE_JDBC_CLASS);
            logger.log(Level.FATAL, e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not create the sqlite database Connection");
            logger.log(Level.ERROR, e);
            throw e;
        }
    }
}

