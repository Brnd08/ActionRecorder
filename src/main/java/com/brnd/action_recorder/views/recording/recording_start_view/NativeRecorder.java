package com.brnd.action_recorder.views.recording.recording_start_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;

/**
 * Class type of NativeInput Recorders
 */
public interface NativeRecorder {
    /**
     * Starts a new recording using the given RecordingConfiguration
     *
     * @param recordingConfiguration The recording configuration specifying the events to be caught
     * @throws NativeHookException If a problem occurs while registering needed listeners
     */
    void startRecording(RecordingConfiguration recordingConfiguration) throws NativeHookException;

    /**
     * Stops the current recording
     */
    void stopRecording();

    /**
     * Pauses the current recording
     */
    public void pauseRecording();

    /**
     * Resumes the paused recording if any
     */
    void resumeRecording();


    /**
     * Returns the last stopped Recording if any
     *
     * @return Recording containing the caught events
     */
    Recording getLastRecording();

    /**
     * Saves the given NativeInputEvent to the current recording
     *
     * @param inputEvent The caught NativeInputEvent
     */
    void saveEvent(NativeInputEvent inputEvent);
}
