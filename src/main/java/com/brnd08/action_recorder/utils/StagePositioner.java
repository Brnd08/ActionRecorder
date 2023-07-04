package com.brnd08.action_recorder.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StagePositioner {

    // Coordinates Offsets for window drag action/motion
    private static double xOffset;
    private static double yOffset;


    /**
     * Makes the specified Stage draggable based on given scene interactions
     * @param scene The scene to link to the drag motion/action
     */
    public static void addStageDragFunctionality(Stage stage, Scene scene){
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
    public static void setStagePosition(Stage stage,  StagePosition position) {
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

        switch(position){
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
                stageYCoordinate = (screenHeight - stageHeight)/2;
                stageXCoordinate = (screenWidth - stageWidth)/2;
            }
        }
        stage.setX(stageXCoordinate);
        stage.setY(stageYCoordinate);
    }

}
