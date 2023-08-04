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
        );

        interactionRecorder.setRecordConfiguration(recordConfiguration);
        interactionRecorder.startRecording("Recording Test");
        
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                interactionRecorder.stopRecording();
                
                Recording recordedRecording = interactionRecorder.getlastRecording();
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
