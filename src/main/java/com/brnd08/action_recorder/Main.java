package com.brnd08.action_recorder;

import com.brnd08.action_recorder.utils.StagePosition;
import com.brnd08.action_recorder.utils.StagePositioner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private StagePositioner stagePositioner;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage principalStage) throws IOException {
        if (stagePositioner == null){
            stagePositioner = new StagePositioner(principalStage);
        }

        // Load Fxml view
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("principal-view.fxml"));
        Parent principalRoot = fxmlLoader.load();
        // Add the fxml view to a new scene
        Scene principalScene = new Scene(principalRoot);

        // Needed configuration to make the application background transparent
        principalStage.initStyle(StageStyle.TRANSPARENT);
        principalScene.setFill(Color.TRANSPARENT);

        // Adds the scene content to the application's window
        principalStage.setScene(principalScene);
        principalStage.setTitle("Action Recorder || By brdn");
        principalStage.setResizable(false);
        // Adds System bar application icon
        principalStage.getIcons().add(0, new Image(Objects.requireNonNull(getClass().getResourceAsStream("actionRecorderIcon.gif"))));

        // makes stage draggable by mouse interaction
        stagePositioner.addStageDragFucntionallity(principalScene);

        // display the stage on the screen
        principalStage.show();

        // sets the initial position of the stage
        stagePositioner.setStagePosition(StagePosition.CENTER);
    }
}