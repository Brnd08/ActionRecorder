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
package com.brnd.action_recorder.views.recording.recording_saving_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author brdn
 */
public class RecordingSavingViewController implements Initializable, ViewController {

    private static final Logger logger = LogManager.getLogger(RecordingSavingViewController.class);
    private final RecordingsRepository recordingsRepository = new RecordingsRepository();
    private static final int RECORDING_TITLE_LENGTH_LIMIT = 30;
    private Recording recording;
    private String recordingTitle;
    private String recordingDescription;

    @FXML
    private Button closeBttn;
    @FXML
    private Button minimizeBttn;
    @FXML
    private Button saveRecordingButton;
    @FXML
    private TextField recordingTitleTexField;
    @FXML
    private TextArea recordingDescriptionTextArea;
    @FXML
    private TableView eventsTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        limitTextFieldLength(this.recordingTitleTexField, RECORDING_TITLE_LENGTH_LIMIT);
        limitTextFieldLength(this.recordingDescriptionTextArea, 100);
        Platform.runLater(() -> { // to prevent execution until initialization is complete. Otherwise node.getScene returns null
            this.recording = getRecordedRecording();
            logger.log(Level.INFO, "Obtained recording {}", this.recording);
            showRecordedEvents();
        });
    }

    /**
     * Adds the passed recorded events to the GUI table for users visualization
     */
    private void showRecordedEvents() {
        logger.log(Level.ALL, "Unimplemented 'show recorded events' feature.");
    }

    /**
     * Gets the last saved recording from the stage set in the recording start
     * view
     *
     * @return
     */
    private Recording getRecordedRecording() {
        Recording recoveredRecording = null;
        try {
            recoveredRecording = (Recording) closeBttn.getScene().getWindow().getUserData();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Could not recover recording from stage. Exception msg: {}", e.getMessage());
        }
        return recoveredRecording;
    }

    /**
     * Limits the number of characters of the given TextInput
     *
     * @param textInput The TextField to be delimited
     * @param lengtLimit The maximum number of characters to be applied
     */
    private void limitTextFieldLength(TextInputControl textInput, int lengtLimit) {
        textInput
                .setTextFormatter(
                        new TextFormatter<TextFormatter.Change>(
                                (TextFormatter.Change change) -> {
                                    String newString = change.getControlNewText(); // incoming string
                                    int remainingCharacters = lengtLimit - newString.length();

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

    private boolean validateFields(Stage currentStage) {
        logger.log(Level.INFO, "Validating inputs");
        this.recordingTitle = this.recordingTitleTexField.getText();
        this.recordingDescription = this.recordingDescriptionTextArea.getText();
        boolean validInputs = false;
        if (this.recordingTitle.isBlank()) {
            var alertMessage = "Indica un nombre para la grabación";         
            var alertHeader = "Configuración inválida - Action Recorder"; 
                ViewController.createCustomAlert(
                        Alert.AlertType.WARNING, alertMessage, alertHeader, currentStage, ButtonType.OK
                ).show(); // shows the alert
        } else if (this.recordingDescription.isBlank()) {
            var alertMessage = "Indica una descripción para la grabación";
            var alertHeader = "Configuración inválida - Action Recorder"; 
                ViewController.createCustomAlert(
                        Alert.AlertType.WARNING, alertMessage, alertHeader, currentStage, ButtonType.OK
                ).show(); // shows the alert
        } else {
            validInputs = true;
        }
        return validInputs;
    }

    private boolean verifyStoredRecording(int recordingId) {
        logger.log(Level.INFO, "Verifying stored recording");
        Recording storedRecording = this.recordingsRepository.getRecordingById(recordingId);
        logger.log(Level.INFO, "Database Retrieved Recording: {} ", storedRecording);
        return storedRecording != null;
    }

    @FXML
    public void saveRecording(Event event) throws IOException {
        Stage currentStage = StagePositioner.getStageFromEvent(event);
        boolean validInputs = this.validateFields(currentStage);
        if (validInputs) {// save recording only if the inputs were valid
            logger.log(Level.INFO, "Saving recording");
            this.recording.setRecordingTitle(this.recordingTitle);
            this.recording.setRecordingDescription(this.recordingDescription);
            int databaseRecordingId = this.recordingsRepository.insertRecording(this.recording);

            if (this.verifyStoredRecording(databaseRecordingId)) {
                var saveAlertMessage = "Grabación guardada con éxito.";
                ViewController.createCustomAlert(
                        Alert.AlertType.INFORMATION, saveAlertMessage, "Guardar Grabación - Grabadora de Acciones", currentStage, ButtonType.OK
                ).showAndWait(); // styles and shows the alert
                this.navigateToRecordView(event);
            } else {
                var saveAlertMessage = "No se pudo guardar la grabación. Intente de nuevo";
                ViewController.createCustomAlert(
                        Alert.AlertType.ERROR, saveAlertMessage, "Guardar Grabación - Grabadora de Acciones", currentStage, ButtonType.OK
                ).show(); // styles and shows the alert
            }
        }
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
    public void navigateToRecordView(Event event) throws IOException {
        ViewController.super.navigateToRecordView(event);
    }

}
