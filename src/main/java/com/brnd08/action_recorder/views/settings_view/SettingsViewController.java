package com.brnd08.action_recorder.views.settings_view;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.brnd08.action_recorder.views.utils.*;

public class SettingsViewController implements ViewController, Initializable {
    @FXML
    Button browseBttn;
    @FXML
    Button saveBttn;
    @FXML
    Button minimizeBttn;
    @FXML
    Button closeBttn;
    @FXML
    TextField directoryTxt;
    @FXML
    CheckBox alwaysOnTopCheckBox;
    @FXML
    ChoiceBox<String> positionChoiceBox;

    private StageLocation actualWindowPosition;

    private StageLocation getSavedPositionFromDatabase() {
        // here you should retrieve the Stage Position from the database
        return StageLocation.LOWER_RIGHT_CORNER;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // adds the list containing the toShowStrings from the StagePosition constants
        positionChoiceBox.getItems().addAll(
                Arrays.stream(StageLocation.values()).map(StageLocation::getToShowString).toList()
        );

        // gets the initial Stage Position
        actualWindowPosition = getSavedPositionFromDatabase();
        // makes the option corresponding to the actual Position selected in the choice box
        positionChoiceBox.getSelectionModel().select(actualWindowPosition.getToShowString());

        positionChoiceBox.setOnAction(event -> {
            String choice = positionChoiceBox.getValue();
            //obtains the enum constant based on his toShowString value and the choice value.
            actualWindowPosition =
                    Arrays.stream(StageLocation.values())
                            .filter(
                                    stagePosition -> stagePosition.getToShowString().equals(choice)
                            ).findFirst().orElseThrow();
            System.out.println(actualWindowPosition);
        });
    }

    @FXML
    public void saveConfiguration(Event event){
        System.out.println("Here the app should have saved the selected configuration.");
    }

    @FXML
    public void browseDirectories(Event event){
        System.out.println("You should be able to select a directory from the explorer.");
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
