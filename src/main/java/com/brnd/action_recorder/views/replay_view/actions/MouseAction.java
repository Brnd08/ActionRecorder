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
package com.brnd.action_recorder.views.replay_view.actions;

import java.awt.*;

/**
 * Represents a ReplayableAction which is related to the system mouse
 */
public abstract class MouseAction extends ReplayableAction {
    private final int mouseX;
    private final int mouseY;
    private final MouseEventType mouseEventType;

    /**
     * Instantiates a new ReplayableAction type object with MOUSE_INPUT as it
     * ActionType
     *
     * @param mouseEventType a MouseAction.MouseEventType describing the MouseAction type.
     * @param eventX         the mouse X coordinate in which the action will be executed
     * @param eventY         the mouse Y coordinate in which the action will be executed
     */
    protected MouseAction(MouseEventType mouseEventType, int eventX, int eventY) {
        super.actionType = ActionType.MOUSE_INPUT;
        this.mouseEventType = mouseEventType;
        this.mouseX = eventX;
        this.mouseY = eventY;
    }

    /**
     * Positions the mouse location on the screen using the given Robot
     * at the mouse coordinates of this object
     *
     * @param robot the Robot object which will be used to position the mouse
     */
    protected void positionMouse(Robot robot) {
        robot.mouseMove(this.mouseX, this.mouseY);
    }

    /**
     * Sets the X screen coordinate in which this MouseAction will be executed
     *
     * @return the screen X coordinate as int
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * Gets the Y screen coordinate where this MouseAction will be executed
     *
     * @return the screen Y coordinate as int
     */
    public int getMouseY() {
        return mouseY;
    }

    /**
     * The type of this MouseAction, it can be any of the declared constants in {@link MouseEventType}
     *
     * @return the MouseEventType corresponding to this MouseAction object
     */
    public MouseEventType getMouseEventType() {
        return mouseEventType;
    }

    /**
     * Constants describing the available types for {@link MouseAction} objects.
     *
     * @see #MOTION
     * @see #BUTTON_CLICK
     * @see #SCROLL
     */
    protected enum MouseEventType {
        /**
         * A {@link MouseAction} type describing a mouse press or release.
         */
        BUTTON_CLICK,
        /**
         * A {@link MouseAction} type describing a mouse scroll.
         */
        SCROLL,
        /**
         * A {@link MouseAction} type describing a mouse movement.
         */
        MOTION
    }
}
