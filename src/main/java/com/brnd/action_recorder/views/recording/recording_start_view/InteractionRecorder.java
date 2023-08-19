/*
 * Copyright (C) 2023 Brandon Velazquez & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.brnd.action_recorder.views.recording.recording_start_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.recording.recording_start_view.RecorderConfiguration;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class has functionalities to create recordings objects based on
 * specified configurations
 */
public class InteractionRecorder {

    public static final Logger logger = LogManager.getLogger(InteractionRecorder.class);
    private final MouseMotionListener mouseMotionListener = new MouseMotionListener();
    private final MouseWheelListener mouseWheelListener = new MouseWheelListener();
    private final MouseClicksListener mouseClicksListener = new MouseClicksListener();
    private final KeyBoardListener keyboardListener = new KeyBoardListener();
    private long pauseStartTime = 0l;
    private RecorderConfiguration recordConfiguration;
    private Recording recording;
    private Long eventsTimeStampOffset = 0l;

    /**
     * Instantiates a InteractionRecorder object
     */
    public InteractionRecorder() {
        /* Do nothing */
    }

    /**
     * Starts a new recording using the given RecorderConfiguration
     *
     * @param recordConfiguration The recorder configuration specifying the
     * events to be catch
     * @throws NativeHookException If a problem occurs while enabling Native
     * Hook
     */
    public void startRecording(RecorderConfiguration recordConfiguration) throws NativeHookException {
        GlobalScreen.setEventDispatcher(new SwingDispatchService());// Force JNativeHook to use the Swing thread
        GlobalScreen.registerNativeHook(); // Enables native hook

        logger.log(Level.TRACE, "Creating new Recording");
        this.recording = new Recording();

        logger.log(Level.TRACE, "Loading configuration: {}", this.recordConfiguration);
        this.recordConfiguration = recordConfiguration;
        loadRecordConfiguration(); // loads the specified configuration

        logger.log(Level.TRACE, "Recording Started");
    }

    /**
     * Stops the current recording
     */
    public void stopRecording() {
        removeListeners();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            logger.log(Level.ERROR, ex);
        }
        this.recording.closeRecording();
        logger.log(Level.TRACE, "Recording Stopped");
    }

    /**
     * Pauses the current recording and register the pause timestamp
     *
     * @param pauseTime Timestamp of pause call in nanoseconds
     */
    public void pauseRecording(long pauseTime) {
        this.removeListeners();
        this.pauseStartTime = pauseTime;
        logger.log(Level.INFO, "Pause recording at {}", pauseTime);
    }

    /**
     * Resumes the paused recording
     *
     * @param resumeTime Timestamp of resume call in nanoseconds
     */
    public void resumeRecording(long resumeTime) {
        this.loadRecordConfiguration();
        this.eventsTimeStampOffset = resumeTime - this.pauseStartTime;
        logger.log(Level.INFO, "Resume recording at {}, the recording remained paused for {} seconds"
                , resumeTime, this.eventsTimeStampOffset/1_000_000_000l);
    }

    /**
     * Applies the configurations from the current recordConfiguration object
     * This method is meant to be when starting a new recording in the
     * startRecording() method
     *
     * @throws NullPointerException if the recordConfiguration object has not
     * been set
     */
    private void loadRecordConfiguration() {
        if (recordConfiguration == null) {
            logger.log(Level.FATAL, "No Record configuration has been set");
            throw new NullPointerException();
        }
        // Keyboard interactions
        if (recordConfiguration.recordingKeyboardInteractions()) {
            GlobalScreen.addNativeKeyListener(keyboardListener);
        } else {
            GlobalScreen.removeNativeKeyListener(keyboardListener);
        }
        // Mouse click interactions
        if (recordConfiguration.recordingMouseClickInteractions()) {
            GlobalScreen.addNativeMouseListener(mouseClicksListener);
        } else {
            GlobalScreen.removeNativeMouseListener(mouseClicksListener);
        }
        // Mouse motion interactions
        if (recordConfiguration.recordingMouseMotionInteractions()) {
            GlobalScreen.addNativeMouseMotionListener(mouseMotionListener);
        } else {
            GlobalScreen.removeNativeMouseMotionListener(mouseMotionListener);
        }
        // Mouse wheel interactions
        if (recordConfiguration.recordingMouseWheelInteractions()) {
            GlobalScreen.addNativeMouseWheelListener(mouseWheelListener);
        } else {
            GlobalScreen.removeNativeMouseWheelListener(mouseWheelListener);
        }
    }

    /**
     * Removes listeners from native hook to stop events listening
     */
    private void removeListeners() {
        GlobalScreen.removeNativeKeyListener(keyboardListener);
        GlobalScreen.removeNativeMouseListener(mouseClicksListener);
        GlobalScreen.removeNativeMouseMotionListener(mouseMotionListener);
        GlobalScreen.removeNativeMouseWheelListener(mouseWheelListener);
    }

    /**
     * Returns the previously created recording
     *
     * @return Recording containing the caught events
     */
    public Recording getlastRecording() {
        return recording;
    }    

    /**
     * This class implements methods to catch Keyboard related events such as
     * key presses, key releases and typed keys
     */
    private class KeyBoardListener implements NativeKeyListener {

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
            addNativeEventToRecording(nativeEvent);
        }

        /**
         * Invoked when a key has been released.
         *
         * @param nativeEvent the native key event.
         */
        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * This class implements methods to catch Mouse click events such as button
     * mouse presses, releases and clicks
     */
    public class MouseClicksListener implements NativeMouseListener {

        /**
         * Invoked when a mouse button has been clicked (pressed and released)
         * without being moved.
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
            addNativeEventToRecording(nativeEvent);
        }

        /**
         * Invoked when a mouse button has been released
         *
         * @param nativeEvent the native mouse event.
         */
        @Override
        public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * This class implements methods to catch Mouse motion event such as mouse
     * movements and drags
     */
    private class MouseMotionListener implements NativeMouseMotionListener {

        /**
         * Invoked when the mouse has been moved.
         *
         * @param nativeEvent the native mouse event.
         */
        @Override
        public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
            addNativeEventToRecording(nativeEvent);
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

    /**
     * This class implements methods to catch Mouse wheel events such as
     * scrolling movements
     */
    private class MouseWheelListener implements NativeMouseWheelListener {

        /**
         * Invoked when the mouse wheel is rotated.
         *
         * @param nativeEvent the native mouse wheel event.
         */
        @Override
        public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
            addNativeEventToRecording(nativeEvent);
        }
    }

    /**
     * Adds the specified NativeInputEvent to the current recording
     *
     * @param event The caught event
     */
    private void addNativeEventToRecording(NativeInputEvent event) {
        try {
                long eventTimeStamp = System.nanoTime() - this.eventsTimeStampOffset;                
                this.recording.getInputEvents().put(eventTimeStamp, event);// adds the event and the current execution time
                String paramString = event.paramString();
                logger.log(Level.INFO, "Recorded : {}", paramString);
        } catch (NullPointerException nullPointerException) {
                logger.log(Level.ERROR, "No available recording object to store events");
                logger.log(Level.ERROR, nullPointerException);
        }
    }
}
