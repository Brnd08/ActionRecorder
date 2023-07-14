package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;

import static com.brnd.action_recorder.views.main_view.Main.logger;

public class SettingsService {

    private static final SettingsRepository settingsRepository = new SettingsRepository();
    private static StageLocation initialStagePosition;
    private static boolean showAlwaysOnTop;

    public static synchronized StageLocation getInitialStagePosition() {
        return initialStagePosition;
    }
    private static synchronized void setInitialStagePosition(StageLocation newLocation) {
        initialStagePosition = newLocation;
    }
    public static synchronized boolean isShowAlwaysOnTopEnabled() {
        return showAlwaysOnTop;
    }
    public static synchronized void setShowAlwaysOnTop(boolean showAlwaysOnTopValue) {
        showAlwaysOnTop = showAlwaysOnTopValue;
    }

    public void saveSettings(Settings settingsToSave) {
        settingsRepository.saveShowOnTopValue(settingsToSave.isShowAlwaysOnTopEnabled());
        settingsRepository.saveInitialStageLocation(settingsToSave.getInitialViewLocation());
    }
    public Settings retrieveLastSavedSettings(){
        return new Settings(
                settingsRepository.obtainInitialStageLocation(),
                settingsRepository.obtainShowOnTopValue()
        );
    }


    public static class SettingsRepository {
        public StageLocation obtainInitialStageLocation(){

            logger.log(Level.ALL, "Unimplemented obtainInitialStageLocation() method functionality");
            return StageLocation.LOWER_LEFT_CORNER;

        }
        public boolean obtainShowOnTopValue(){
            logger.log(Level.ALL, "Unimplemented obtainShowOnTopValue() method functionality");
            return true;
        }
        public String obtainExportDirectoryPath(){
            logger.log(Level.ALL, "Unimplemented obtainExportDirectoryPath() method functionality");

            return "";
        }
        public void saveInitialStageLocation(StageLocation newInitialStageLocation){
            logger.log(Level.ALL, "Unimplemented saveInitialStageLocation(StageLocation) method functionality");
        }
        public void saveShowOnTopValue(boolean newShowOnTopValue){
            logger.log(Level.ALL, "Unimplemented saveShowOnTopValue(boolean) method functionality");
        }
        public void saveExportDirectoryPath(String newPath){
            logger.log(Level.ALL, "Unimplemented saveExportDirectoryPath(String) method functionality");
        }

    }
}
