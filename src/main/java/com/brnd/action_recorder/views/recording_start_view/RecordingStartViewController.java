/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.views.main_view.Main;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class for Recording start view
 */
public class RecordingStartViewController implements Initializable, ViewController {

    private static final Logger logger = LogManager.getLogger(RecordingStartViewController.class);

    private static final int RECORDING_TITLE_LENGTH_LIMIT = 30;

    private final InteractionRecorder interactionRecorder = new InteractionRecorder();

    private FXTrayIcon trayIcon;

    private final boolean useSystemTray = Main.settingsRepository.obtainShowOnTopValue();
    @FXML
    public Button returnButton;

    @FXML
    TextField recordingTitleTexField;
    @FXML
    Button startRecordingButton;

    boolean isRecording = false;

    @FXML
    CheckBox recordKeyboardCheckBox, recordMouseClicksCheckBox, recordMouseMotionCheckBox, recordMouseWheelCheckBox;

    /**
     * Disables all the inputs from the view, intended to be used when a
     * recording has started
     */
    private void disableAllInputs(boolean disable) {
        recordKeyboardCheckBox.setDisable(disable);
        recordMouseClicksCheckBox.setDisable(disable);
        recordMouseMotionCheckBox.setDisable(disable);
        recordMouseWheelCheckBox.setDisable(disable);
        recordingTitleTexField.setDisable(disable);
        returnButton.setDisable(disable);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitRecordingTitleLength();
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
                recordMouseWheelCheckBox.isSelected(),
                recordingTitleTexField.getText()
        );
    }

    /**
     * This method changes GUI behavior when creating a Recording based on the
     * useSystemTray boolean
     * @param currentStage the current stage 
     */
    public void switchToRecordingMode(Stage currentStage) {
        if (!this.isRecording) //if the app is not currently recording do nothing
        {
            return;
        }

        this.disableAllInputs(true);
        this.switchStartRecordingButtonFunctionality();

        if (this.useSystemTray) {
            configureSystemTrayIcon(currentStage);
        }
    }

    private void pauseRecording() {
        logger.log(Level.ALL, "Unimplemented pause Recording functionality");

    }

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

    /**
     * Limits the number of characters of the Recording Name input
     */
    private void limitRecordingTitleLength() {
        this.recordingTitleTexField
                .setTextFormatter(
                        new TextFormatter<Change>(
                                (Change change) -> {

                                    String newString = change.getControlNewText(); // incoming string

                                    int remainingCharacters = RECORDING_TITLE_LENGTH_LIMIT - newString.length();

                                    if (remainingCharacters < 0) {
                                        String incomingChange = change.getText();
                                        // Remove leftover characters to keep the max length
                                        change.setText(incomingChange.substring(0, incomingChange.length() + remainingCharacters));
                                    }

                                    return change;

                                }
                        )
                );
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
