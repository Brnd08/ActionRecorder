package com.brnd.action_recorder.views.main_view;

import com.brnd.action_recorder.views.utils.ViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainViewController implements ViewController {
    @FXML
    Button playBttn;
    @FXML
    Button recordBttn;
    @FXML
    Button settingsBttn;
    @FXML
    Button closeBttn;
    @FXML
    Button minimizeBttn;

    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }

    @Override
    public void closeStage(Event event) {
        ViewController.super.closeStage(event);
    }

    @Override
    public void navigateToSettingsView(Event event) throws IOException {
        ViewController.super.navigateToSettingsView(event);
    }

    @Override
    public void navigateToReplayView(Event event) throws IOException {
        ViewController.super.navigateToReplayView(event);
    }

    @Override
    public void navigateToRecordView(Event event) throws IOException {
        ViewController.super.navigateToRecordView(event);
    }

    @Override
    public void enableCoordinatesMode(Event event) {
        ViewController.super.enableCoordinatesMode(event);
    }
}