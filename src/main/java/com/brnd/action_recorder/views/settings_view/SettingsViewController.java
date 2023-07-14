package com.brnd.action_recorder.views.settings_view;

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

public class SettingsViewController implements ViewController, Initializable {
    private static final SettingsService settingsService = new SettingsService();
    private static Settings currentSettings = settingsService.retrieveLastSavedSettings();
    @FXML
    Button browseButton;
    @FXML
    Button saveConfigsButton;
    @FXML
    Button minimizeButton;
    @FXML
    Button closeButton;
    @FXML
    TextField directoryTxt;
    @FXML
    CheckBox alwaysOnTopCheckBox;
    @FXML
    ChoiceBox<String> positionChoiceBox;

    /**
     * Configure the settings functionalities at class instantiation
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurePositionChoiceFunctionality();
        configureAlwaysOnTopFunctionality();
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
            SettingsViewController.setInitialStageLocation(
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
            setShowAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
            logger.log(Level.INFO, "Always on top selected value: {}" , currentSettings.isShowAlwaysOnTopEnabled() );
        });

    }

    @FXML
    public void saveConfigurationOnDatabase() {
        logger.log(Level.ALL, "Unimplemented functionality" );

        Settings newSettings = new Settings(getInitialStageLocation(), isShowAlwaysOnTopEnabled());

        settingsService.saveSettings(newSettings);

        logger.log(Level.INFO, "SAVES SETTINGS {}", newSettings);
    }
    @FXML
    public void browseDirectories() {
        logger.log(Level.ALL, "Unimplemented functionality" );
    }

    private static synchronized void setCurrentSettings(Settings newCurrentSettings){
        currentSettings = newCurrentSettings;

    }

    public static StageLocation getInitialStageLocation() {
        return currentSettings.getInitialViewLocation();
    }
    private static void setInitialStageLocation(StageLocation newLocation) {
        currentSettings.setInitialViewLocation( newLocation);
    }
    public static  boolean isShowAlwaysOnTopEnabled() {
        return currentSettings.isShowAlwaysOnTopEnabled();
    }
    public static  void setShowAlwaysOnTop(boolean showAlwaysOnTopValue) {
        currentSettings.setShowAlwaysOnTop(showAlwaysOnTopValue);
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
