package com.brnd.action_recorder.views.recording.recording_start_view;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

/**
 * This class implements methods to catch Mouse motion event such as mouse
 * movements and drags
 */
public class MouseMotionListener implements NativeMouseMotionListener {
    private final NativeRecorder recorder;

    public MouseMotionListener(NativeRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Invoked when the mouse has been moved.
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }

    /**
     * Invoked when the mouse has been moved while a button is depressed.
     *
     * @param nativeEvent the native mouse event
     * @since 1.1
     */
    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        // Creates a mouse-moved event from the drag event to ignore the drag but preserve the movement of the mouse
        var mouseMoveEvent = new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_MOVED, nativeEvent.getModifiers(), nativeEvent.getX(),
                nativeEvent.getY(), nativeEvent.getClickCount()
        );

        // Only saves the movement of the drag event
        this.recorder.saveEvent(mouseMoveEvent);
    }
}
