/*
 * Copyright (C) 2023 Brandon Velazquez & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.brnd.action_recorder.record.capturing;

import com.brnd.action_recorder.views.recording_start_view.InteractionRecorder;
import com.brnd.action_recorder.views.recording_start_view.Recording;
import com.brnd.action_recorder.views.recording_start_view.RecordingsRepository;
import com.brnd.action_recorder.views.recording_start_view.RecordingConfiguration;
import com.github.kwhat.jnativehook.NativeHookException;
import org.apache.logging.log4j.Level;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import static com.brnd.action_recorder.data.Database.initializeDatabase;
import static com.brnd.action_recorder.views.recording_start_view.InteractionRecorder.logger;

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
