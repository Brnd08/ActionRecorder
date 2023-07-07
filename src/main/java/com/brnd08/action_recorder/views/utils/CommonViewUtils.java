package com.brnd08.action_recorder.views.utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

import static com.brnd08.action_recorder.views.utils.StagePositioner.addDragFunctionalityToStage;

public abstract class CommonViewUtils {


    private CommonViewUtils(){
        // to prevent class instantiation
        throw new UnsupportedOperationException("Utility class can not be instantiated");
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
        addDragFunctionalityToStage(stage, newScene);

        // display the stage on the screen
        stage.show();
        
    }

    public static void navigateToView(Event clickEvent, ViewEnum nextView) throws IOException, NullPointerException {

        Stage previousStage =
                (Stage) ((Button) clickEvent.getSource())
                        .getScene()
                        .getWindow();

        previousStage.close();

        openView(new Stage(), nextView);

    }
}
