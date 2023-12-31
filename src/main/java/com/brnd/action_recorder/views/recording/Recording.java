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

import com.brnd.action_recorder.views.replay.replay_start_view.actions.ReplayableAction;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to store the Recordings events as well as other useful information
 */
public class Recording implements Serializable {
    private static final Logger logger = LogManager.getLogger(Recording.class);
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mma"; // 2023-08-20 10:20pm PST
    @Serial
    private static final long serialVersionUID = 4265898901141738551L;
    private int id;
    private final Queue<ReplayableAction> inputEvents;
    private final LocalDateTime recordingDateTime;
    private String recordingTitle;
    private boolean mouseEvents = false;
    private boolean keyboardEvents = false;
    private boolean scrollEvents = false;
    private boolean clickEvents = false;

    public boolean isMouseEvents() {
        return mouseEvents;
    }

    public void setMouseEvents(boolean mouseEvents) {
        this.mouseEvents = mouseEvents;
    }

    public boolean isKeyboardEvents() {
        return keyboardEvents;
    }

    public void setKeyboardEvents(boolean keyboardEvents) {
        this.keyboardEvents = keyboardEvents;
    }

    public boolean isScrollEvents() {
        return scrollEvents;
    }

    public void setScrollEvents(boolean scrollEvents) {
        this.scrollEvents = scrollEvents;
    }

    public boolean isClickEvents() {
        return clickEvents;
    }

    public void setClickEvents(boolean clickEvents) {
        this.clickEvents = clickEvents;
    }

    private String recordingDescription;
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
     *
     * @param pausedTime The time that the recording remained paused
     */
    public void closeRecording(float pausedTime) {
        this.recordingStopTime = System.nanoTime();
        float nanoSecondsInOneSecond = 1_000_000_000.0f;// 1 second = 1x10^9 nanoseconds.
        this.recordingDuration = ((recordingStopTime - recordingStartTime) - pausedTime) / nanoSecondsInOneSecond;

        logger.log(Level.INFO, "Recording stoped at {} s", this.recordingStopTime / 1_000_000_000.0f);
    }

    /**
     * The string representation of the stored events
     *
     * @return a String representation of the map containing the events.
     */
    public String interactionsString() {
        return
                inputEvents.stream().map(
                            event -> String.format("time: %d -> %s %n", event.getRelativeExecutionTime(), event)
                        ).collect(Collectors.joining());
    }

    public Recording() { // used to create new Recordings
        this.recordingStartTime = System.nanoTime();
        this.inputEvents = new LinkedList<>();
        this.recordingDateTime = LocalDateTime.now();
        logger.log(Level.INFO, "Recording started at {} s", this.recordingStartTime / 1_000_000_000.0f);
    }

    public Recording(// used to retrieve Recordings from the database
                     int id, Queue<ReplayableAction> inputEvents, String recordingTitle, String recordingDescription,
                     float recordingDuration, LocalDateTime recordingDateTime
    ) {
        this.id = id;
        this.inputEvents = inputEvents;
        this.recordingTitle = recordingTitle;
        this.recordingDescription = recordingDescription;
        this.recordingDuration = recordingDuration;
        this.recordingDateTime = recordingDateTime;
        this.recordingStartTime = 0;
    }


    public int getId() {
        return id;
    }

    public Queue<ReplayableAction> getInputEvents() {
        return inputEvents;
    }

    public void setInputEvents(Queue<ReplayableAction> inputEvents) {
        this.inputEvents.addAll(inputEvents);
    }


    public LocalDateTime getRecordingDateTime() {
        return recordingDateTime;
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

    public String getRecordingDescription() {
        return recordingDescription;
    }

    public void setRecordingDescription(String recordingDescription) {
        this.recordingDescription = recordingDescription;
    }

    public String getRecordingDate() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern(Recording.DATE_TIME_FORMAT);
        return this.recordingDateTime.format(dateTimeFormatter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recording{");
        sb.append("id=").append(id);
        sb.append(", recordingTitle=").append(recordingTitle);
        sb.append(", recordingDescription=").append(recordingDescription);
        sb.append(", recordingDate=").append(recordingDateTime);
        sb.append(", recordingStartTime=").append(recordingStartTime);
        sb.append(", recordingStopTime=").append(recordingStopTime);
        sb.append(", recordingDuration=").append(recordingDuration);
        sb.append(", inputEvents=").append(interactionsString());
        sb.append('}');
        return sb.toString();
    }

    /**
     * A String representation of the object excluding the recording input Events
     *
     * @return The String representation of this object
     */
    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recording{");
        sb.append("id=").append(id);
        sb.append(", recordingTitle=").append(recordingTitle);
        sb.append(", recordingDescription=").append(recordingDescription);
        sb.append(", recordingDate=").append(recordingDateTime);
        sb.append(", recordingStartTime=").append(recordingStartTime);
        sb.append(", recordingStopTime=").append(recordingStopTime);
        sb.append(", recordingDuration=").append(recordingDuration);
        sb.append(", inputEventsCount=").append(inputEvents.size());
        sb.append('}');
        return sb.toString();

    }

}
