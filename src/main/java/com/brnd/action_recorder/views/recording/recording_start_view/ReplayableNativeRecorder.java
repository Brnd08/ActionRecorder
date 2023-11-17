package com.brnd.action_recorder.views.recording.recording_start_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.replay.replay_start_view.actions.*;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class has functionalities to create new Recording containing ReplayableAction objects
 */
public class ReplayableNativeRecorder implements NativeRecorder {
    public static final Logger logger = LogManager.getLogger(ReplayableNativeRecorder.class);
    private long recordingPauseTime = 0L;
    private RecordingConfiguration recordingConfiguration;
    private long eventsTimeOffset = 0L;
    private long recordingStartTime;
    private Recording recording;
    private final MouseMotionListener mouseMovementListener;
    private final ScrollListener scrollListener;
    private final MouseButtonsListener mouseButtonsListener;
    private final KeyboardListener keyboardListener;

    public ReplayableNativeRecorder() {
        this.mouseButtonsListener = new MouseButtonsListener(this);
        this.mouseMovementListener = new MouseMotionListener(this);
        this.scrollListener = new ScrollListener(this);
        this.keyboardListener = new KeyboardListener(this);
    }

    /**
     * Applies the configurations from the current recordingConfiguration object
     * This method is meant to be called when a new recording will be started
     *
     * @throws NullPointerException if the recordingConfiguration object has not been set
     */
    private void loadRecordingConfiguration() {
        if (recordingConfiguration == null) {
            logger.log(Level.FATAL, "No Record configuration has been set");
            throw new NullPointerException();
        }
        // Keyboard interactions
        if (recordingConfiguration.recordingKeyboardInteractions()) {
            GlobalScreen.addNativeKeyListener(keyboardListener);
        } else {
            GlobalScreen.removeNativeKeyListener(keyboardListener);
        }
        // Mouse buttons interactions
        if (recordingConfiguration.recordingMouseClickInteractions()) {
            GlobalScreen.addNativeMouseListener(mouseButtonsListener);
        } else {
            GlobalScreen.removeNativeMouseListener(mouseButtonsListener);
        }
        // Mouse movements interactions
        if (recordingConfiguration.recordingMouseMotionInteractions()) {
            GlobalScreen.addNativeMouseMotionListener(mouseMovementListener);
        } else {
            GlobalScreen.removeNativeMouseMotionListener(mouseMovementListener);
        }
        // Scroll interactions
        if (recordingConfiguration.recordingMouseWheelInteractions()) {
            GlobalScreen.addNativeMouseWheelListener(scrollListener);
        } else {
            GlobalScreen.removeNativeMouseWheelListener(scrollListener);
        }
    }

    /**
     * Starts a new recording using the given RecordingConfiguration
     *
     * @param recordingConfiguration The recording configuration specifying the events to be caught
     * @throws NativeHookException If a problem occurs while registering needed listeners
     */
    @Override
    public void startRecording(RecordingConfiguration recordingConfiguration) throws NativeHookException {
        GlobalScreen.setEventDispatcher(new SwingDispatchService());// Force JNativeHook to use the Swing thread
        GlobalScreen.registerNativeHook(); // Enables native hook

        logger.log(Level.TRACE, "Creating new Recording");
        this.recording = new Recording();
        this.recordingStartTime = this.recording.getRecordingStartTime();

        this.recordingConfiguration = recordingConfiguration;
        logger.log(Level.TRACE, "Loading configuration: {}", this.recordingConfiguration);
        loadRecordingConfiguration(); // loads the specified configuration

        logger.log(Level.TRACE, "Recording Started");
    }

    /**
     * Removes listeners from native hook to stop events listening
     */
    private void removeAllListeners() {
        GlobalScreen.removeNativeKeyListener(keyboardListener);
        GlobalScreen.removeNativeMouseListener(mouseButtonsListener);
        GlobalScreen.removeNativeMouseMotionListener(mouseMovementListener);
        GlobalScreen.removeNativeMouseWheelListener(scrollListener);
    }

    /**
     * Pauses the current recording
     */
    public void pauseRecording() {
        this.removeAllListeners();
        this.recordingPauseTime = System.nanoTime();
        logger.log(Level.INFO, "Pause recording at {}", recordingPauseTime / 1_000_000_000.0f);
    }

    /**
     * Resumes the paused recording if any
     */
    @Override
    public void resumeRecording() {
        this.loadRecordingConfiguration();
        long resumeTime = System.nanoTime();
        this.eventsTimeOffset = resumeTime - this.recordingPauseTime;
        logger.log(Level.INFO, "Resume recording at {}, the recording lasted paused by {} seconds"
                , resumeTime / 1_000_000_000.0f, this.eventsTimeOffset / 1_000_000_000.0f);
    }

    /**
     * Returns the last stopped Recording if any
     *
     * @return Recording containing the caught events
     */
    @Override
    public Recording getLastRecording() {
        return this.recording;
    }

    /**
     * Saves the given NativeInputEvent to the current recording
     *
     * @param event The caught NativeInputEvent
     */
    public void saveEvent(NativeInputEvent event) {
        try {
            long eventTime = System.nanoTime() - this.eventsTimeOffset;
            // Adds the event to recording events along its timestamp (relative to the recording start time)
            long eventRelativeTime = eventTime - recordingStartTime;
            this.recording.getInputEvents().put(eventRelativeTime, parseReplayableAction(event));

            String paramString = event.toString();
            logger.log(Level.INFO, "Recorded : {}", paramString);
        } catch (NullPointerException nullPointerException) {
            logger.log(Level.ERROR, "No available recording object to store events");
            logger.log(Level.ERROR, nullPointerException);
        }
    }

    /**
     * Returns the corresponding ReplayableAction of the given NativeInputEvent
     *
     * @param nativeEvent The NativeInputEvent to be parsed
     */
    private ReplayableAction parseReplayableAction(NativeInputEvent nativeEvent) {
        ReplayableAction parsedAction = null;
        if (nativeEvent instanceof NativeMouseWheelEvent mouseWheelEvent) { // scroll events
            parsedAction = new ScrollAction(mouseWheelEvent);
        } else if (nativeEvent instanceof NativeKeyEvent keyEvent) { // keyboard events
            parsedAction = new KeyboardAction(keyEvent);
        } else if (nativeEvent instanceof NativeMouseEvent mouseEvent) { // mouse clicks and movements
            parsedAction = (mouseEvent.getID() == NativeMouseEvent.NATIVE_MOUSE_MOVED) ?
                    new MouseMotionAction(mouseEvent) : new MouseButtonAction(mouseEvent);
        }
        return parsedAction;
    }

    /**
     * Stops the current recording
     */
    public void stopRecording() {
        removeAllListeners();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            logger.log(Level.ERROR, ex);
        }
        this.recording.closeRecording(this.eventsTimeOffset);
        logger.log(Level.TRACE, "Recording Stopped");
    }
}
