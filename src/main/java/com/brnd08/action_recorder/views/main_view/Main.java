package com.brnd08.action_recorder.views.main_view;

import com.brnd08.action_recorder.views.utils.StageLocation;
import com.brnd08.action_recorder.views.utils.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import static com.brnd08.action_recorder.views.utils.CommonViewUtils.openView;
import static com.brnd08.action_recorder.views.utils.StagePositioner.setStageLocation;


public class Main extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        // shows the main view
        openView(stage, ViewEnum.MAIN);

        // sets the initial position of the stage
        setStageLocation( stage , StageLocation.CENTER);
    }
}