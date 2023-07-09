package com.brnd08.action_recorder.views.utils;

/**
 * This enum contains constants for each available position of the Stages relative to
 * the screen bounds
 */
public enum StageLocation {
    UPPER_RIGHT_CORNER("Esquina superior derecha."),
    UPPER_LEFT_CORNER("Esquina superior izquierda."),
    LOWER_RIGHT_CORNER("Esquina inferior derecha."),
    LOWER_LEFT_CORNER("Esquina inferior izquierda."),
    CENTER("Centro.");

    private StageLocation(String toShow){
        this.toShowString = toShow;
    }

    public String getToShowString(){
        return this.toShowString;
    }
    private final String toShowString;
}
