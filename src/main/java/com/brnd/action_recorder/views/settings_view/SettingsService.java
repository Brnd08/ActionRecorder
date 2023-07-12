package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.views.utils.StageLocation;
import org.apache.logging.log4j.Level;

import static com.brnd.action_recorder.views.main_view.Main.logger;

public class SettingsService {
    public StageLocation retrieveSavedPosition() {
        // here you should retrieve the Stage Position from the database
        return StageLocation.LOWER_RIGHT_CORNER;
    }
    public boolean retrieveShowAlwaysOnTop() {
        // here you should retrieve the always on top boolean from the database
        return true;
    }
    public void saveConfiguration(Settings settingsToSave) {

    }
}
