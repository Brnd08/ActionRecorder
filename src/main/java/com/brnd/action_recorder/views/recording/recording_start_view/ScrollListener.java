package com.brnd.action_recorder.views.recording.recording_start_view;

import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

/**
 * This class implements methods to catch Mouse wheel events such as
 * scrolling movements
 */
public class ScrollListener implements NativeMouseWheelListener {
    private final NativeRecorder recorder;

    /**
     * Creates a new ScrollListener instance which will use the given NativeRecorder object to store caught events
     * @param recorder NativeRecorder type object to be used
     */
    public ScrollListener(NativeRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param nativeEvent the native mouse wheel event.
     */
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }
}
