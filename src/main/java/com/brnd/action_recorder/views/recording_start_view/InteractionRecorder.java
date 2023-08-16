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
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.views.recording_start_view.RecordingConfiguration;
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
    private Recording recording;
    private RecordingConfiguration recordConfiguration;
    private final KeyBoardListener keyboardListener = new KeyBoardListener();
    private final MouseClicksListener mouseClicksListener = new MouseClicksListener();
    private final MouseMotionListener mouseMotionListener = new MouseMotionListener();
    private final MouseWheelListener mouseWheelListener = new MouseWheelListener();

    /**
     * Applies the configurations from the recordConfiguration object
     * This method is meant to be when starting a new recording in the startRecording() method
     * @throws NullPointerException if the recordConfiguration object has not been created
     */
    private void loadRecordConfiguration(){

        if (recordConfiguration == null){
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

    public void startRecording(RecordingConfiguration recordConfiguration) throws NativeHookException {
        // Force JNativeHook to use the Swing thread
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        logger.log(Level.TRACE, "Creating new Recording");
        this.recording = new Recording();
        // Enables native hook
        GlobalScreen.registerNativeHook();


        // sets and load the recordConfiguration
        this.recordConfiguration = recordConfiguration;
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


    /**
     * Removes all listeners from native hook to stop listening to all events
     */
    private void removeListeners(){
        
            GlobalScreen.removeNativeKeyListener(keyboardListener);
            GlobalScreen.removeNativeMouseListener(mouseClicksListener);
            GlobalScreen.removeNativeMouseMotionListener(mouseMotionListener);
            GlobalScreen.removeNativeMouseWheelListener(mouseWheelListener);
    }

    /**
     * Stops the current recording by removing all listeners and disabling native hook
     */
    public void stopRecording() {
        removeListeners();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            logger.log(Level.ERROR,ex);
        }
        this.recording.closeRecording();
        logger.log(Level.TRACE, "Recording Stopped");
    }

    /**
     * Returns the previously created recording
     * @return Recording containing the caught events
     */
    public Recording getlastRecording() {
        return recording;
    }

    /**
     * This class implements methods to catch Keyboard related events such
     * as key presses, key releases and typed keys
     */
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
            if (recordConfiguration.recordingKeyboardInteractions()) {
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
            if (recordConfiguration.recordingKeyboardInteractions()) {
                addNativeEventToRecording(nativeEvent);
            }
        }
    }

    /**
     * This class implements methods to catch Mouse click events such
     * as button mouse presses, releases and clicks
     */
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
            if (recordConfiguration.recordingMouseClickInteractions()) {
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
            if (recordConfiguration.recordingMouseClickInteractions()) {
                addNativeEventToRecording(nativeEvent);
            }
        }
    }

    /**
     * This class implements methods to catch Mouse motion event such as mouse movements
     * and drags
     */
    private class MouseMotionListener implements NativeMouseMotionListener{
        /**
         * Invoked when the mouse has been moved.
         *
         * @param nativeEvent the native mouse event.
         */
        @Override
        public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
            if (recordConfiguration.recordingMouseMotionInteractions()) {
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

    /**
     * This class implements methods to catch Mouse wheel events such as scrolling movements
     */
    private class MouseWheelListener implements NativeMouseWheelListener{

        /**
         * Invoked when the mouse wheel is rotated.
         *
         * @param nativeEvent the native mouse wheel event.
         */
        @Override
        public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
            if (recordConfiguration.recordingMouseWheelInteractions()) {
                addNativeEventToRecording(nativeEvent);
            }
        }
    }

    /**
     * Adds the specified NativeInputEvent to the current recording
     * @param event The caught event
     */
    private void addNativeEventToRecording(NativeInputEvent event) {
        try {
            if (this.recording == null) {
                logger.log(Level.ERROR, "No available recording object to store events");
                throw new NullPointerException();
            } else {
                this.recording.getInputEvents().put(System.nanoTime(), event);// adds the event and the current execution time
                String paramString = event.paramString();
                logger.log(Level.INFO, "Recorded : {}", paramString);
            }

        } catch (NullPointerException nullPointerException) {
            logger.log(Level.FATAL, nullPointerException);
        }
    }
}
