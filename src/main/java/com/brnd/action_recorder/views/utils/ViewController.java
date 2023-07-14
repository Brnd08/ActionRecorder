package com.brnd.action_recorder.views.utils;

import com.brnd.action_recorder.views.settings_view.Settings;
import com.brnd.action_recorder.views.settings_view.SettingsService;
import com.brnd.action_recorder.views.settings_view.SettingsViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Objects;

import static com.brnd.action_recorder.views.main_view.Main.logger;


/**
 * This interface is intended to be implemented by Scene controllers in order to reduce repetitive
 * code, by providing methods for views navigation and related methods.
 */
public interface ViewController {
    public static final SettingsService settingsService = new SettingsService();

    public static void openView(Stage stage, ViewEnum nextView) throws IOException {
        logger.log(Level.TRACE, "Opening {} view", nextView.name());
        // Load Fxml view
        FXMLLoader fxmlLoader;
        logger.log(Level.TRACE, "Creating FXMLLoader for {} view with fxml path {}", nextView.name(), nextView.getFxmlPath());
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
            logger.log(Level.FATAL, "Could not find settingsView.fxml file", e);
            throw e;
        }

        Parent root = fxmlLoader.load();
        logger.log(Level.TRACE, "Configuring {} view", nextView.name());
        // Add the fxml view to a new scene with the previous scene width and height
        Scene newScene = new Scene(root);


        // Needed configuration to make the application background transparent
        newScene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setResizable(false);

        // Adds taskbar icon
        stage.getIcons().add(0, ViewEnum.getAppIcon());
        // Modify the stage title
        stage.setTitle(nextView.getStageTitle());


        // Adds the new scene to the Stage
        stage.setScene(newScene);

        // Sets show on view enabled or disabled depending on specific value
        stage.setAlwaysOnTop(settingsService.getSavedSettings().isShowAlwaysOnTopEnabled());


        // makes stage draggable by mouse interaction
        StagePositioner.addDragFunctionalityToStage(stage, newScene);

        logger.log(Level.TRACE, "Showing {} view", nextView.name());
        // display the stage on the screen
        stage.show();


    }

    private static void navigateToView(Event clickEvent, ViewEnum nextView) throws IOException, NullPointerException {
        Stage previousStage =
                (Stage) ((Button) clickEvent.getSource())
                        .getScene()
                        .getWindow();

        logger.log(Level.TRACE, "Navigating to {} view from {} view", nextView.name(), ViewEnum.fromTitle(previousStage.getTitle()));


        Stage nextStage = new Stage(); // instantiates the new stage

        openView(nextStage, nextView); // configures and displays the new stage
        // positions the new stage in the same place as the previous stage
        nextStage.setX(previousStage.getX());
        nextStage.setY(previousStage.getY());
        logger.log(Level.TRACE, "Closing {} view ", ViewEnum.fromTitle(previousStage.getTitle()));
        previousStage.close();
    }

    @FXML
    public default void minimizeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).setIconified(true);
        logger.log(Level.TRACE, "Minimizing view" );
    }

    @FXML
    public default void closeStage(Event event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        logger.log(Level.TRACE, "Closing view and stopping app execution" );
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
        logger.log(Level.ALL, "Unimplemented functionality" );
    }

    @FXML
    public default void navigateToRecordView(Event event) throws IOException {
        logger.log(Level.ALL, "Unimplemented functionality" );
    }

    @FXML
    public default void enableCoordinatesMode(Event event) {
        logger.log(Level.ALL, "Unimplemented functionality" );
    }

}
