package com.brnd08.action_recorder.views.main_view;

import com.brnd08.action_recorder.views.utils.ViewEnum;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static com.brnd08.action_recorder.views.utils.CommonViewUtils.navigateToView;

public class MainViewController {

    @FXML
    Button minimizeBttn, closeBttn;
    @FXML
    Button playBttn, recordBttn, settingsBttn;

    @FXML
    public void minimizeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).setIconified(true);
    }

    @FXML
    public void closeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    public void showNewRecordScene(Event event) {
        System.out.println("You should be able to see New Record scene now");
    }

    public void showSettingsScene(Event event) throws IOException {
        navigateToView(event, ViewEnum.SETTINGS);
    }

    public void showPlayRecordScene(Event event) {
        System.out.println("You should be able to see Play Record scene now");
    }

    public void enableCoordinatesMode(Event event) {
        System.out.println("You should be in Coordinates mode now");
    }


}