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

import com.brnd.action_recorder.views.utils.ViewController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author brdn
 */
public class RecordingSavingViewController implements Initializable, ViewController{

    private static final int RECORDING_TITLE_LENGTH_LIMIT = 30;
    
     @FXML
    private Button closeBttn;
    @FXML
    private Button minimizeBttn;

    @FXML
    private TextField recordingTitleTexField;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            limitTextFieldLength(this.recordingTitleTexField, RECORDING_TITLE_LENGTH_LIMIT);
    }
    /**
     * Limits the number of characters of the given TextField
     * @param textfield The TextField to be delimited
     * @param lengtLimit The maximum number of characters to be applied
     */
    private void limitTextFieldLength(TextField textfield, int lengtLimit) {
        textfield
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

    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }

    @Override
    public void closeStage(Event event) {
        ViewController.super.closeStage(event);
    }
    
}
