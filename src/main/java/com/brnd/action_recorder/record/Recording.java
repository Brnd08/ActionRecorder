package com.brnd.action_recorder.record;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Long, NativeInputEvent> getInteractions() {
        return interactions;
    }

    public String interactionsString() {

        return
                interactions.entrySet()
                        .stream()
                        .map(entry -> {
                            long time = entry.getKey();
                            String event = entry.getValue().paramString();
                            return String.format("time: %d -> %s %n", time, event);
                        })
                        .collect(Collectors.joining());
    }

    public long getRecordingStartTime() {
        return recordingStartTime;
    }

}