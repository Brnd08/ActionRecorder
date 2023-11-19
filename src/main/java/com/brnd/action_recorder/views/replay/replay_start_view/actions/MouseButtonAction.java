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
package com.brnd.action_recorder.views.replay.replay_start_view.actions;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseButtonAction extends MouseAction {
    private static final Logger logger = LogManager.getLogger(MouseButtonAction.class);
    private final ButtonActionType buttonActionType;
    private final int buttonId;

    /**
     * Creates a new MouseButtonAction instance using the specified NativeMouseEvent
     *
     * @param nativeMouseEvent A NativeMouseEvent wrapping a mouse button press or release
     * @throws IllegalStateException if the given NativeMouseEvent event type is other than a button press or release
     */
    public MouseButtonAction(NativeMouseEvent nativeMouseEvent, long protectedExecutionTime) {
        super(MouseEventType.BUTTON_CLICK, nativeMouseEvent.getX(), nativeMouseEvent.getY(), protectedExecutionTime);
        this.buttonId = this.parseButtonId(nativeMouseEvent.getButton());
        var nativeMouseEventId = nativeMouseEvent.getID(); // gets the id of the mouse event
        switch (nativeMouseEventId) {
            case NativeMouseEvent.NATIVE_MOUSE_PRESSED -> this.buttonActionType = ButtonActionType.BUTTON_PRESS;
            case NativeMouseEvent.NATIVE_MOUSE_RELEASED -> this.buttonActionType = ButtonActionType.BUTTON_RELEASE;
            default -> {
                logger.log(Level.ERROR
                        , "Unexpected NativeMouseEvent, expecting NativeMouseEvent.NATIVE_MOUSE_PRESSED:({}) or" +
                                " NativeMouseEvent.NATIVE_MOUSE_RELEASED:({}) and got: ({}) instead "
                        , NativeMouseEvent.NATIVE_MOUSE_PRESSED, NativeMouseEvent.NATIVE_MOUSE_RELEASED, nativeMouseEventId
                );
                throw new UnsupportedOperationException("Only mouse presses and releases are admitted.");
            }
        }
    }

    /**
     * Parses the given button id from an NativeMouseButtonEvent to an id
     * that can be used to identify this MouseButtonAction button
     *
     * @param nativeMouseButtonId the id of the NativeInputEvent
     * @return the parsed button id for Robot.mousePress() or Robot.mouseRelease() methods as int.
     */
    private int parseButtonId(int nativeMouseButtonId) {
        int parsedId = -1;
        /*
            System dependent button id modification
            wheel button and right button are inverted
         */
        if(nativeMouseButtonId == 2){ // in my computer 2 is the right mouse button id
            nativeMouseButtonId = 3;
        }else if(nativeMouseButtonId == 3){ // 3 is the wheel button id
            nativeMouseButtonId = 2;
        }
        try {
            parsedId = InputEvent.getMaskForButton(nativeMouseButtonId);
            logger.log(Level.ALL, "Mask obtained ({}) for button id: {}", parsedId, nativeMouseButtonId);
        } catch (IllegalArgumentException error) {
            logger.log(Level.ERROR, "Could not convert the mouseButtonId. Specified mouse button id: {}. Error: {}",
                    nativeMouseButtonId, error.getMessage());
        }
        return parsedId;
    }

    /**
     * Executes needed steps to reproduce this MouseButtonAction
     *
     * @param robot a Robot object which will be used to reproduce the action
     */
    @Override
    public void replayAction(Robot robot) {
        logger.log(Level.ALL, "Executing action: {}.", this);
        super.positionMouse(robot); // positions the mouse cursor at the action coordinates
        if (this.buttonActionType.equals(ButtonActionType.BUTTON_PRESS)) {// button press
            robot.mousePress(this.buttonId);
        } else {
            robot.mouseRelease(this.buttonId);// button release
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MouseButtonAction{");
        sb.append("buttonActionType=").append(buttonActionType);
        sb.append(", buttonId=").append(buttonId);
        sb.append(", mouseX=").append(super.getMouseX());
        sb.append(", mouseY=").append(super.getMouseY());
        sb.append(", mouseEventType=").append(super.getMouseEventType());
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Constant describing the button action type
     *
     * @see #BUTTON_PRESS
     * @see #BUTTON_RELEASE
     */
    private enum ButtonActionType {
        /**
         * Action Type describing a mouse button press
         */
        BUTTON_PRESS,
        /**
         * Action Type describing a mouse button release
         */
        BUTTON_RELEASE
    }
}
