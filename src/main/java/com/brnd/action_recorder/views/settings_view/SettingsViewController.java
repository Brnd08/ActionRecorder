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

import com.brnd.action_recorder.views.main_view.Main;
import com.brnd.action_recorder.views.utils.StageLocation;
import com.brnd.action_recorder.views.utils.ViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.brnd.action_recorder.views.main_view.Main.logger;

/**
 * FXML controller for the settings view controller
 */
public class SettingsViewController implements ViewController, Initializable {

    private Settings currentSettings;
    @FXML
    Button browseButton;
    @FXML
    Button saveConfigsButton;
    @FXML
    Button minimizeButton;
    @FXML
    Button closeButton;
    @FXML
    CheckBox alwaysOnTopCheckBox;
    @FXML
    ChoiceBox<String> positionChoiceBox;
    @FXML
    CheckBox useSystemTrayCheckBox;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSavedSettings();
        configurePositionChoiceFunctionality();
        configureAlwaysOnTopFunctionality();
        configureUseSystemTray();
    }

    /**
     * Sets the needed configuration for the position choice functionality
     */
    private void configurePositionChoiceFunctionality() {
        // adds the list containing the toShowStrings from the StagePosition constants
        positionChoiceBox.getItems().addAll(StageLocation.getToShowStringsList());

        // makes the option corresponding to the actual Position selected in the choice box
        positionChoiceBox.getSelectionModel().select(currentSettings.getInitialViewLocation().getToShowString());

        // configure a listener for the positionChoiceBox
        positionChoiceBox.setOnAction(event -> {
            //obtains the enum constant based on the selected value from the position ChoiceBox
            currentSettings.setInitialViewLocation(
                    StageLocation.stageLocationFromToShowString(positionChoiceBox.getValue())
            );
            logger.log(Level.INFO, "Initial Stage Position selected value: {}", currentSettings.getInitialViewLocation() );
        });
    }

    /**
     * Sets the needed configuration for the show always on top feature
     */
    private void configureAlwaysOnTopFunctionality() {

        // sets selecting depending on showAlways on top value
        alwaysOnTopCheckBox.setSelected(currentSettings.isShowAlwaysOnTopEnabled());

        // configure listener for the checkbox
        alwaysOnTopCheckBox.setOnAction(actionEvent -> {
            currentSettings.setShowAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
            logger.log(Level.INFO, "Always on top selected value: {}" , currentSettings.isShowAlwaysOnTopEnabled() );
        });

    }
    

    /**
     * Sets the needed configuration for system tray icon setting feature
     */
    private void configureUseSystemTray() {

        // sets selecting depending on showAlways on top value
        useSystemTrayCheckBox.setSelected(currentSettings.isUseSystemTrayOnRecordingEnabled());

        // configure listener for the checkbox
        useSystemTrayCheckBox.setOnAction(actionEvent -> {
            currentSettings.setUseSystemTrayOnRecordingEnabled(useSystemTrayCheckBox.isSelected());
            logger.log(Level.INFO, "Use system tray selected value: {}" , currentSettings.isShowAlwaysOnTopEnabled() );
        });

    }

    /**
     * Saves the current settings in the database
     */
    @FXML
    public void saveConfigurationOnDatabase() {

        saveSettings(currentSettings);

        logger.log(Level.INFO, "SAVED SETTINGS {}", currentSettings);
    }

    /**
     * Saves the specified Settings values in the database
     * @param settings Settings containing the desired values to be saved
     */
    private void saveSettings(Settings settings){
        Main.settingsRepository.saveInitialStageLocation(settings.getInitialViewLocation());
        Main.settingsRepository.saveShowOnTopValue(settings.isShowAlwaysOnTopEnabled());        
        Main.settingsRepository.saveUseSystemTrayValue(settings.isUseSystemTrayOnRecordingEnabled());        
    }

    /**
     * Loads the settings values stored in the database
     */
    private void loadSavedSettings(){
        this.currentSettings = new Settings (
                Main.settingsRepository.obtainInitialStageLocation()
                ,Main.settingsRepository.obtainShowOnTopValue()
                , Main.settingsRepository.obtainUseSystemTrayValue()
        );
        logger.log(Level.INFO, "Successfully load settings ({}) from database", this.currentSettings.toString() );

    }
    @FXML
    public void browseDirectories() {
        logger.log(Level.ALL, "Unimplemented functionality" );
    }

    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }
    @Override
    public void closeStage(Event event) {
        ViewController.super.closeStage(event);
    }
    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }
}
