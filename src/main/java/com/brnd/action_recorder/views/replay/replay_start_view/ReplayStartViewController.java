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
package com.brnd.action_recorder.views.replay.replay_start_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Replay View
 */
public class ReplayStartViewController implements ViewController, Initializable {
    private static final Logger logger = LogManager.getLogger(ReplayStartViewController.class);
    private boolean isReplaying = false;
    private FXTrayIcon trayIcon;
    private ActionsPlayer actionsPlayer;
    private final ToggleGroup replayModeToggleGroup = new ToggleGroup();
    private boolean mouseEvents = false;
    private boolean keyboardEvents = false;
    private boolean scrollEvents = false;
    private boolean clickEvents = false;

    @FXML
    private ToggleButton systemTrayToggleButton, windowToggleButton;
    @FXML
    private CheckBox minimizeAtReplayingCheckBox;
    @FXML
    private Text durationText, inputsText;
    @FXML
    private CheckBox mouseCheck, keyboardCheck, scrollCheck, clickCheck;
    @FXML
    private Button returnButton, closeBttn, minimizeBttn;
    @FXML
    private Button startReplayButton;
    @FXML
    private AnchorPane mainPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureReplayModeToggleGroup();
        Platform.runLater(() -> { // to prevent execution until initialization is complete. Otherwise node.getScene returns null
            Recording recording = getRecording();
            try {
                this.actionsPlayer = new ActionsPlayer(recording.getInputEvents(), recording.getRecordingDuration());
            } catch (Exception e) {
                logger.log(Level.ERROR, "Could not create ActionsPlayer. Message: {}", e.getMessage());
            }
            this.displayReplayInfo(recording);
        });
    }

    /**
     * Show the information of the replay in the screen controls and
     */
    public void displayReplayInfo(Recording recording) {
        this.mouseCheck.setSelected(recording.isMouseEvents());
        this.keyboardCheck.setSelected(recording.isKeyboardEvents());
        this.scrollCheck.setSelected(recording.isScrollEvents());
        this.clickCheck.setSelected(recording.isClickEvents());
        this.inputsText.setText(String.valueOf(recording.getInputEvents().size()));
        this.durationText.setText(String.format("%.2f s", recording.getRecordingDuration()));
    }
//

    /**
     * Gets the last saved input events from the stage set in the replay selection
     * view
     *
     * @return a Recording containing the input events
     */
    private Recording getRecording() {
        Recording recoveredRecordingActions = null;
        try {
            recoveredRecordingActions = (Recording) closeBttn.getScene().getWindow().getUserData();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Could not recover recording from stage. Exception msg: {}", e.getMessage());
        }
        return recoveredRecordingActions;
    }
//

    /**
     * This method configures the Replay mode toggle group by adding to it the
     * systemTray and window toggle buttons and configuring them to force to
     * have at least one toggle button selected and to switch the minimize when
     * replaying button
     */
    private void configureReplayModeToggleGroup() {
        this.replayModeToggleGroup.selectedToggleProperty()
                .addListener(
                        (selectedToggleProperty, oldSelectedToggleButton, newSelectedToggleButton) -> {
                            /* no selected ToggleButtons  and previously selected toggleButton*/
                            if (newSelectedToggleButton == null && oldSelectedToggleButton != null) {
                                oldSelectedToggleButton.setSelected(true);
                            }
                        }
                );
        /* set minimizeReplaying checkbox enabled if system tray toggle button is deselected, otherwise disables it */
        this.systemTrayToggleButton.selectedProperty().addListener(
                (observable, oldValue, newValue) -> minimizeAtReplayingCheckBox.setDisable(newValue)
        );
        this.windowToggleButton.setToggleGroup(this.replayModeToggleGroup);
        this.systemTrayToggleButton.setToggleGroup(this.replayModeToggleGroup);
    }
