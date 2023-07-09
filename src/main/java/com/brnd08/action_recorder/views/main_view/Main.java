package com.brnd08.action_recorder.views.main_view;

import com.brnd08.action_recorder.views.utils.StageLocation;
import com.brnd08.action_recorder.views.utils.StagePositioner;
import com.brnd08.action_recorder.views.utils.ViewController;
import com.brnd08.action_recorder.views.utils.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        // shows the main view
        ViewController.openView(stage, ViewEnum.MAIN);

        // sets the initial position of the stage
        StagePositioner.setStageLocation( stage , StageLocation.CENTER);
    }
}