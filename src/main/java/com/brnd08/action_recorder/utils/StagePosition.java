package com.brnd08.action_recorder.utils;

public enum StagePosition {
    UPPER_RIGHT_CORNER("Esquina superior derecha."),
    UPPER_LEFT_CORNER("Esquina superior izquierda."),
    LOWER_RIGHT_CORNER("Esquina inferior derecha."),
    LOWER_LEFT_CORNER("Esquina inferior izquierda."),
    CENTER("Centro.");

    private StagePosition(String toShow){
        this.toShowString = toShow;
    }

    public String getToShowString(){
        return this.toShowString;
    }
    private final String toShowString;
}
