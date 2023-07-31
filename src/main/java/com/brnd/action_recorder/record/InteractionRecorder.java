package com.brnd.action_recorder.record;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractionRecorder implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {
    public static final Logger logger = LogManager.getLogger(InteractionRecorder.class);
    private Recording recording;
    private RecordConfiguration recordConfiguration;

    private void loadRecordConfiguration() {

        if (recordConfiguration.isRecordingKeyboardInteractions())
            GlobalScreen.addNativeKeyListener(this);
        else
            GlobalScreen.removeNativeKeyListener(this);

        if (recordConfiguration.isRecordingMouseWheelInteractions())
            GlobalScreen.addNativeMouseWheelListener(this);
        else
            GlobalScreen.removeNativeMouseWheelListener(this);

        if (recordConfiguration.isRecordingMouseMotionInteractions())
            GlobalScreen.addNativeMouseMotionListener(this);
        else
            GlobalScreen.removeNativeMouseMotionListener(this);

        if (recordConfiguration.isRecordingMouseClickInteractions())
            GlobalScreen.addNativeMouseListener(this);
        else
            GlobalScreen.removeNativeMouseListener(this);

    }

    public InteractionRecorder(Recording recording, RecordConfiguration recordConfiguration) throws NativeHookException {
        this.recording = recording;
        this.recordConfiguration = recordConfiguration;

        GlobalScreen.registerNativeHook();
    }
    public void startRecording() throws NativeHookException {
        this.recording = new Recording();
        GlobalScreen.registerNativeHook();
        loadRecordConfiguration();
    }

    public InteractionRecorder() throws NativeHookException {
        /* Do nothing */
    }

    public RecordConfiguration getRecordConfiguration() {
        return recordConfiguration;
    }

    public void setRecordConfiguration(RecordConfiguration recordConfiguration) {
        this.recordConfiguration = recordConfiguration;
    }


    public void stopRecording() {
        this.recordConfiguration = new RecordConfiguration(
                false
                , false
                , false
                , false
        );
        loadRecordConfiguration();
    }

    public Recording getRecording() {
        return recording;
    }

    /*      KEYBOARD INTERACTIONS       */

    /**
     * Invoked when a key has been typed.
     *
     * @param nativeEvent the native key event.
     * @since 1.1
     */
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        System.out.println(nativeEvent.paramString());
        /* Do nothing */
    }

    /**
     * Invoked when a key has been pressed.
     *
     * @param nativeEvent the native key event.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (recordConfiguration.isRecordingKeyboardInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * Invoked when a key has been released.
     *
     * @param nativeEvent the native key event.
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        if (recordConfiguration.isRecordingKeyboardInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }


    /*      MOUSE-CLICK INTERACTIONS       */

    /**
     * Invoked when a mouse button has been clicked (pressed and released) without being moved.
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
        /* Do Nothing */
    }

    /**
     * Invoked when a mouse button has been pressed
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (recordConfiguration.isRecordingMouseClickInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * Invoked when a mouse button has been released
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if (recordConfiguration.isRecordingMouseClickInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /*      MOUSE-MOTION INTERACTIONS       */


    /**
     * Invoked when the mouse has been moved.
     *
     * @param nativeEvent the native mouse event.
     */
    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        if (recordConfiguration.isRecordingMouseMotionInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * Invoked when the mouse has been moved while a button is depressed.
     *
     * @param nativeEvent the native mouse event
     * @since 1.1
     */
    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        /* Do nothing */
    }


    /*      MOUSE-WHEEL INTERACTIONS       */

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param nativeEvent the native mouse wheel event.
     */
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        if (recordConfiguration.isRecordingMouseWheelInteractions()) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    private void addNativeEventToRecording(NativeInputEvent event) {
        try {
            if (this.recording == null) {
                logger.log(Level.ERROR, "No available recording object to store events");
                throw new NullPointerException();
            } else {
                this.recording.getInteractions().put(System.nanoTime(), event);
                logger.log(Level.INFO, "Recorded : {}", event.paramString());
            }

        } catch (NullPointerException nullPointerException) {
            logger.log(Level.FATAL, nullPointerException);
        }
    }
}
