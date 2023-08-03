package com.brnd.action_recorder.record;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Recording implements Serializable {
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
     */
    public void closeRecording() {
        this.recordingStopTime = System.nanoTime();
        float nanoSecondsInOneSecond = 1_000_000_000.0f;// 1 second = 1x10^9 nanoseconds.
        this.recordingDuration = (recordingStopTime - recordingStartTime) / nanoSecondsInOneSecond;
    }

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


    public Recording() {
        this.recordingStartTime = System.nanoTime();
        this.inputEvents = new LinkedHashMap<>();
        this.recordingDate = LocalDate.now();
    }

    public Recording(
            int id, LinkedHashMap<Long, NativeInputEvent> inputEvents, String recordingTitle, float recordingDuration, LocalDate recordingDate
    ) {
        this.id = id;
        this.inputEvents = inputEvents;
        this.recordingTitle = recordingTitle;
        this.recordingDuration = recordingDuration;
        this.recordingDate = recordingDate;
        recordingStartTime = 0;
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