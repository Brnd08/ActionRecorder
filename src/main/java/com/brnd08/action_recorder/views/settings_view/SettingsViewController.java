package com.brnd08.action_recorder.views.settings_view;

import com.brnd08.action_recorder.views.utils.StageLocation;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SettingsViewController implements Initializable {

    StageLocation actualWindowPosition;

    @FXML
    Button minimizeBttn, closeBttn;

    @FXML
    ChoiceBox<String> positionChoice;

    private StageLocation getSavedPositionFromDatabase() {
        // here you should retrieve the Stage Position from the database
        return StageLocation.LOWER_RIGHT_CORNER;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // adds the list containing the toShowStrings from the StagePosition constants
        positionChoice.getItems().addAll(
                Arrays.stream(StageLocation.values()).map(StageLocation::getToShowString).toList()
        );

        // gets the initial Stage Position
        actualWindowPosition = getSavedPositionFromDatabase();
        // makes the option corresponding to the actual Position selected in the choice box
        positionChoice.getSelectionModel().select(actualWindowPosition.getToShowString());

        positionChoice.setOnAction(event -> {
            String choice = positionChoice.getValue();
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
    public void minimizeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).setIconified(true);
    }

    @FXML
    public void closeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }


    @FXML
    public void showMainView(Event event) {
        System.out.println("Now you should be able to see the main-view scene.");
    }
}
