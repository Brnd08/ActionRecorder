package com.brnd.action_recorder.views.utils;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


/**
 * This interface is intended to be implemented by Scene controllers in order to reduce repetitive
 * code, by providing methods for views navigation and related methods.
 */
public interface ViewController {

    @FXML
    public default void minimizeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).setIconified(true);
    }

    @FXML
    public default void closeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }
    @FXML
    public default void navigateToMainView(Event event) throws IOException {
        navigateToView(event, ViewEnum.MAIN);
    }
    @FXML
    public default void navigateToSettingsView(Event event) throws IOException {
        navigateToView(event, ViewEnum.SETTINGS);
    }
    @FXML
    public default void navigateToReplayView(Event event) throws IOException {
        System.out.println("You should be able to see Replay Scene.");
    }
    @FXML
    public default void navigateToRecordView(Event event) throws IOException {
        System.out.println("You should be able to see Record Scene.");
    }
    @FXML
    public default void enableCoordinatesMode(Event event) {
        System.out.println("You should be in Coordinates mode now");
    }

    public static void openView(Stage stage, ViewEnum nextView) throws IOException {
        // Load Fxml view
        FXMLLoader fxmlLoader;

        try {
            fxmlLoader =
                    new FXMLLoader(
                            Objects.requireNonNull(
                                    ViewEnum.class.getResource(
                                            nextView.getFxmlPath() // get the fxml file path from View constant
                                    )
                            )
                    );
        } catch (NullPointerException e) {
            System.out.println("No se encontro el archivo settingsView.fxml");
            throw e;
        }

        Parent root = fxmlLoader.load();

        // Add the fxml view to a new scene with the previous scene width and height
        Scene newScene = new Scene(root);


        // Needed configuration to make the application background transparent
        newScene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setResizable(false);

        // Adds taskbar icon
        stage.getIcons().add(0, nextView.getAppIcon() );
        // Modify the stage title
        stage.setTitle(nextView.getStageTitle());


        // Adds the new scene to the Stage
        stage.setScene(newScene);


        // makes stage draggable by mouse interaction
        StagePositioner.addDragFunctionalityToStage(stage, newScene);

        // display the stage on the screen
        stage.show();

    }

    private static void navigateToView(Event clickEvent, ViewEnum nextView) throws IOException, NullPointerException {
        Stage previousStage =
                (Stage) ((Button) clickEvent.getSource())
                        .getScene()
                        .getWindow();

        previousStage.close();
        openView(new Stage(), nextView);
    }

}
