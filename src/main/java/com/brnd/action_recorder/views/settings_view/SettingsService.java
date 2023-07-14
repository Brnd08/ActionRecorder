package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;

import static com.brnd.action_recorder.views.main_view.Main.logger;

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
        public StageLocation obtainInitialStageLocation(){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.obtainInitialStageLocation() method functionality");
            return StageLocation.LOWER_LEFT_CORNER;
        }
        public boolean obtainShowOnTopValue(){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.obtainShowOnTopValue() method functionality");
            return true;
        }
        public String obtainExportDirectoryPath(){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.obtainExportDirectoryPath() method functionality");
            return "";
        }
        public void saveInitialStageLocation(StageLocation newInitialStageLocation){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveInitialStageLocation(StageLocation) method functionality");
        }
        public void saveShowOnTopValue(boolean newShowOnTopValue){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveShowOnTopValue(boolean) method functionality");
        }
        public void saveExportDirectoryPath(String newPath){
            logger.log(Level.ALL, "Unimplemented SettingsRepository.saveExportDirectoryPath(String) method functionality");
        }

    }
}
