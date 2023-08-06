package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.data.DatabaseTable;
import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.brnd.action_recorder.data.Database.logger;

/**
 * This class has the needed functionalities to insert, select and update Settings from the
 * database
 */
public class SettingsRepository {

    private final Connection connection;

    private static final String SETTINGS_ID_FIELD = "settings_id";
    private static final String INITIAL_STAGE_LOCATION_FIELD = "initial_stage_location";
    private static final String SHOW_ON_TOP_FIELD = "always_on_top";

    private static final String SELECT_INITIAL_STAGE_LOCATION_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = %d;",
                    INITIAL_STAGE_LOCATION_FIELD,
                    DatabaseTable.SETTINGS.name(),
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
                    ,newInitialStageLocation, UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
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
                }
            }
        }
    }

    public void saveExportDirectoryPath(String newPath) {
        logger.log(Level.ALL, "Unimplemented SettingsRepository.saveExportDirectoryPath(String) method functionality");
    }

}
