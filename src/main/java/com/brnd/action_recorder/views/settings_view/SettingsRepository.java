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
package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.data.DataUtils;
import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.data.DatabaseTable;
import com.brnd.action_recorder.views.recording.recording_start_view.RecordingConfiguration;
import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class has the needed functionalities to insert, select and update Settings from the
 * database
 */
public class SettingsRepository {
    private static final Logger logger = LogManager.getLogger(SettingsRepository.class);
    private final Connection connection;
    private static final String SETTINGS_ID_FIELD = "settings_id";
    private static final String INITIAL_STAGE_LOCATION_FIELD = "initial_stage_location";
    private static final String SHOW_ON_TOP_FIELD = "always_on_top";
    private static final String REMEMBER_RECORD_CONFIG = "remember_last_record_config";
    private static final String LAST_RECORD_CONFIGS_FIELD = "last_record_configs";

    private static final String SELECT_LAST_RECORD_CONFIGS_FIELD =
            String.format("SELECT %s FROM %s WHERE %s = %d;",
                    LAST_RECORD_CONFIGS_FIELD,
                    DatabaseTable.PREFERENCES.name(),
                    "preferences_id",
                    1
            );

    private static final String SELECT_INITIAL_STAGE_LOCATION_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = %d;",
            INITIAL_STAGE_LOCATION_FIELD,
            DatabaseTable.SETTINGS.name(),
            SETTINGS_ID_FIELD,
            1
    );

    private static final String SELECT_REMEMBER_RECORD_CONFIG_FIELD_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = %d;",
            REMEMBER_RECORD_CONFIG,
            DatabaseTable.SETTINGS.name(),
            SETTINGS_ID_FIELD,
            1
    );
    private static final String UPDATE_REMEMBER_RECORD_CONFIG_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = %d;",
            DatabaseTable.SETTINGS.name(),
            REMEMBER_RECORD_CONFIG,
            SETTINGS_ID_FIELD,
            1
    );
    private static final String SELECT_SHOW_ON_TOP_FIELD_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = %d;",
            SHOW_ON_TOP_FIELD,
            DatabaseTable.SETTINGS.name(),
            SETTINGS_ID_FIELD,
            1
    );
    private static final String UPDATE_INITIAL_STAGE_LOCATION_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = %d;",
            DatabaseTable.SETTINGS.name(),
            INITIAL_STAGE_LOCATION_FIELD,
            SETTINGS_ID_FIELD,
            1
    );

    private static final String UPDATE_SHOW_ON_TOP_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = %d;",
            DatabaseTable.SETTINGS.name(),
            SHOW_ON_TOP_FIELD,
            SETTINGS_ID_FIELD,
            1
    );
    private static final String UPDATE_LAST_RECORD_CONFIGS_SENTENCE
            = String.format("REPLACE INTO %s (%s, preferences_id) VALUES (?, 1);",
            DatabaseTable.PREFERENCES.name(),
            LAST_RECORD_CONFIGS_FIELD
    );


    public SettingsRepository() {
        try {
            connection = Database.getSqliteConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public StageLocation obtainInitialStageLocation() {
        PreparedStatement preparedStatement = null;
        StageLocation initialStageLocation;

        try {
            preparedStatement = connection.prepareStatement(SELECT_INITIAL_STAGE_LOCATION_SENTENCE);
            ResultSet resultSet = preparedStatement.executeQuery();
            String location = resultSet.getString(1);

            if (location != null) {
                initialStageLocation = StageLocation.valueOf(location);
            } else {
                initialStageLocation = StageLocation.CENTER;
                logger.log(Level.ALL, "No saved Initial Stage Location value found in database, using default value {}"
                        , initialStageLocation.name());
            }

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not retrieve Initial Stage Location value from database with following query {}"
                    , SELECT_INITIAL_STAGE_LOCATION_SENTENCE);
            logger.log(Level.ERROR, e);
            initialStageLocation = null;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error message {}"
                            , "SettingsService.SettingsRepository.obtainInitialStageLocation()", ex.getMessage());
                }
            }
        }

        return initialStageLocation;
    }

    public boolean obtainRememberRecordConfig() {
        boolean rememberRecordConfig = false;
        logger.log(Level.ALL, "Retrieving remember record config boolean value from database.");

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_REMEMBER_RECORD_CONFIG_FIELD_SENTENCE)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            rememberRecordConfig = resultSet.getBoolean(1);
        } catch (Exception e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording duration, using default value: {}. Exception message: {}. Excecuted query {}",
                    rememberRecordConfig, e.getMessage(), SELECT_REMEMBER_RECORD_CONFIG_FIELD_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return rememberRecordConfig;
    }

    public void saveRememberRecordConfig(boolean rememberConfig) {
        logger.log(Level.ALL, "Saving database remember record config boolean: ({})", rememberConfig);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REMEMBER_RECORD_CONFIG_SENTENCE)) {
            preparedStatement.setBoolean(1, rememberConfig);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count", modifiedRows);
        } catch (Exception e) {
            logger.log(
                    Level.ERROR,
                    "Could not update remember record config value value. Executed query {}. Exception message: {}",
                    UPDATE_REMEMBER_RECORD_CONFIG_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }

    public RecordingConfiguration obtainLastRecordConfig() {
        RecordingConfiguration lastRecordConfig = new RecordingConfiguration(false,false, false, false);
        logger.log(Level.ALL, "Retrieving last record configs from database.");

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LAST_RECORD_CONFIGS_FIELD)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            lastRecordConfig = DataUtils.objectFromBytes(resultSet.getBytes(1), RecordingConfiguration.class);
        } catch (Exception e) {
            logger.log(
                    Level.INFO,
                    "Could not retrieve last record configs , using default value: {}. Exception message: {}. Excecuted query {}",
                    lastRecordConfig, e.getMessage(), SELECT_LAST_RECORD_CONFIGS_FIELD
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return lastRecordConfig;
    }

    public void saveLastRecordConfig(RecordingConfiguration recordConfig) {
        logger.log(Level.ALL, "Saving last record configs: ({})", recordConfig);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LAST_RECORD_CONFIGS_SENTENCE)) {
            preparedStatement.setBytes(1, DataUtils.toBytes(recordConfig));
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Successfully execute script with a {} modified rows count", modifiedRows);
        } catch (Exception e) {
            logger.log(
                    Level.ERROR,
                    "Could not update last record configs value value. Executed query {}. Exception message: {}",
                    UPDATE_LAST_RECORD_CONFIGS_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }
    public boolean obtainShowOnTopValue() {
        PreparedStatement preparedStatement = null;
        boolean showOnTop;

        try {
            preparedStatement = connection.prepareStatement(SELECT_SHOW_ON_TOP_FIELD_SENTENCE);
            ResultSet resultSet = preparedStatement.executeQuery();
            showOnTop = resultSet.getBoolean(1);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not retrieve show on top value from database with following query {}", SELECT_SHOW_ON_TOP_FIELD_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved obtain show on top value found in database, using default value {}", true);
            showOnTop = true;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}"
                            , "SettingsService.SettingsRepository.obtainShowOnTopValue()", ex.getMessage());
                }
            }
        }

        return showOnTop;
    }


    public String obtainExportDirectoryPath() {
        logger.log(Level.ALL, "Unimplemented SettingsRepository.obtainExportDirectoryPath() method functionality");
        return "";
    }

    public void saveInitialStageLocation(StageLocation newInitialStageLocation) {
        PreparedStatement preparedStatement = null;

        try {
            logger.log(Level.ALL, "Saving Initial Stage Location value ({}) in database with script {}"
                    , newInitialStageLocation.name(), UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
            preparedStatement = connection.prepareStatement(UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
            preparedStatement.setString(1, newInitialStageLocation.name());
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);


        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not save Initial Stage Location value ({}) in database following query {}"
                    , newInitialStageLocation, UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "SettingsService.SettingsRepository.saveInitialStageLocation()", ex.getMessage());
                }
            }
        }
    }

    public void saveShowOnTopValue(boolean newShowOnTopValue) {
        PreparedStatement preparedStatement = null;
        try {
            logger.log(Level.ALL, "Saving show on top value ({}) in database with script {}"
                    , newShowOnTopValue, UPDATE_SHOW_ON_TOP_SENTENCE);
            preparedStatement = connection.prepareStatement(UPDATE_SHOW_ON_TOP_SENTENCE);
            preparedStatement.setBoolean(1, newShowOnTopValue);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not save Show on top value ({}) in database following query {}", UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method", "SettingsService.SettingsRepository.saveShowOnTopValue()");
                    logger.log(Level.ERROR, ex);

                }
            }
        }
    }

    public void saveExportDirectoryPath(String newPath) {
        logger.log(Level.ALL, "Unimplemented SettingsRepository.saveExportDirectoryPath(String) method functionality");
    }

}
