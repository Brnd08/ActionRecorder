package com.brnd.action_recorder.views.recording_start_view;

/**
 * This record class is used to store recording configurations
 * @param recordingKeyboardInteractions whether record Keyboard events (Key presses and releases)
 * @param recordingMouseMotionInteractions whether record Mouse Motion events (Mouse movement)
 * @param recordingMouseClickInteractions whether record Mouse Clicks events (Right, left, mouse, etc clicks)
 * @param recordingMouseWheelInteractions whether record Mouse Wheel events (Mouse wheel scroll)
 * @param recordingTitle the name associated to the record
 */
public record RecordingConfiguration(
        boolean recordingKeyboardInteractions,
        boolean recordingMouseMotionInteractions,
        boolean recordingMouseClickInteractions,
        boolean recordingMouseWheelInteractions,
        String recordingTitle
        ) {
    
    /**
     * Verifies if at least one of the available boolean variables corresponding to the native listeners 
     * has a true value
     * @return Whether at least one listener is enabled
     */
    public boolean isAtLeastOneListenerEnabled(){
        return
                recordingKeyboardInteractions || 
                recordingMouseMotionInteractions || 
                recordingMouseClickInteractions || 
                recordingMouseWheelInteractions;
    }
}