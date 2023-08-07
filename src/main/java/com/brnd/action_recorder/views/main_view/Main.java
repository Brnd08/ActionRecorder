package com.brnd.action_recorder.views.main_view;

import com.brnd.action_recorder.views.settings_view.SettingsRepository;
import com.brnd.action_recorder.views.utils.StageLocation;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * This class executes needed app actions at app startup
 */
public class Main extends Application {
    
    public static final Logger logger = LogManager.getLogger(Main.class);
    public static final SettingsRepository settingsRepository = new SettingsRepository();

    public static void main(String[] args) {
        launch();
    }

    /**
     * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running. * NOTE: This method is called on the JavaFX Application Thread.
     * @param stage – the primary stage for this application, onto which the application scene can be set. Applications may create other stages, 
     * if needed, but they will not be primary stages.
     * @throws IOException if an exception occurs when opening main view
     */
    @Override
    public void start(Stage stage) throws IOException {
        logger.log(Level.TRACE, "App Initialization.");
        
        
        // shows the main view
        ViewController.openView(stage, ViewEnum.MAIN);


        // sets the initial position of the stage
        StageLocation initialStageLocation = settingsRepository.obtainInitialStageLocation();
        logger.log(Level.TRACE, "Setting view location to {}.", initialStageLocation);
        StagePositioner.setStageLocation( stage , initialStageLocation);
    }
}