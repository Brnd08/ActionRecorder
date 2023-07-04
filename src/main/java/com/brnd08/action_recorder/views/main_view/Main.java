package com.brnd08.action_recorder.views.main_view;

import com.brnd08.action_recorder.utils.StagePosition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

import static com.brnd08.action_recorder.utils.StagePositioner.addStageDragFucntionallity;
import static com.brnd08.action_recorder.utils.StagePositioner.setStagePosition;

public class Main extends Application {


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) throws IOException {

        // Load Fxml view
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainView.fxml"));
        Parent principalRoot = fxmlLoader.load();
        // Add the fxml view to a new scene
        Scene principalScene = new Scene(principalRoot);

        // Needed configuration to make the application background transparent
        mainStage.initStyle(StageStyle.TRANSPARENT);
        principalScene.setFill(Color.TRANSPARENT);

        // Adds the scene content to the application's window
        mainStage.setScene(principalScene);
        mainStage.setTitle("Action Recorder || By brdn");
        mainStage.setResizable(false);
        // Adds taskbar icon
        mainStage.getIcons().add(0,
                new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("appIcon.gif")
                        )
                )
        );

        // makes stage draggable by mouse interaction
        addStageDragFucntionallity(mainStage, principalScene);

        // display the stage on the screen
        mainStage.show();

        // sets the initial position of the stage
        setStagePosition( mainStage , StagePosition.CENTER);
    }
}