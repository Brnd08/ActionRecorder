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
package com.brnd.action_recorder.views.replay.replay_selection_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.brnd.action_recorder.views.utils.ViewEnum;
import com.github.kwhat.jnativehook.NativeInputEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the Replay View
 */
public class ReplaySelectionViewController implements ViewController, Initializable {
    private static final Logger logger = LogManager.getLogger(ReplaySelectionViewController.class);
    private final RecordingsRepository recordingsRepository = new RecordingsRepository();
    private final List<Recording> storedRecordings = recordingsRepository.getAllRecordings();
    @FXML
    Button returnButton;
    @FXML
    Button closeBttn;
    @FXML
    Button minimizeBttn;
    @FXML
    Button replaySelectedButton;
    @FXML
    TableView<Recording> recordingsTable;
    @FXML
    TableColumn<Recording, String> recordingNameCol;
    @FXML
    TableColumn<Recording, String> recordingDescriptionCol;
    @FXML
    TableColumn<Recording, String> recordingDateCol;
    @FXML
    TableColumn<Recording, Float> recordingDurationCol;

    @FXML
    TableView<Map.Entry<Double, NativeInputEvent>> recordingActionsTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.log(Level.ALL, "{} recording retrieved from database.", storedRecordings.size());
        this.addRowsToRecordingsTable();
    }

    /**
     * Fills the recordingsTable using the storedRecordings list
     * by adding a new row for each recording contained in the storedRecordings
     */
    private void addRowsToRecordingsTable() {
        /*
         Links the table columns to the RecordingRow properties
         */
        recordingNameCol.setCellValueFactory(
                new PropertyValueFactory<>("recordingTitle")
        );
        recordingDescriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("recordingDescription")
        );
        recordingDateCol.setCellValueFactory(
                new PropertyValueFactory<>("recordingDate")
        );
        recordingDurationCol.setCellValueFactory(
                new PropertyValueFactory<>("recordingDuration")
        );
        ObservableList<Recording> recordingRows = FXCollections.observableArrayList();
        recordingRows.addAll(storedRecordings);
        recordingsTable.setItems(recordingRows);
    }

    @FXML
    public void startSelectedReplay(Event event) {
        Stage currentStage = StagePositioner.getStageFromEvent(event);

        var selectedRecording = recordingsTable.getSelectionModel().getSelectedItem(); // get selected recording
        if (selectedRecording != null) {
            logger.log(Level.INFO, "Selected recording: {}", selectedRecording.toShortString());
            navigatetoStartReplayView(selectedRecording, currentStage);
        }else{
            var alertMessage = "Para continuar debes seleccionar una grabación para reproducir";
            ViewController.createCustomAlert(
                    Alert.AlertType.ERROR, alertMessage, "Reproducir Grabación - Grabadora de Acciones", currentStage, ButtonType.OK
            ).show(); // styles and shows the alert
        }
    }

    /**
     * Navigates to the saving view, position in the same coordinates and passes
     * to it the specified recording to it.
     *
     * @param recordedRecording the recorded recording which is intended to be
     *                          saved.
     * @param currentStage      the current stage to get the app position.
     */
    private void navigatetoStartReplayView(Recording recordedRecording, Stage currentStage) {
        try {
            var nextStage = new Stage();
            nextStage.setX(currentStage.getX());
            nextStage.setY(currentStage.getY());
            nextStage.setUserData(recordedRecording);
            ViewController.openView(nextStage, ViewEnum.REPLAY_START);
            currentStage.close();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Could not navigate to {} view. Exception message: {}", ViewEnum.REPLAY_START, ex.getMessage());
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
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }

    @FXML
    public void selectRecording(Event event) {
        logger.log(Level.ALL, "Unimplemented functionality selectRecording(Event)");
    }
}
