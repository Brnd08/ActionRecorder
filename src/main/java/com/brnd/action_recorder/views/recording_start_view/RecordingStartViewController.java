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
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.views.recording_start_view.RecordingConfiguration;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.github.kwhat.jnativehook.NativeHookException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class for Recording start view
 */
public class RecordingStartViewController implements Initializable, ViewController {

    private static final Logger logger = LogManager.getLogger(RecordingStartViewController.class);
    private final InteractionRecorder interactionRecorder = new InteractionRecorder();
    private FXTrayIcon trayIcon;
    boolean isRecording = false;

    @FXML
    Button returnButton;

    @FXML
    private ToggleButton windowToggleButton;
    @FXML
    private final ToggleGroup recordModeToggleGroup = new ToggleGroup();
    @FXML
    private ToggleButton systemTrayToggleButton;

    @FXML
    CheckBox recordKeyboardCheckBox, recordMouseClicksCheckBox, recordMouseMotionCheckBox, recordMouseWheelCheckBox;
    @FXML

    Button startRecordingButton;

    /**
     * This method configures the Record mode toggle group by adding to it the
     * systemTray and window toggle buttons and configuring them to force to
     * have at least one toggle button selected
     */
    private void configureRecordModeToogleGroup() {
        this.recordModeToggleGroup.selectedToggleProperty()
                .addListener((selectedToggleProperty, oldSelectedToggleButton, newSelectedToggleButton) -> {
                    // no selected ToggleButtons  and previously selected toggleButton
                    if (newSelectedToggleButton == null && oldSelectedToggleButton != null){
                            oldSelectedToggleButton.setSelected(true);
                    }
                });

        this.windowToggleButton.setToggleGroup(this.recordModeToggleGroup);
        this.systemTrayToggleButton.setToggleGroup(this.recordModeToggleGroup);
    }

    /**
     * Disables all the inputs from the view, intended to be used when a
     * recording has started
     */
    private void disableAllInputs(boolean disable) {
        recordKeyboardCheckBox.setDisable(disable);
        recordMouseClicksCheckBox.setDisable(disable);
        recordMouseMotionCheckBox.setDisable(disable);
        recordMouseWheelCheckBox.setDisable(disable);
        windowToggleButton.setDisable(disable);
        systemTrayToggleButton.setDisable(disable);
        returnButton.setDisable(disable);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureRecordModeToogleGroup();
    }

    /**
     * Modifies start recording button functionality based on the isRecoding
     * boolean If its true modify button to stop the recorder, if not modify it
     * to start a recording
     */
    private void switchStartRecordingButtonFunctionality() {
        if (this.isRecording) {
            startRecordingButton.setText("Detener Grabación");
            startRecordingButton.setOnAction(event -> stopRecording());
        } else {
            startRecordingButton.setText("Iniciar grabación");
            startRecordingButton.setOnAction(event -> startRecording(event));
        }

    }

    /**
     * This method executes needed actions to stop the current recording
     */
    @FXML
    public void stopRecording() {
        interactionRecorder.stopRecording();
        logger.log(Level.TRACE, "Resulting Recording: {}", interactionRecorder.getlastRecording());
        this.isRecording = false;
        this.disableAllInputs(false);
        switchStartRecordingButtonFunctionality();
        if (this.trayIcon != null && this.trayIcon.isShowing()) { // if the tray icon is already defined and showing remove it from the system tray
            trayIcon.hide();
        }
    }

    /**
     * Obtains a recording configuration based on the inserted values on the GUI
     * inputs
     *
     * @return RecordingConfiguration containing the configurations
     */
    private RecordingConfiguration obtainRecordingConfigurationFromGUI() {
        return new RecordingConfiguration(
                recordKeyboardCheckBox.isSelected(),
                recordMouseMotionCheckBox.isSelected(),
                recordMouseClicksCheckBox.isSelected(),
                recordMouseWheelCheckBox.isSelected()
        );
    }

    /**
     * This method changes GUI behavior when creating a Recording based on the
     * useSystemTray boolean
     *
     * @param currentStage the current stage
     */
    public void switchToRecordingMode(Stage currentStage) {
        if (!this.isRecording) //if the app is not currently recording do nothing
        {
            return;
        }

        this.disableAllInputs(true);
        this.switchStartRecordingButtonFunctionality();

        if (this.systemTrayToggleButton.isSelected()) {
            configureSystemTrayIcon(currentStage);
        }
    }

    private void pauseRecording() {
        logger.log(Level.ALL, "Unimplemented pause Recording functionality");
    }

    /**
     * Creates and configure the app system tray icon during the recording time
     *
     * @param currentStage The stage that will be related with the tray icon
     */
    private void configureSystemTrayIcon(Stage currentStage) {
        logger.log(Level.INFO, "configuring System Tray Icon");
        trayIcon = new FXTrayIcon(currentStage, ViewEnum.getAppIcon());
        trayIcon.setTrayIconTooltip("Recording in progress | Action Recorder");
        trayIcon.show();

        MenuItem stopRecording = new MenuItem("Stop Recording");
        MenuItem pauseRecording = new MenuItem("Pause Recording");
        MenuItem resumeRecording = new MenuItem("Resume Recording");
        resumeRecording.setDisable(true);

        stopRecording.setOnAction(e -> {
            logger.log(Level.INFO, "Stoping Recording from System Tray icon");
            stopRecording();
        });

        pauseRecording.setOnAction(e -> {
            logger.log(Level.INFO, "Pausing recording from System Tray icon");
            pauseRecording();
            resumeRecording.setDisable(false);
            pauseRecording.setDisable(true);
        });

        resumeRecording.setOnAction(e -> {
            logger.log(Level.INFO, "Resuming recording from System Tray icon");
            stopRecording();
            pauseRecording.setDisable(false);
        });

        MenuItem exitProgram = new MenuItem("Exit");
        exitProgram.setOnAction(e -> {
            logger.log(Level.INFO, "Exit program call from System Tray icon");
            stopRecording();
            trayIcon.hide();
            currentStage.close();
        });

        trayIcon.addMenuItem(stopRecording);
        trayIcon.addMenuItem(pauseRecording);
        trayIcon.addMenuItem(resumeRecording);
        trayIcon.addMenuItem(exitProgram);
    }

    /**
     * Starts a new recording with the current configuration from the GUI
     *
     * @param event the event that triggered this method call
     */
    @FXML
    public void startRecording(Event event) {
        Stage currentStage = StagePositioner.getStageFromEvent(event);

        RecordingConfiguration recordingConfiguration = obtainRecordingConfigurationFromGUI();

        if (!recordingConfiguration.isAtLeastOneListenerEnabled()) {
            // verifies if at least one listener is enabled, if not displays an alert and aborts method execution
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecciona al menos un evento para grabar", ButtonType.OK);
            alert.setHeaderText("Configuración inválida - Action Recorder");
            alert.initOwner(currentStage);
            ViewEnum.styleAlert(alert).show(); // styles and shows the alert
            return;
        }

        logger.log(Level.INFO, "Starting Recording with following configuration: {}", recordingConfiguration);

        try {
            this.interactionRecorder.startRecording(recordingConfiguration);
            this.isRecording = true;
            switchToRecordingMode(currentStage);
        } catch (NativeHookException exception) {
            logger.log(Level.ERROR, "Fail to create the Recording", exception);
        }

    }

    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }

    @Override
    public void closeStage(Event event) {
        if (this.isRecording) {
            stopRecording();
        }
        ViewController.super.closeStage(event);
    }

    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }

}
