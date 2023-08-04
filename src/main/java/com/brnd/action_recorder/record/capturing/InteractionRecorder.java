package com.brnd.action_recorder.record.capturing;

import com.brnd.action_recorder.views.recording_start_view.RecordingConfiguration;
import com.brnd.action_recorder.record.Recording;
import com.brnd.action_recorder.record.RecordingsRepository;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractionRecorder {
    public static final Logger logger = LogManager.getLogger(InteractionRecorder.class);
    private Recording recording;
    private RecordingConfiguration recordConfiguration;
    private final KeyBoardListener keyboardListener = new KeyBoardListener();
    private final MouseClicksListener mouseClicksListener = new MouseClicksListener();
    private final MouseMotionListener mouseMotionListener = new MouseMotionListener();
    private final MouseWheelListener mouseWheelListener = new MouseWheelListener();
    private final RecordingsRepository recordingsRepository = new RecordingsRepository();

    private void loadRecordConfiguration(){

        if (recordConfiguration == null){
            logger.log(Level.FATAL, "No Record configuration has been set");
            throw new NullPointerException();
        }

        // Keyboard interactions
        if (recordConfiguration.isRecordingKeyboardInteractions()) {
            GlobalScreen.addNativeKeyListener(keyboardListener);
        } else {
            GlobalScreen.removeNativeKeyListener(keyboardListener);
        }

        // Mouse click interactions
        if (recordConfiguration.isRecordingMouseClickInteractions()) {
            GlobalScreen.addNativeMouseListener(mouseClicksListener);
        } else {
            GlobalScreen.removeNativeMouseListener(mouseClicksListener);
        }

        // Mouse motion interactions
        if (recordConfiguration.isRecordingMouseMotionInteractions()) {
            GlobalScreen.addNativeMouseMotionListener(mouseMotionListener);
        } else {
            GlobalScreen.removeNativeMouseMotionListener(mouseMotionListener);
        }

        // Mouse wheel interactions

        if (recordConfiguration.isRecordingMouseWheelInteractions()) {
            GlobalScreen.addNativeMouseWheelListener(mouseWheelListener);
        } else {
            GlobalScreen.removeNativeMouseWheelListener(mouseWheelListener);
        }
    }

    public void startRecording(String recordingTitle) throws NativeHookException {

        logger.log(Level.TRACE, "Creating new Recording");
        this.recording = new Recording();
        recording.setRecordingTitle(recordingTitle);

        GlobalScreen.registerNativeHook();

        logger.log(Level.TRACE, "Loading configuration: {}", this.recordConfiguration);

            loadRecordConfiguration();

        logger.log(Level.TRACE, "Recording Started");
    }

    public InteractionRecorder()  {
        /* Do nothing */
    }

    public RecordingConfiguration getRecordConfiguration() {
        return recordConfiguration;
    }

    public void setRecordConfiguration(RecordingConfiguration recordConfiguration) {
        this.recordConfiguration = recordConfiguration;
    }


    public void stopRecording() {
        this.recordConfiguration = new RecordingConfiguration(
                false
                , false
                , false
                , false
        );
        loadRecordConfiguration();
        this.recording.closeRecording();
        recordingsRepository.insertRecording(recording);
        logger.log(Level.TRACE, "Recording Stopped and saved in database");

    }

    public Recording getlastRecording() {
        return recording;
    }


    private class KeyBoardListener implements NativeKeyListener{
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
    }

    public class MouseClicksListener implements NativeMouseListener{

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
    }



    /*      MOUSE-MOTION INTERACTIONS       */
    private class MouseMotionListener implements NativeMouseMotionListener{
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
    }

    private class MouseWheelListener implements NativeMouseWheelListener{

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
    }


    private void addNativeEventToRecording(NativeInputEvent event) {
        try {
            if (this.recording == null) {
                logger.log(Level.ERROR, "No available recording object to store events");
                throw new NullPointerException();
            } else {
                this.recording.getInputEvents().put(System.nanoTime(), event);
                String paramString = event.paramString();
                logger.log(Level.INFO, "Recorded : {}", paramString);
            }

        } catch (NullPointerException nullPointerException) {
            logger.log(Level.FATAL, nullPointerException);
        }
    }

}
