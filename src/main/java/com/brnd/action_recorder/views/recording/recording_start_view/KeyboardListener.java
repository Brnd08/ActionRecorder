package com.brnd.action_recorder.views.recording.recording_start_view;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

/**
 * This class implements methods to catch Keyboard related events such as
 * key presses, key releases and typed keys
 */
public class KeyboardListener implements NativeKeyListener {
    private final NativeRecorder recorder;
    public KeyboardListener (NativeRecorder recorder){
        this.recorder = recorder;
    }

    /**
     * Invoked when a key has been typed.
     *
     * @param nativeEvent the native key event.
     * @since 1.1
     */
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        /* Do nothing */
    }

    /**
     * Invoked when a key has been pressed.
     *
     * @param nativeEvent the native key event.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }

    /**
     * Invoked when a key has been released.
     *
     * @param nativeEvent the native key event.
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        this.recorder.saveEvent(nativeEvent);
    }
}
