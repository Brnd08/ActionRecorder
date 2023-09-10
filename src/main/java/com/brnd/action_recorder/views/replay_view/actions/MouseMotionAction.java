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

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class MouseMotionAction extends MouseAction {
    private static final Logger logger = LogManager.getLogger(MouseMotionAction.class);

    /**
     * Creates a new MouseMotionAction instance using the specified NativeMouseEvent.
     * @param nativeMouseEvent the NativeMouseEvent which will be used.
     * @throws IllegalStateException if the given NativeMouseEvent has an ID different from NativeMouseEvent.NATIVE_MOUSE_MOVED
     */
    public MouseMotionAction(NativeMouseEvent nativeMouseEvent) throws IllegalStateException{
        super(MouseEventType.MOTION, nativeMouseEvent.getX(), nativeMouseEvent.getY());
        var nativeMouseEventId = nativeMouseEvent.getID();
        if (nativeMouseEventId != NativeMouseEvent.NATIVE_MOUSE_MOVED) {
            logger.log(Level.ERROR
                    , "Unexpected NativeMouseEvent id, expecting NativeMouseEvent.NATIVE_MOUSE_MOVED:({}) and got: ({}) instead"
                    , NativeMouseEvent.NATIVE_MOUSE_MOVED, nativeMouseEventId
            );
            throw new IllegalStateException("Only mouse presses and releases are admitted.");
        }
    }

    /**
     * Executes needed steps to reproduce this MouseMotionAction
     *
     * @param robot a Robot object which will be used to reproduce the action
     */
    @Override
    public void replayAction(Robot robot) {
        logger.log(Level.ALL, "Executing action: {}.", this);
        this.positionMouse(robot);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MouseMotionAction{");
        sb.append("mouseX=").append(super.getMouseX());
        sb.append(", mouseY=").append(super.getMouseY());
        sb.append(", mouseEventType=").append(super.getMouseEventType());
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }
}
