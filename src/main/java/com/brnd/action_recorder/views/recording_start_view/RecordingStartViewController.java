/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.record.capturing.InteractionRecorder;
import com.brnd.action_recorder.views.utils.ViewController;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * FXML Controller class for Recording start view
 */
public class RecordingStartViewController implements Initializable, ViewController {

    private static final Logger logger = LogManager.getLogger(RecordingStartViewController.class);

    private static final int RECORDING_TITLE_LENGTH_LIMIT = 30;

    private final InteractionRecorder interactionRecorder = new InteractionRecorder();
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
     * Disables all the inputs from the view, intended to be used when a recording has started
     */
    private void disableAllInputs() {
        recordKeyboardCheckBox.setDisable(true);
        recordMouseClicksCheckBox.setDisable(true);
        recordMouseMotionCheckBox.setDisable(true);
        recordMouseWheelCheckBox.setDisable(true);
        recordingTitleTexField.setDisable(true);
        returnButton.setDisable(true);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitRecordingTitleLength();
    }

    /**
     * Modifies start recording button functionality based on the isRecoding boolean
     * If its true modify button to stop the recorder, if not modify it to start a recording
     */
    private void switchStartRecordingButtonFunctionality() {
        if (this.isRecording) {
            startRecordingButton.setText("Detener Grabación");
            startRecordingButton.setOnAction(event -> stopRecording());
        } else {
            startRecordingButton.setText("Iniciar grabación");
            startRecordingButton.setOnAction(event -> startRecording());
        }

    }

    /**
     * This method executes needed actions to stop the current recording
     */
    @FXML
    public void stopRecording() {
        interactionRecorder.stopRecording();
        logger.log(Level.TRACE, "Resulting Recording: {}", interactionRecorder.getlastRecording());
        switchStartRecordingButtonFunctionality();
    }

    /**
     * Obtains a recording configuration based on the inserted values on the GUI inputs
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
     * Starts a new recording with the current configuration from the GUI
     */
    @FXML
    public void startRecording() {

        RecordingConfiguration recordingConfiguration = obtainRecordingConfigurationFromGUI();

        logger.log(Level.INFO, "Starting Recording with following configuration: {}", recordingConfiguration);

        try {
            this.interactionRecorder.startRecording(recordingConfiguration);
            disableAllInputs();
            this.isRecording = true;
        } catch (NativeHookException exception) {
            logger.log(Level.ERROR, "Fail to create the Recording", exception);
        }

        if (isRecording) {
            this.switchStartRecordingButtonFunctionality();
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
        if(this.isRecording)
            stopRecording();
        ViewController.super.closeStage(event);
    }

    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }

}
