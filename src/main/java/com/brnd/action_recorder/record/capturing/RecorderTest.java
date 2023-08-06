package com.brnd.action_recorder.record.capturing;

import com.brnd.action_recorder.views.recording_start_view.RecordingConfiguration;
import static com.brnd.action_recorder.data.Database.initializeDatabase;
import com.brnd.action_recorder.record.Recording;
import com.brnd.action_recorder.record.RecordingsRepository;
import com.github.kwhat.jnativehook.NativeHookException;
import org.apache.logging.log4j.Level;

import java.util.Timer;
import java.util.TimerTask;

import static com.brnd.action_recorder.record.capturing.InteractionRecorder.logger;
import java.sql.SQLException;

/**
 * This class serves a quick way to check verify recording creation functionality
 */
public class RecorderTest {

    public static void main(String[] args) throws NativeHookException, SQLException {
        initializeDatabase(); // Initialize database

        InteractionRecorder interactionRecorder = new InteractionRecorder();
        RecordingsRepository recordingsRepository = new RecordingsRepository();

        RecordingConfiguration recordConfiguration = new RecordingConfiguration(
                true
                , false
                , true
                , true
                , "Recording Test"
        );

        interactionRecorder.startRecording(recordConfiguration);
        
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                interactionRecorder.stopRecording();
                
                Recording recordedRecording = interactionRecorder.getlastRecording();
                recordingsRepository.insertRecording(recordedRecording);
                
                logger.log(Level.TRACE, "Recorded actions: {}", recordedRecording.interactionsString());
                
                Recording retrievedRecording = recordingsRepository.getRecordingById(recordedRecording.getId());
                logger.log(Level.TRACE, "Retrieved Recording from database: {}"
                        , retrievedRecording
                );
                
                System.exit(0);
            }
        }, (long) 5 * 1000);

    }
}
