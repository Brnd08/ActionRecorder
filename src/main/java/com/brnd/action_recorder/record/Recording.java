package com.brnd.action_recorder.record;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Recording implements Serializable {
    private final int id;
    private final LinkedHashMap<Long, NativeInputEvent> interactions;
    private final long recordingStartTime;

    public Recording() {
        this.recordingStartTime = System.nanoTime();
        this.interactions = new LinkedHashMap<>();
        this.id = 1;
    }

    public int getId() {
        return id;
    }

    public LinkedHashMap<Long, NativeInputEvent> getInteractions() {
        return interactions;
    }

    public long getRecordingStartTime() {
        return recordingStartTime;
    }

}