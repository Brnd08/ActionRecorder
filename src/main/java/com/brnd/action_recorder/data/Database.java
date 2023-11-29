/*
 * Copyright (C) 2023 Brandon Velazquez & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.brnd.action_recorder.data;

import static com.brnd.action_recorder.data.DatabaseTable.SETTINGS;
import com.brnd.action_recorder.views.settings_view.Settings;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class has database related functionalities such as database
 * initialization or to get connection instances
 */
public class Database {

    private static final String SQLITE_JDBC_CLASS = "org.sqlite.JDBC";

    private static final String APP_FOLDER
            = System.getenv("LOCALAPPDATA")
            + File.separator + "Brnd08"
            + File.separator + "ActionRecorder";

    private static final String DATA_FOLDER
            = APP_FOLDER
            + File.separator + "data";

    private static final String DB_FILE_PATH
            = DATA_FOLDER
            + File.separator + "action_recorder.db";

    private static final String DB_URL = "jdbc:sqlite:" + File.separator + DB_FILE_PATH;

    public static final Logger logger = LogManager.getLogger(Database.class);

    static {
        try {
            logger.log(Level.TRACE, "Database Initialization.");
            Database.initializeDatabase();
        } catch (SQLException ex) {
            logger.log(Level.FATAL, "Could not initialize the database", ex);
            System.exit(1);
        }

    }

    private Database() { // To prevent class instantiation in utility classes
        throw new UnsupportedOperationException("Utility class can not be instantiated");
    }

    private static Connection sqliteConnection;

    /**
     * Method to return a singleton {@link Connection} instance for the SQLite
     * database
     *
     * @return {@link Connection}
     * @throws java.sql.SQLException if an Exception occurs during
     * databaseInitialization
     */
    public static synchronized Connection getSqliteConnection() throws SQLException {
        initializeConnection();
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
        createAppDirs();
        initializeConnection();

        for (DatabaseTable table : DatabaseTable.values()) {
            logger.log(Level.TRACE, "Creating {} table ", table.name());
            createTable(table);
            logger.log(Level.TRACE, "Table {} created successfully ", table.name());
        }

        insertDefaultSettingsValues();

    }

    /**
     * Insets the default settings method to the database intended to be use
     * when first app execution
     */
    private static void insertDefaultSettingsValues() {
        PreparedStatement preparedStatement = null;
        String insertScript = SETTINGS.getInsertFirstSentence();
        Settings defaultSettings = Settings.DEFAULT_SETTINGS;
        try {
            logger.log(Level.ALL, "Adding default Settings to database with script: {}",
                     insertScript);
            preparedStatement = sqliteConnection.prepareStatement(insertScript);
            preparedStatement.setInt(1, 1);
            preparedStatement.setBoolean(2, defaultSettings.isShowAlwaysOnTopEnabled());
            preparedStatement.setString(3, defaultSettings.getInitialViewLocation().name());
            preparedStatement.setBoolean(4, defaultSettings.isRemberRecordConfig());
            int modifiedRows = preparedStatement.executeUpdate();
            if (modifiedRows == 0) {
                logger.log(Level.ALL, "Already configured settings were found. No need to add default values.");
            } else {
                logger.log(Level.ALL, "Default settings values got added: {}.", defaultSettings);
            }

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Fail to insert Default Settings in database with the following query {}",
                     insertScript);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStatement on {} method. Exception msg: {}",
                             "SettingsService.SettingsRepository.saveShowOnTopValue()", ex.getMessage());
                }
            }
        }
    }

    /**
     * Method to instantiate the singleton {@link Connection} instance
     *
     * @throws SQLException Either if an exception occur when loading JDBC class
     * or creating the connection
     */
    private static void initializeConnection() throws SQLException {
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
    }

    /**
     * Creates the app related directories
     */
    private static void createAppDirs() {

        File programRootDir = new File(APP_FOLDER);
        if (!programRootDir.exists()) {
            logger.log(Level.INFO, "The program root directory '{}' {}.",
                     APP_FOLDER, (programRootDir.mkdirs() ? "was created" : "couldn't be created"));
        } else {
            logger.log(Level.INFO, "The program root directory '{}' alreadyExists.", APP_FOLDER);
        }

        File programDataDir = new File(DATA_FOLDER);
        if (!programDataDir.exists()) {
            logger.log(Level.INFO, "The program data directory '{}' {}.",
                     DATA_FOLDER, (programDataDir.mkdirs() ? "was created" : "couldn't be created"));
        } else {
            logger.log(Level.INFO, "The program data directory '{}' alreadyExists.", DATA_FOLDER);
        }

    }

    /**
     * This method creates the specified table in the app database
     *
     * @param table The table to be created
     * @throws SQLException Either If an exception occurs during table creation
     *
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
                    logger.log(Level.ERROR, "Could not close PrepareStatement for {} table", table.name(), ex);
                }
            }
        }
    }

    public static void deleteDatabase() {
        try {
            Files.delete(new File(DB_FILE_PATH).toPath());
        } catch ( IOException e) {
            logger.log(Level.ERROR, "Could not delete database. File path: {}. Cause msg: {}", DB_URL, e.getMessage());
        }

    }
}
