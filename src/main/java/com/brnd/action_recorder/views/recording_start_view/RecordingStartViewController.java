/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.record.capturing.InteractionRecorder;
import com.brnd.action_recorder.views.utils.ViewController;
import com.github.kwhat.jnativehook.NativeHookException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author brdn
 */
public class RecordingStartViewController implements Initializable, ViewController {

    private static final Logger logger = LogManager.getLogger(RecordingStartViewController.class);

    private static final int RECORDING_TITLE_LENGTH_LIMIT = 30;

    private final InteractionRecorder interactionRecorder = new InteractionRecorder();

    @FXML
    TextField recordingTitleTexField;

    @FXML
    CheckBox recordKeyboardCheckBox, recordMouseClicksCheckBox, recordMouseMotionCheckBox, recordMouseWheelCheckBox;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitRecordingTitleLength();

    }

    @FXML
    public void startRecording(Event event) {
        
        RecordingConfiguration recordingConfiguration = new RecordingConfiguration(
                recordKeyboardCheckBox.isSelected(),
                recordMouseMotionCheckBox.isSelected(),
                recordMouseClicksCheckBox.isSelected(),
                recordMouseWheelCheckBox.isSelected(),
                recordingTitleTexField.getText()
        );

        logger.log(Level.INFO, "Starting Recording with following configuration: {}", recordingConfiguration);

        this.interactionRecorder.setRecordConfiguration(recordingConfiguration);
        boolean recordingStarted = false;
        
        try {
            this.interactionRecorder.startRecording();
            recordingStarted = true;
        } catch (NativeHookException exception) {
            logger.log(Level.ERROR, "Could not start Recording. Exception message: {}", exception);
        }

        if (recordingStarted) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    interactionRecorder.stopRecording();
                    logger.log(Level.TRACE, "Resulting Recording: {}", interactionRecorder.getlastRecording());
                    
                }
            }, (long) 5 * 1000);
            timer.cancel();
            timer.purge();
            
        }

    }

    private void limitRecordingTitleLength() {
        this.recordingTitleTexField
                .setTextFormatter(
                        new TextFormatter<Change>(
                                (Change change) -> {

                                    String newString = change.getControlNewText();

                                    int remainingCharacters = RECORDING_TITLE_LENGTH_LIMIT - newString.length();

                                    if (remainingCharacters < 0) {
                                        String incomingChange = change.getText();
                                        change.setText(incomingChange.substring(0, incomingChange.length()+remainingCharacters));
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
        ViewController.super.closeStage(event);
    }

    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }

}
