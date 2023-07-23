package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.data.Database;
import static com.brnd.action_recorder.data.Database.logger;
import com.brnd.action_recorder.data.DatabaseTable;
import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SettingsService {

    private final SettingsRepository settingsRepository = new SettingsRepository();

    public Settings getSavedSettings() {
        return new Settings(
                settingsRepository.obtainInitialStageLocation(),
                settingsRepository.obtainShowOnTopValue()
        );
    }

    public void saveSettings(Settings settingsToSave) {
        settingsRepository.saveShowOnTopValue(settingsToSave.isShowAlwaysOnTopEnabled());
        settingsRepository.saveInitialStageLocation(settingsToSave.getInitialViewLocation());
    }

    private static class SettingsRepository {

        private final Connection connection;

        private static final String SETTINGS_ID_FIELD = "settings_id";
        private static final String INITIAL_STAGE_LOCATION_FIELD = "initial_stage_location";
        private static final String SHOW_ON_TOP_FIELD = "always_on_top";

        private static final String SELECT_INITIAL_STAGE_LOCATION_SENTENCE
                = String.format("SELECT %s FROM %s WHERE %s = %d;",
                        INITIAL_STAGE_LOCATION_FIELD,
                        DatabaseTable.SETTINGS.name(),
                        SETTINGS_ID_FIELD,
                        0
                );
        private static final String SELECT_SHOW_ON_TOP_FIELD_SENTENCE
                = String.format("SELECT %s FROM %s WHERE %s = %d;",
                        SHOW_ON_TOP_FIELD,
                        DatabaseTable.SETTINGS.name(),
                        SETTINGS_ID_FIELD,
                        0
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
                    logger.log(Level.ALL, "No saved Initial Stage Location value found in database, using default value {}", StageLocation.LOWER_LEFT_CORNER.name());
                    initialStageLocation = StageLocation.LOWER_LEFT_CORNER;
                }

            } catch (SQLException e) {
                logger.log(Level.ERROR, "Could not retrieve Initial Stage Location value from database with following query {}", SELECT_INITIAL_STAGE_LOCATION_SENTENCE);
                logger.log(Level.ERROR, e);
                initialStageLocation = null;
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException ex) {
                        logger.log(Level.ERROR, "Could not close PrepareStament on {} method", "SettingsService.SettingsRepository.obtainInitialStageLocation()");
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
                logger.log(Level.ERROR, "Could not retrieve Initial Stage Location value from database with following query {}", SELECT_INITIAL_STAGE_LOCATION_SENTENCE);
                logger.log(Level.ERROR, e);

                logger.log(Level.ALL, "No saved Initial Stage Location value found in database, using default value {}", StageLocation.LOWER_LEFT_CORNER.name());
                showOnTop = true;
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException ex) {
                        logger.log(Level.ERROR, "Could not close PrepareStament on {} method", "SettingsService.SettingsRepository.obtainInitialStageLocation()");
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
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveInitialStageLocation(StageLocation) method functionality");
        }

        public void saveShowOnTopValue(boolean newShowOnTopValue) {
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveShowOnTopValue(boolean) method functionality");
        }

        public void saveExportDirectoryPath(String newPath) {
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveExportDirectoryPath(String) method functionality");
        }

    }
}
