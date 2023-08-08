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
package com.brnd.action_recorder.views.utils;

import java.util.Arrays;
import java.util.List;

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

    private final String toShowString;

    private StageLocation(String toShow) {
        this.toShowString = toShow;
    }

    /**
     * Maps a StageLocation enum constant using its toShowString value
     * @param toShowString the toShowString property of the constant
     * @return the StageLocation associated to the toShowString
     */
    public static StageLocation stageLocationFromToShowString(String toShowString) {
        return Arrays.stream(StageLocation.values())
                .filter(stagePosition -> stagePosition.getToShowString().equals(toShowString))
                .findFirst().orElseThrow();
    }

    /**
     * Obtain all the toShowString for all the StageLocation enum constants
     * @return List<String> containing the toShowStrings
     */
    public static List<String> getToShowStringsList(){
        return Arrays
                .stream(StageLocation.values())
                .map(StageLocation::getToShowString)
                .toList();
    }

    public String getToShowString() {
        return this.toShowString;
    }
}
