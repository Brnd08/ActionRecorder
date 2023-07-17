package com.brnd.action_recorder.data;

import java.io.File;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    public static final Logger logger = LogManager.getLogger(Database.class);

    private static final String SQLITE_JDBC_CLASS = "org.sqlite.JDBC";

    private static final String APP_FOLDER
            = System.getenv("LOCALAPPDATA")
            + File.pathSeparator + "Brnd08"
            + File.pathSeparator + "Action Rcorder";

    private static final String DB_FILE_PATH
            = APP_FOLDER
            + File.pathSeparator + "data"
            + File.pathSeparator + "action_recorder.db";

    private static final String DB_URL = "jdbc:sqlite:" + File.pathSeparator + DB_FILE_PATH;

    private static Connection sqliteConnection;

    /**
     * Method to return a singleton {@link Connection} instance for the SQLite
     * database
     *
     * @return {@link Connection}
     */
    public static synchronized Connection getSqliteConnection() throws SQLException {
        try {
            if (sqliteConnection == null || sqliteConnection.isClosed()) {
                Class.forName(SQLITE_JDBC_CLASS);
                sqliteConnection = DriverManager.getConnection(DB_URL);
            }
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, "Could not load the JDBC driver class: {}", SQLITE_JDBC_CLASS);
            logger.log(Level.FATAL, e);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not create the sqlite database Connection");
            logger.log(Level.ERROR, e);
            throw e;
        }

        return sqliteConnection;
    }

    /**
     * This method initializes the SQLite database by creating the tables
     * specified in the {@link DatabaseTable} enumeration class
     *
     * @throws SQLException either if Could not get the database
     * {@link Connection} instance or if an error occurs during a table creation
     * process
     */
    public static void initializeDatabase() throws SQLException {

        for (DatabaseTable table : DatabaseTable.values()) {
            logger.log(Level.TRACE, "Creating {} table ", table.name());
            createTable(table);
            logger.log(Level.TRACE, "Table {} created successfully ", table.name());

        }

    }

    /**
     * This method creates the specified table in the app database
     *
     * @param table The table to be created
     * @throws SQLException If could not create table or if could not close the
     * PreparedStatement used for update execution
     */
    private static void createTable(DatabaseTable table) throws SQLException {
        PreparedStatement pStatement = null;
        try {
            pStatement = getSqliteConnection().prepareStatement(table.getCreateTableSentence());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not create {} table", table.name());
            throw e;
        } finally {
            if (pStatement != null) {
                try {
                    pStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament for {} table", table.name());
                }
            }
        }
    }
;

}
