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

import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.InputEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This class provides methods for Window positioning, as window drag implementation or
 * positioning the stage based on a specified location
 */
public abstract class StagePositioner {
    // Coordinates Offsets for window drag action/motion
    private static double xOffset;
    private static double yOffset;

    private StagePositioner() { // To prevent class instantiation in utility classes
        throw new UnsupportedOperationException("Utility class can not be instantiated");
    }


    /**
     * Makes the specified Stage draggable based on given scene interactions
     *
     * @param stage The Stage to make draggable
     * @param scene The scene to link to the drag motion/action
     */
    public static void addDragFunctionalityToStage(Stage stage, Scene scene) {
        scene.setOnMousePressed(event -> { // gets the offsets between mouse click and window screen coordinates
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        scene.setOnMouseDragged(event -> { // when mouse released, set the new window screen coordinates
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

    }

    /**
     * Sets stage coordinates depending on specified position.
     *
     * @param position The position to be applied.
     */
    public static void setStageLocation(Stage stage, StageLocation position) {
        // Gets the visible bounds of the screen as a Rectangle2D object
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Screen's Height and width
        double screenHeight = screenBounds.getHeight();
        double screenWidth = screenBounds.getWidth();

        // Stage's height and width
        double stageHeight = stage.getHeight();
        double stageWidth = stage.getWidth();

        // Stage's coordinates to be applied
        double stageXCoordinate = 0d;
        double stageYCoordinate = 0d;

        switch (position) {
            case LOWER_LEFT_CORNER -> {
                stageYCoordinate = screenHeight - stageHeight;
                stageXCoordinate = 0d;
            }
            case LOWER_RIGHT_CORNER -> {
                stageYCoordinate = screenHeight - stageHeight;
                stageXCoordinate = screenWidth - stageWidth;
            }
            case UPPER_LEFT_CORNER -> {
                stageYCoordinate = 0d;
                stageXCoordinate = 0d;
            }
            case UPPER_RIGHT_CORNER -> {
                stageYCoordinate = 0d;
                stageXCoordinate = screenWidth - stageWidth;
            }
            case CENTER -> {
                stageYCoordinate = (screenHeight - stageHeight) / 2d;
                stageXCoordinate = (screenWidth - stageWidth) / 2d;
            }
        }
        // set calculated coordinates to the stage
        stage.setX(stageXCoordinate);
        stage.setY(stageYCoordinate);
    }

    /**
     * Gets the specific stage from where this event was triggered
     * @param event The input event
     * @return The corresponding stage for the input event
     */
    public static Stage getStageFromEvent(Event event){
        return ((Stage) (((Node) event.getSource()).getScene().getWindow()));
    }
}
