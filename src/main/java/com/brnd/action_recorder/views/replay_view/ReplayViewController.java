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
package com.brnd.action_recorder.views.replay_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository;
import com.brnd.action_recorder.views.utils.ViewController;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the Main View
 */
public class ReplayViewController implements ViewController, Initializable {

    private static final Logger logger = LogManager.getLogger(ReplayViewController.class);
    private final RecordingsRepository recordingsRepository = new RecordingsRepository();
    private final List<Recording> storedRecordings = recordingsRepository.getAllRecordings();
    @FXML
    Button returnButton;
    @FXML
    Button closeBttn;
    @FXML
    Button minimizeBttn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.log(Level.ALL, "Stored Recordings: \n {}", storedRecordings);
    }

    @FXML
    public void replayRecording(Event event) {
        logger.log(Level.ALL, "Unimplemented functionality replay recording");
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
