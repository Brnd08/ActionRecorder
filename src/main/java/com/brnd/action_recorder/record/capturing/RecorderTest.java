package com.brnd.action_recorder.record.capturing;

import static com.brnd.action_recorder.data.Database.initializeDatabase;
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

        RecordConfiguration recordConfiguration = new RecordConfiguration(
                true
                , true
                , true
                , true
        );

        interactionRecorder.setRecordConfiguration(recordConfiguration);
        interactionRecorder.startRecording("Recording Test");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                interactionRecorder.stopRecording();
                logger.log(Level.TRACE, "Recorded actions: \n{}", interactionRecorder.getlastRecording().interactionsString());
                System.exit(0);
            }
        }, (long) 5 * 1000);

    }
}
