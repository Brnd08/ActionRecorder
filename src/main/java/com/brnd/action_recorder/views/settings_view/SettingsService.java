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
        private static final String UPDATE_INITIAL_STAGE_LOCATION_SENTENCE
                = String.format("UPDATE %s SET %s = (?) WHERE %s = %d;",
                        DatabaseTable.SETTINGS.name(),
                        INITIAL_STAGE_LOCATION_FIELD,
                        SETTINGS_ID_FIELD,
                        0
                );
        
        private static final String UPDATE_SHOW_ON_TOP_SENTENCE
                = String.format("UPDATE %s SET %s = (?) WHERE %s = %d;",
                        DatabaseTable.SETTINGS.name(),
                        SHOW_ON_TOP_FIELD,
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
                logger.log(Level.ERROR, "Could not retrieve show on top value from database with following query {}", SELECT_SHOW_ON_TOP_FIELD_SENTENCE);
                logger.log(Level.ERROR, e);
                logger.log(Level.ALL, "No saved obtain show on top value found in database, using default value {}", true);
                showOnTop = true;
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException ex) {
                        logger.log(Level.ERROR, "Could not close PrepareStament on {} method", "SettingsService.SettingsRepository.obtainShowOnTopValue()");
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
                preparedStatement = connection.prepareStatement(UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
                preparedStatement.setString(1, newInitialStageLocation.name());
                int modifiedRows  = preparedStatement.executeUpdate();                

                logger.log(Level.ALL, "Save Initial Stage Location value ({}) in database with {} modified rows.", newInitialStageLocation.name(), modifiedRows);

            } catch (SQLException e) {
                logger.log(Level.ERROR, "Could not save Initial Stage Location value ({}) in database following query {}",  UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
                logger.log(Level.ERROR, e);
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException ex) {
                        logger.log(Level.ERROR, "Could not close PrepareStament on {} method", "SettingsService.SettingsRepository.saveInitialStageLocation()");
                    }
                }
            }
        }

        public void saveShowOnTopValue(boolean newShowOnTopValue) {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(UPDATE_SHOW_ON_TOP_SENTENCE);
                preparedStatement.setBoolean(1, newShowOnTopValue);
                int modifiedRows  = preparedStatement.executeUpdate();                
                logger.log(Level.ALL, "Save Show on top value ({}) in database with {} modified rows.", newShowOnTopValue, modifiedRows);
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Could not save Show on top value ({}) in database following query {}",  UPDATE_INITIAL_STAGE_LOCATION_SENTENCE);
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
}
