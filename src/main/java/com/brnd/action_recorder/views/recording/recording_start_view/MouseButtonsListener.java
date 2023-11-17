package com.brnd.action_recorder.views.recording.recording_start_view;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

/**
 * This class implements methods to catch Mouse click events such as button
 * mouse presses, releases and clicks
 */
public class MouseButtonsListener implements NativeMouseListener {
    private final NativeRecorder recorder;
    public MouseButtonsListener (NativeRecorder recorder){
        this.recorder = recorder;
    }

    /**
     * Invoked when a mouse button has been clicked (pressed and released)
     * without being moved.
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
        // Don't save clicked event since they are formed by presses and releases
    }

    /**
     * Invoked when a mouse button has been pressed
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }

    /**
     * Invoked when a mouse button has been released
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }
}
