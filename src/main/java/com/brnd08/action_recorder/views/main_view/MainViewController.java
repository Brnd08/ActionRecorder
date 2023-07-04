package com.brnd08.action_recorder.views.main_view;

import com.brnd08.action_recorder.views.settings_view.SettingsViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.brnd08.action_recorder.utils.StagePositioner.addStageDragFunctionality;

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
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));


        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be able to see New Record scene now");
    }

    public void showSettingsScene(Event event) throws IOException {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));

        double sceneWidth = previousStage.getScene().getWidth();
        double sceneHeight = previousStage.getScene().getHeight();

        Image programIcon = previousStage.getIcons().get(0);
        previousStage.close();
        System.out.println("You should be able to Settings scene now");

        // Load Fxml view
        FXMLLoader fxmlLoader;

        try {
            fxmlLoader =
                    new FXMLLoader(
                            Objects.requireNonNull(
                                    SettingsViewController.class.getResource("settingsView.fxml")
                            )
                    );
        } catch (NullPointerException e) {
            System.out.println("No se encontro el archivo settingsView.fxml");
            throw e;
        }

        Parent root = fxmlLoader.load();
        // Add the fxml view to a new scene with the previous scene width and height
        Scene newScene = new Scene(root, sceneWidth, sceneHeight);


        // Adds the new scene content to the application's Stage
        previousStage.setScene(newScene);
        // Modify the stage title
        previousStage.setTitle("Settings || Action Recorder");

        addStageDragFunctionality(previousStage, newScene);
        // display the stage on the screen
        previousStage.show();
    }

    public void showPlayRecordScene(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));


        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be able to see Play Record scene now");
    }

    public void enableCoordinatesMode(Event event) {
        Stage previousStage = ((Stage) (((Button) event.getSource()).getScene().getWindow()));


        double stageWidth = previousStage.getWidth();
        double stageHeight = previousStage.getHeight();
        double xStageCoordinate = previousStage.getX();
        double yStageCoordinate = previousStage.getY();
        Image programIcon = previousStage.getIcons().get(0);
        System.out.println("You should be in Coordinates mode now");
    }


}