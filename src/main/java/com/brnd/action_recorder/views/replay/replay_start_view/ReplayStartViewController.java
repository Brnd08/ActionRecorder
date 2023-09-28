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
package com.brnd.action_recorder.views.replay.replay_start_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Replay View
 */
public class ReplayStartViewController implements ViewController, Initializable {
    private static final Logger logger = LogManager.getLogger(ReplayStartViewController.class);
    private final RecordingsRepository recordingsRepository = new RecordingsRepository();
    private final List<Recording> storedRecordings = recordingsRepository.getAllRecordings();
    @FXML    Button returnButton;
    @FXML
    Button closeBttn;
    @FXML
    Button minimizeBttn;
    @FXML
    Button replaySelectedButton;
    @FXML
    TableView<Recording> recordingsTable;
    @FXML
    TableView<Recording> recordingActionsTable;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.log(Level.ALL, "Stored Recordings: \n {}", storedRecordings);
    }



    /**
     * Navigates to the saving view, position in the same coordinates and passes
     * to it the specified recording to it.
     *
     * @param recordedRecording the recorded recording which is intended to be
     * saved.
     * @param currentStage the current stage to get the app position.
     */
    private void navigatetoStartReplayView(Recording recordedRecording, Stage currentStage) {
        try {
            var nextStage = new Stage();
            nextStage.setX(currentStage.getX());
            nextStage.setY(currentStage.getY());
            nextStage.setUserData(recordedRecording);
            ViewController.openView(nextStage, ViewEnum.REPLAY_START);
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
