package com.brnd08.action_recorder;

import com.brnd08.action_recorder.utils.StagePositioner;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PrincipalViewController {

    @FXML
    Button minimizeBttn, closeBttn;
    @FXML
    Button playBttn, recordBttn, settingsBttn;
    private StagePositioner stagePositioner;

    @FXML
    public void minimizeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).setIconified(true);
    }

    @FXML
    public void closeStage(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    public void showNewRecordScene(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));

        if (stagePositioner == null) {
            stagePositioner = new StagePositioner(previousStage);
        }

        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be able to see New Record scene now");
    }

    public void showSettingsScene(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));

        if (stagePositioner == null) {
            stagePositioner = new StagePositioner(previousStage);
        }

        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be able to Settings scene now");
    }

    public void showPlayRecordScene(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));

        if (stagePositioner == null) {
            stagePositioner = new StagePositioner(previousStage);
        }

        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be able to see Play Record scene now");
    }

    public void enableCoordinatesMode(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));

        if (stagePositioner == null) {
            stagePositioner = new StagePositioner(previousStage);
        }

        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be in Coordinates mode now");
    }


}