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
package com.brnd.action_recorder.views.recording.recording_start_view;

import com.brnd.action_recorder.views.main_view.Main;
import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.github.kwhat.jnativehook.NativeHookException;
import java.awt.EventQueue;
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
import javafx.scene.input.InputEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class for Recording start view
 */
public class RecordingStartViewController implements Initializable, ViewController {

    @FXML
    private ToggleButton systemTrayToggleButton;
    @FXML
    private CheckBox minimizeAtRecordingCheckBox;
    @FXML
    private CheckBox recordMouseClicksCheckBox;
    @FXML
    private CheckBox recordMouseMotionCheckBox;
    @FXML
    private CheckBox recordMouseWheelCheckBox;
    @FXML
    private CheckBox recordKeyboardCheckBox;
    @FXML
    private ToggleButton windowToggleButton;
    @FXML
    private Button startRecordingButton;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button returnButton;
    @FXML
    private Button minimizeBttn;
    @FXML
    private Button closeBttn;

    private static final Logger logger = LogManager.getLogger(RecordingStartViewController.class);
    private final NativeRecorder actionsRecorder = new ReplayableNativeRecorder();
    private final ToggleGroup recordModeToggleGroup = new ToggleGroup();
    private boolean isRecording = false;
    private FXTrayIcon trayIcon;
    private boolean rememberRecordingConfig;

    @Override
    public void initialize(URL url, ResourceBundle rb) { //runs at controller initialization
        configureRecordModeToogleGroup();
        this.rememberRecordingConfig = Main.settingsRepository.obtainRememberRecordConfig();
        loadLastRecordConfigs();
    }

    /**
     * Loads the last recording settings if enabled.
     */
    private void loadLastRecordConfigs() {
        if(!this.rememberRecordingConfig)
            return;
        RecordingConfiguration lastRecordConfig = Main.settingsRepository.obtainLastRecordConfig();
        this.recordKeyboardCheckBox.setSelected(lastRecordConfig.recordingKeyboardInteractions());
        this.recordMouseClicksCheckBox.setSelected(lastRecordConfig.recordingMouseClickInteractions());
        this.recordMouseWheelCheckBox.setSelected(lastRecordConfig.recordingMouseWheelInteractions());
        this.recordMouseMotionCheckBox.setSelected(lastRecordConfig.recordingMouseMotionInteractions());
    }

    /**
     * Starts a new recording with the current configuration from the GUI
     *
     * @param event the event that triggered this method call
     */
    @FXML
    public void startRecording(InputEvent event) {
        var currentStage = StagePositioner.getStageFromEvent(event);
        RecordingConfiguration recordingConfiguration = this.obtainRecordingConfigurationFromGUI();

        if (recordingConfiguration.isAtLeastOneListenerEnabled()) {
            Main.settingsRepository.saveLastRecordConfig(recordingConfiguration);
            try {
                logger.log(Level.INFO, "Starting Recording with following configuration: {}", recordingConfiguration);
                this.actionsRecorder.startRecording(recordingConfiguration);
                this.isRecording = true;
                this.switchToRecordingMode(currentStage);
            } catch (NativeHookException exception) {
                logger.log(Level.ERROR, "Fail to start Recording with following configuration: {}",
                        recordingConfiguration, exception);
            }
        } else {
            /* if no listeners were selected shows an alert requesting to select at least one*/
            ViewController.createCustomAlert(
                    Alert.AlertType.WARNING, "Selecciona al menos un evento para grabar", "Configuración inválida - Action Recorder", currentStage, ButtonType.OK
            ).show();
        }
    }

    /**
     * This method executes needed actions to stop the current recording
     *
     * @param event the InputEvent that triggered this method call
     */
    @FXML
    public void stopRecording(Event event) {
        logger.log(Level.INFO, "Stoping current recording");

        this.removeTrayIconIfExits();

        this.actionsRecorder.stopRecording();
        this.isRecording = false;

        var recordedRecording = this.actionsRecorder.getLastRecording();
        this.navigateToSavingView(recordedRecording, StagePositioner.getStageFromEvent(event));
        StagePositioner.getStageFromEvent(event).close(); // closes the current stage after opening saving view
    }

    /**
     * Navigates to the saving view, position in the same coordinates and passes
     * to it the specified recording to it.
     *
     * @param recordedRecording the recorded recording which is intended to be
     * saved.
     * @param currentStage the current stage to get the app position.
     */
    private void navigateToSavingView(Recording recordedRecording, Stage currentStage) {
        try {
            var nextStage = new Stage();
            nextStage.setX(currentStage.getX());
            nextStage.setY(currentStage.getY());
            nextStage.setUserData(recordedRecording);
            ViewController.openView(nextStage, ViewEnum.RECORDING_SAVING);
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Could not navigate to {} view. Exception message: {}", ViewEnum.RECORDING_SAVING, ex.getMessage());
        }
    }

    /**
     * Removes app tray icon created at recording start
     */
    private void removeTrayIconIfExits() {
        if (this.trayIcon != null) {
            this.trayIcon.hide(); // removes the tray icon if its being show
            this.trayIcon = null;
        }
    }