//

    /**
     * Disables all the inputs from the view, intended to be used when a
     * recording has started or stopped
     */
    private void disableAllInputs(boolean disable) {
        this.minimizeAtReplayingCheckBox.setDisable(disable);
        this.systemTrayToggleButton.setDisable(disable);
        this.windowToggleButton.setDisable(disable);
        this.returnButton.setDisable(disable);
    }

    /**
     * This method changes GUI behavior when creating a Recording based on the
     * useSystemTray boolean
     *
     * @param currentStage the current stage
     */
    private void switchToReplayingMode(Stage currentStage) {
        //if the app is not recording at the moment do nothing
        currentStage.setTitle("Reproduciendo Grabación - Grabadora de Acciones");
        this.disableAllInputs(true);
        if (this.systemTrayToggleButton.isSelected()) {
            this.configureSystemTrayIcon(currentStage);
            currentStage.close();
        } else if (this.minimizeAtReplayingCheckBox.isSelected()) {
            this.minimizeBttn.fire();
        }
    }


    /**
     * Modifies start recording button functionality based on the isRecoding
     * boolean If its true modify button to stop the recorder, if not modify it
     * to start a recording
     */
    private void switchStartReplayingButtonFunctionality(Stage stage) {
        if (this.isReplaying) {
            startReplayButton.setText("Finalizar");
            startReplayButton.setOnAction(this::stopRecording);
            switchToReplayingMode(stage);
            mainPane.requestFocus();// removes focus from start button to prevent firing it while typing with the keyboard during recordings
        } else {
            startReplayButton.setText("Comenzar");
            startReplayButton.setOnAction(this::startReplaying);
            stage.setTitle("Reproducir Grabación - Grabadora de Acciones");
            disableAllInputs(false);
            if (this.systemTrayToggleButton.isSelected()) {
                removeTrayIconIfExits();
                stage.setIconified(false);
            } else if (this.minimizeAtReplayingCheckBox.isSelected()) {
                stage.setIconified(false);
            }
        }
    }


    /**
     * Pauses the current recording
     */
    @FXML
    private void pauseRecording() {
        logger.log(Level.ALL, "Pausing recording");
        this.actionsPlayer.pauseReplay();
    }

    /**
     * Resume the current recording intended to be used after pausing a recording
     */
    @FXML
    private void resumeRecording() {
        logger.log(Level.ALL, "Resuming recording");
        this.actionsPlayer.resumeRecording();
    }

    /**
     * Creates and configure the app system tray icon during the recording time
     *
     * @param currentStage The stage that will be related with the tray icon
     */
    private void configureSystemTrayIcon(Stage currentStage) {
        logger.log(Level.INFO, "configuring System Tray Icon");

        this.trayIcon = new FXTrayIcon(currentStage, ViewEnum.getAppIcon());
        this.trayIcon.setTrayIconTooltip("Recording in progress - Action Recorder");

        var stopRecording = new MenuItem("Detener Grabación");
        var pauseRecording = new MenuItem("Pausar Grabación");
        var resumeRecording = new MenuItem("Reanudar Grabación");
        var exitProgram = new MenuItem("Salir del Programa");
        resumeRecording.setDisable(true);

        stopRecording.setOnAction(e
                -> this.closeBttn.fire()
        );
        pauseRecording.setOnAction(e -> {
            EventQueue.invokeLater(() -> { // disables swing pause MenuItem and enables resume one
                trayIcon.getMenuItem(2).setEnabled(true);
                trayIcon.getMenuItem(1).setEnabled(false);
            });
            this.pauseRecording();
        });

        resumeRecording.setOnAction(e -> {
            this.resumeRecording();
            EventQueue.invokeLater(() -> { // disables swing resume MenuItem and enables pause one
                trayIcon.getMenuItem(2).setEnabled(false);
                trayIcon.getMenuItem(1).setEnabled(true);
            });
        });

        exitProgram.setOnAction(event -> {
            this.stopRecording(event);
            this.trayIcon.hide();
            currentStage.close();
        });

        this.trayIcon.addMenuItem(stopRecording);
        this.trayIcon.addMenuItem(pauseRecording);
        this.trayIcon.addMenuItem(resumeRecording);
        this.trayIcon.addMenuItem(exitProgram);
        this.trayIcon.show();
    }

    /**
     * Starts replaying the recording events
     *
     * @param event the event that triggered this method call
     */
    @FXML
    public void startReplaying(Event event) {

        logger.log(Level.INFO, "Starting Recording replay");

        Platform.runLater(() -> {
                    var currentStage = StagePositioner.getStageFromEvent(event);
                    this.isReplaying = true;
                    this.switchStartReplayingButtonFunctionality(currentStage);
                    this.actionsPlayer.startReplay(() -> { // runnable to execute at replay finalization
                        this.isReplaying = false;
                        this.switchStartReplayingButtonFunctionality(currentStage);
                        ViewController.createCustomAlert(
                                Alert.AlertType.CONFIRMATION,
                                "Todos los eventos de la grabación fueron executados con exito",
                                "La reproducción finalizó", currentStage, ButtonType.OK
                        ).show();

                    });
                }
        );
    }


    /**
     * This method executes needed actions to stop the current recording
     *
     * @param event the InputEvent that triggered this method call
     */
    @FXML
    public void stopRecording(Event event) {
        logger.log(Level.INFO, "Stoping current recording");
        var currentStage = StagePositioner.getStageFromEvent(event);
        var success = this.actionsPlayer.stopReplaying();
        String alertMessage;
        String alertHeader;
        Alert.AlertType alertType;
        if (success) {
            alertMessage = "Las reproducción de eventos de la grabación se detuvo correctamente";
            alertHeader = "Reproducción detenida";
            alertType = Alert.AlertType.CONFIRMATION;
            this.isReplaying = false;
            this.switchStartReplayingButtonFunctionality(currentStage);
        } else {
            alertMessage = "Algo fué mal al intentar detener la reproducción \n AVISO: los eventos de la" +
                    "grabacion se seguirán ejecutando";
            alertHeader = "Falló al detener reproducción";
            alertType = Alert.AlertType.WARNING;
        }

        ViewController.createCustomAlert(
                alertType, alertMessage,
                alertHeader, currentStage, ButtonType.OK
        ).show();
    }

    /**
     * Removes app tray icon created at recording start
     */
    private void removeTrayIconIfExits() {
        if (this.trayIcon != null) {
            this.trayIcon.hide(); // removes the tray icon if its being show
            this.trayIcon = null;
        }
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

    @Override
    public void navigateToReplaySelectionView(Event event) throws IOException {
        ViewController.super.navigateToReplaySelectionView(event);
    }
}
