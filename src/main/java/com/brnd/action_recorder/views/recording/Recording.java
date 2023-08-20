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
package com.brnd.action_recorder.views.recording;

import com.github.kwhat.jnativehook.NativeInputEvent;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*
    This class is used to store the Recordings events as well as other useful information
 */
public class Recording implements Serializable {
    private static final Logger logger = LogManager.getLogger(Recording.class);
    public static final DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-DD");
    @Serial
    private static final long serialVersionUID = 4265898901141738551L;
    private int id;
    private final LinkedHashMap<Long, NativeInputEvent> inputEvents;
    private final LocalDate recordingDate;
    private String recordingTitle;
    /**
     * Recording Execution start time in nanoseconds.
     */
    private final long recordingStartTime;
    /**
     * Recording Execution stop time in nanoseconds.
     */
    private long recordingStopTime;
    /**
     * Recording Duration in seconds.
     */
    private float recordingDuration;

    /**
     * This method is intended to be called in Recording finalization
     * Sets the stopTime and calculates Recording duration.
     * @param pausedTime The time that the recording remained paused
     */
    public void closeRecording(float pausedTime) {
        this.recordingStopTime = System.nanoTime();
        float nanoSecondsInOneSecond = 1_000_000_000.0f;// 1 second = 1x10^9 nanoseconds.
        this.recordingDuration = ((recordingStopTime - recordingStartTime) - pausedTime) / nanoSecondsInOneSecond;
        
        logger.log(Level.INFO, "Recording stoped at {} s", this.recordingStopTime/1_000_000_000.0f);
    }

    /**
     * The string representation of the stored events
     * @return a String representation of the map containing the events.
     */
    public String interactionsString() {

        return
                inputEvents.entrySet()
                        .stream()
                        .map(entry -> {
                            long time = entry.getKey();
                            String event = entry.getValue().paramString();
                            return String.format("time: %d -> %s %n", time, event);
                        })
                        .collect(Collectors.joining());
    }

    public Recording() { // used to create new Recordings
        this.recordingStartTime = System.nanoTime();
        this.inputEvents = new LinkedHashMap<>();
        this.recordingDate = LocalDate.now();
        logger.log(Level.INFO, "Recording started at {} s", this.recordingStartTime/1_000_000_000.0f);
    }

    public Recording(// used to retrieve Recordings from database
            int id, LinkedHashMap<Long, NativeInputEvent> inputEvents, String recordingTitle, float recordingDuration, LocalDate recordingDate
    ) {
        this.id = id;
        this.inputEvents = inputEvents;
        this.recordingTitle = recordingTitle;
        this.recordingDuration = recordingDuration;
        this.recordingDate = recordingDate;
        this.recordingStartTime = 0;
    }
    

    public int getId() {
        return id;
    }

    public LinkedHashMap<Long, NativeInputEvent> getInputEvents() {
        return inputEvents;
    }

    public LocalDate getRecordingDate() {
        return recordingDate;
    }

    public long getRecordingStartTime() {
        return recordingStartTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordingTitle() {
        return recordingTitle;
    }

    public long getRecordingStopTime() {
        return recordingStopTime;
    }

    public void setRecordingStopTime(long recordingStopTime) {
        this.recordingStopTime = recordingStopTime;
    }

    public float getRecordingDuration() {
        return recordingDuration;
    }

    public void setRecordingDuration(float recordingDuration) {
        this.recordingDuration = recordingDuration;
    }

    public void setRecordingTitle(String recordingTitle) {
        this.recordingTitle = recordingTitle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recording{");
        sb.append("id=").append(id);
        sb.append(", inputEvents=").append(interactionsString());
        sb.append(", recordingDate=").append(recordingDate);
        sb.append(", recordingTitle=").append(recordingTitle);
        sb.append(", recordingStartTime=").append(recordingStartTime);
        sb.append(", recordingStopTime=").append(recordingStopTime);
        sb.append(", recordingDuration=").append(recordingDuration);
        sb.append('}');
        return sb.toString();
    }
    
}