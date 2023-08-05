package com.brnd.action_recorder.views.main_view;

import com.brnd.action_recorder.views.settings_view.SettingsRepository;
import com.brnd.action_recorder.views.utils.StageLocation;
import com.brnd.action_recorder.views.utils.StagePositioner;
import com.brnd.action_recorder.views.utils.ViewController;
import com.brnd.action_recorder.views.utils.ViewEnum;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Main extends Application {
    
    public static final Logger logger = LogManager.getLogger(Main.class);
    public static final SettingsRepository settingsRepository = new SettingsRepository();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        logger.log(Level.TRACE, "App Initialization.");
        
        
        // shows the main view
        ViewController.openView(stage, ViewEnum.MAIN);


        // sets the initial position of the stage
        StageLocation initialStageLocation = settingsRepository.obtainInitialStageLocation();
        logger.log(Level.TRACE, "Setting view location to {}.", initialStageLocation);
        StagePositioner.setStageLocation( stage , initialStageLocation);
    }
}