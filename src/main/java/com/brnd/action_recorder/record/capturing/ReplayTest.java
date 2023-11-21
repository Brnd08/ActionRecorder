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

import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository;
import com.brnd.action_recorder.views.recording.recording_start_view.ReplayableNativeRecorder;
import com.brnd.action_recorder.views.recording.recording_start_view.RecordingConfiguration;
import com.brnd.action_recorder.views.replay.replay_start_view.ActionsPlayer;
import com.github.kwhat.jnativehook.NativeHookException;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import static com.brnd.action_recorder.views.recording.recording_start_view.ReplayableNativeRecorder.logger;

/**
 * This class serves a quick way to check verify recording creation functionality
 */
public class ReplayTest {

    public static void main(String[] args) throws SQLException, NativeHookException {
//        Database.deleteDatabase();// deletes previous databases
        Database.initializeDatabase(); // Initialize database

        ReplayableNativeRecorder interactionRecorder = new ReplayableNativeRecorder();
        RecordingsRepository recordingsRepository = new RecordingsRepository();

        RecordingConfiguration recordingConfiguration = new RecordingConfiguration(
               true
                , true
                , true
                , true
        );

        interactionRecorder.startRecording(recordingConfiguration);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                interactionRecorder.stopRecording();

                Recording recordedRecording = interactionRecorder.getLastRecording();
                recordedRecording.setRecordingTitle("First full recording test");
                recordedRecording.setRecordingDescription("This the first recording with all the events listeners recording input event I'm so exited I worked and investigate for a long time to see this happen");
                int recordingId = recordingsRepository.insertRecording(recordedRecording);

                logger.log(Level.TRACE, "Recorded actions: {}", recordedRecording.interactionsString());

                Recording retrievedRecording = recordingsRepository.getRecordingById(recordingId);
                logger.log(Level.TRACE, "Retrieved Recording from database: {}"
                        , retrievedRecording
                );

                ActionsPlayer recordingPlayer = null;
                try {
                    recordingPlayer = new ActionsPlayer(retrievedRecording.getInputEvents(), retrievedRecording.getRecordingDuration());
                    recordingPlayer.startReplay(()->logger.log(Level.ALL, "Reach replay end"));
                } catch (AWTException e) {
                    logger.log(Level.ALL, "Could not create the Robot instance. Error: {}", e.getMessage());
                }
            }
        }, (long) 10 * 1000);

    }
}
