package com.brnd.action_recorder.record;

public class RecordConfiguration {
    private boolean recordingKeyboardInteractions;
    private boolean recordingMouseMotionInteractions;

    public boolean isRecordingKeyboardInteractions() {
        return recordingKeyboardInteractions;
    }

    public void setIsRecordingKeyboardInteractions(boolean recordingKeyboardInteractions) {
        this.recordingKeyboardInteractions = recordingKeyboardInteractions;
    }

    public boolean isRecordingMouseMotionInteractions() {
        return recordingMouseMotionInteractions;
    }

    public void setIsRecordingMouseMotionInteractions(boolean recordingMouseMotionInteractions) {
        this.recordingMouseMotionInteractions = recordingMouseMotionInteractions;
    }

    public boolean isRecordingMouseClickInteractions() {
        return recordingMouseClickInteractions;
    }

    public void setIsRecordingMouseClickInteractions(boolean recordingMouseClickInteractions) {
        this.recordingMouseClickInteractions = recordingMouseClickInteractions;
    }

    public boolean isRecordingMouseWheelInteractions() {
        return recordingMouseWheelInteractions;
    }

    public void setIsRecordingMouseWheelInteractions(boolean recordingMouseWheelInteractions) {
        this.recordingMouseWheelInteractions = recordingMouseWheelInteractions;
    }

    private boolean recordingMouseClickInteractions;
    private boolean recordingMouseWheelInteractions;

    public RecordConfiguration(boolean recordingKeyboardInteractions, boolean recordingMouseMotionInteractions, boolean recordingMouseClickInteractions, boolean recordingMouseWheelInteractions) {
        this.recordingKeyboardInteractions = recordingKeyboardInteractions;
        this.recordingMouseMotionInteractions = recordingMouseMotionInteractions;
        this.recordingMouseClickInteractions = recordingMouseClickInteractions;
        this.recordingMouseWheelInteractions = recordingMouseWheelInteractions;
    }
    public RecordConfiguration(){
        this.recordingKeyboardInteractions = true;
        this.recordingMouseMotionInteractions = true;
        this.recordingMouseClickInteractions = true;
        this.recordingMouseWheelInteractions = true;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RecordConfiguration{");
        sb.append("recordKeyboardInteractions=").append(recordingKeyboardInteractions);
        sb.append(", recordMouseMotionInteractions=").append(recordingMouseMotionInteractions);
        sb.append(", recordMouseClickInteractions=").append(recordingMouseClickInteractions);
        sb.append(", recordMouseWheelInteractions=").append(recordingMouseWheelInteractions);
        sb.append('}');
        return sb.toString();
    }



}
