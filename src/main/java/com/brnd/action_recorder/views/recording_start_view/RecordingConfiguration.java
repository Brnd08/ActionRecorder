package com.brnd.action_recorder.views.recording_start_view;

public record RecordingConfiguration(
        boolean recordingKeyboardInteractions,
        boolean recordingMouseMotionInteractions,
        boolean recordingMouseClickInteractions,
        boolean recordingMouseWheelInteractions,
        String recordingTitle
        ) {}