    /**
     * This method configures the Record mode toggle group by adding to it the
     * systemTray and window toggle buttons and configuring them to force to
     * have at least one toggle button selected and to switch the minimize when
     * recording button
     */
    private void configureRecordModeToogleGroup() {
        this.recordModeToggleGroup.selectedToggleProperty()
                .addListener(
                        (selectedToggleProperty, oldSelectedToggleButton, newSelectedToggleButton) -> {
                            /* no selected ToggleButtons  and previously selected toggleButton*/
                            if (newSelectedToggleButton == null && oldSelectedToggleButton != null) {
                                oldSelectedToggleButton.setSelected(true);
                            }
                        }
                );
        /* set minimizeRecording checkbox enabled if system tray toggle button is deselected, otherwise disables it */
        this.systemTrayToggleButton.selectedProperty().addListener(
                (observable, oldValue, newValue) -> minimizeAtRecordingCheckBox.setDisable(newValue)
        );
        this.windowToggleButton.setToggleGroup(this.recordModeToggleGroup);
        this.systemTrayToggleButton.setToggleGroup(this.recordModeToggleGroup);
    }

    /**
     * Disables all the inputs from the view, intended to be used when a
     * recording has started or stopped
     */
    private void disableAllInputs(boolean disable) {
        this.minimizeAtRecordingCheckBox.setDisable(disable);
        this.recordMouseClicksCheckBox.setDisable(disable);
        this.recordMouseMotionCheckBox.setDisable(disable);
        this.recordMouseWheelCheckBox.setDisable(disable);
        this.recordKeyboardCheckBox.setDisable(disable);
        this.systemTrayToggleButton.setDisable(disable);
        this.windowToggleButton.setDisable(disable);
        this.returnButton.setDisable(disable);
    }

    /**
     * This method changes GUI behavior when creating a Recording based on the
     * useSystemTray boolean
     *
     * @param currentStage the current stage
     */
    public void switchToRecordingMode(Stage currentStage) {
        //if the app is not recording at the moment do nothing
        currentStage.setTitle("Grabando... - Grabadora de Acciones");
        this.disableAllInputs(true);
        this.switchStartRecordingButtonFunctionality();

        if (this.systemTrayToggleButton.isSelected()) {
            this.configureSystemTrayIcon(currentStage);
            currentStage.close();
        } else if (this.minimizeAtRecordingCheckBox.isSelected()) {
            this.minimizeBttn.fire();
        }
    }

    /**
     * This method changes GUI behavior when stopping a recording
     *
     * @param currentStage the current stage
     */
    public void switchToRecordingStartMode(Stage currentStage) {
        currentStage.setTitle(ViewEnum.RECORDING_START.getStageTitle());
        this.disableAllInputs(false);
        this.switchStartRecordingButtonFunctionality();
    }

    /**
     * Modifies start recording button functionality based on the isRecoding
     * boolean If its true modify button to stop the recorder, if not modify it
     * to start a recording
     */
    private void switchStartRecordingButtonFunctionality() {
        if (this.isRecording) {
            startRecordingButton.setText("Finalizar");
            startRecordingButton.setOnMouseClicked(this::stopRecording);
            mainPane.requestFocus();// removes focus from start button to prevent firing it while typing with the keyboard during recordings
        } else {
            startRecordingButton.setText("Comenzar");
            startRecordingButton.setOnMouseClicked(this::startRecording);
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
     * Pauses the current recording
     */
    @FXML
    private void pauseRecording() {
        logger.log(Level.ALL, "Pausing recording");
        this.actionsRecorder.pauseRecording();
    }

    /**
     * Resume the current recording intended to be use after pausing a recording
     */
    @FXML
    private void resumeRecording() {
        logger.log(Level.ALL, "Resuming recording");
        this.actionsRecorder.resumeRecording();
    }

    /**
     * Creates and configure the app system tray icon during the recording time
     *
     * @param currentStage The stage that will be related with the tray icon
     */
    private void configureSystemTrayIcon(Stage currentStage) {
        logger.log(Level.INFO, "configuring System Tray Icon");

        this.trayIcon = new FXTrayIcon(currentStage, ViewEnum.getAppIcon());
        this.trayIcon.setTrayIconTooltip("Recording in progress - Action Recorder");

        var stopRecording = new MenuItem("Detener Grabación");
        var pauseRecording = new MenuItem("Pausar Grabación");
        var resumeRecording = new MenuItem("Reanudar Grabación");
        var exitProgram = new MenuItem("Salir del Programa");
        resumeRecording.setDisable(true);

        stopRecording.setOnAction(e
                -> this.closeBttn.fire()
        );
        pauseRecording.setOnAction(e -> {
            EventQueue.invokeLater(() -> { // disables swing pause MenuItem and enables resume one
                trayIcon.getMenuItem(2).setEnabled(true);
                trayIcon.getMenuItem(1).setEnabled(false);
            });
            this.pauseRecording();
        });

        resumeRecording.setOnAction(e -> {
            this.resumeRecording();
            EventQueue.invokeLater(() -> { // disables swing resume MenuItem and enables pause one
                trayIcon.getMenuItem(2).setEnabled(false);
                trayIcon.getMenuItem(1).setEnabled(true);
            });
        });

        exitProgram.setOnAction(event -> {
            this.stopRecording(event);
            this.trayIcon.hide();
            currentStage.close();
        });

        this.trayIcon.addMenuItem(stopRecording);
        this.trayIcon.addMenuItem(pauseRecording);
        this.trayIcon.addMenuItem(resumeRecording);
        this.trayIcon.addMenuItem(exitProgram);
        this.trayIcon.show();
    }

    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }

    @Override
    public void closeStage(Event event) {
        if (this.isRecording && this.trayIcon != null) {
            stopRecording(event);
        }
        ViewController.super.closeStage(event);
    }

    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }

}
