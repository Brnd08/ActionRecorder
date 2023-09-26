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
package com.brnd.action_recorder.views.utils;

import com.brnd.action_recorder.views.main_view.Main;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.InputEvent;

/**
 * This interface is intended to be implemented by Scene controllers in order to
 * reduce repetitive code, by providing methods for views navigation and related
 * methods.
 */
public interface ViewController {

    /**
     * Configures, loads and show a specific view in to the specified Stage
     *
     * @param stage the Stage to be used to display the view
     * @param view the desired view constant to be showed
     * @throws IOException if and exception occurs when loading the fxml file of
     * the view
     */

    public static void openView(Stage stage, ViewEnum view) throws IOException {
        logger.log(Level.TRACE, "Opening {} view", view.name());
        // Load Fxml view
        FXMLLoader fxmlLoader;
        logger.log(Level.TRACE, "Creating FXMLLoader for {} view with fxml path {}", view.name(), view.getFxmlPath());
        try {
            fxmlLoader
                    = new FXMLLoader(
                            Objects.requireNonNull(
                                    ViewEnum.class.getResource(
                                            view.getFxmlPath() // get the fxml file path from View constant
                                    )
                            )
                    );
        } catch (NullPointerException e) {
            logger.log(Level.FATAL, "Could not find fxml file. fxml specified path: {}", view.getFxmlPath(), e);
            throw e;
        }

        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (Exception e) {
            logger.log(Level.FATAL, "Something went wrong while loading FXML file, verify its declared controller class or sintax:", e);
            throw e;
        }
        
        logger.log(Level.TRACE, "Configuring {} view", view.name());
        // Add the fxml view to a new scene with the previous scene width and height
        Scene newScene = new Scene(root);

        // Needed configuration to make the application background transparent
        newScene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setResizable(false);

        // Adds taskbar icon
        stage.getIcons().add(0, ViewEnum.getAppIcon());
        // Modify the stage title
        stage.setTitle(view.getStageTitle());

        // Adds the new scene to the Stage
        stage.setScene(newScene);

        // Sets show on view enabled or disabled depending on specific value
        boolean alwaysOnTop = Main.settingsRepository.obtainShowOnTopValue();

        logger.log(Level.TRACE, "Using alwaysOnTop value {} for the view.", alwaysOnTop);
        stage.setAlwaysOnTop(alwaysOnTop);

        // makes stage draggable by mouse interaction
        StagePositioner.addDragFunctionalityToStage(stage, newScene);

        logger.log(Level.TRACE, "Showing {} view", view.name());
        // display the stage on the screen
        stage.show();

    }

    /**
     * Opens the desired view and closes the view from where this method was
     * called
     *
     * @param clickEvent the event that triggered this method
     * @param nextView the view will be showed
     * @throws IOException if an exception occurs when opening the next view
     */
    private static void navigateToView(Event clickEvent, ViewEnum nextView) throws IOException {
        Stage previousStage
                = (Stage) ((Button) clickEvent.getSource())
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

    /**
     * Minimizes the stage from where this method was called
     *
     * @param event the event that triggered this method call
     */
    @FXML
    public default void minimizeStage(Event event) {
        StagePositioner.getStageFromEvent(event).setIconified(true);
        logger.log(Level.TRACE, "Minimizing view");
    }

    /**
     * Close the stage from where this method was called and finalizes app
     * execution
     *
     * @param event the event that triggered this method call
     */
    @FXML
    public default void closeStage(Event event) {
        StagePositioner.getStageFromEvent(event).close();
        logger.log(Level.TRACE, "Closing view");
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
        navigateToView(event, ViewEnum.REPLAY);
    }

    @FXML
    public default void navigateToReplayStartView(Event event) throws IOException {
        navigateToView(event, ViewEnum.REPLAY_START);
    }

    @FXML
    public default void navigateToRecordView(Event event) throws IOException {
        navigateToView(event, ViewEnum.RECORDING_START);
    }

    @FXML
    public default void enableCoordinatesMode(Event event) {
        logger.log(Level.ALL, "Unimplemented functionality");
    }
    
    public static Alert createCustomAlert(
            Alert.AlertType alertType, String alertMessage, String alertHeader, Stage ownerStage, ButtonType buttonType
    ) {
                var alert = new Alert(alertType, alertMessage, buttonType);
                alert.setHeaderText(alertHeader);
                alert.initOwner(ownerStage);
                ViewController.styleAlert(alert);
                return alert;
    }
    
    /**
     * Applies app styles to given alert and then return it
     * @param alert the alert to be styled 
     * @return the styled alert
     */
    private static Alert  styleAlert(Alert alert) {
        DialogPane alertPane = alert.getDialogPane();
        alertPane.setStyle(
                "-fx-background-color: #e0e0e0;" + "-fx-border-color: #03a9f4;"
                + "-fx-border-width: 4px;" + "-border-radius: 8px;" + "-fx-background-radius: 12px;"
        );
        Scene alertScene = alertPane.getScene();
        Stage alertStage = (Stage) alertScene.getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        StagePositioner.addDragFunctionalityToStage(alertStage, alertScene);
        return alert;
    }

}
