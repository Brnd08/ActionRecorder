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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MouseMotionAction{");
        sb.append("mouseX=").append(mouseX);
        sb.append(", mouseY=").append(mouseY);
        sb.append(", mouseEventType=").append(mouseEventType);
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }

    public MouseMotionAction(NativeMouseEvent nativeMouseEvent) {
        var nativeMouseEventId = nativeMouseEvent.getID();
        if(nativeMouseEventId== NativeMouseEvent.NATIVE_MOUSE_MOVED){
            super.mouseEventType = MouseEventType.MOTION;
            super.mouseX = nativeMouseEvent.getX();
            super.mouseY = nativeMouseEvent.getY();

        }else{
            logger.log(Level.ERROR
                    , "Unexpected NativeMouseEvent id, expecting NativeMouseEvent.NATIVE_MOUSE_MOVED:({}) and got: ({}) instead"
                    , NativeMouseEvent.NATIVE_MOUSE_MOVED, nativeMouseEventId
            );
            throw new UnsupportedOperationException("Only mouse presses and releases are admitted.");
        }
    }

    @Override
    public void replayAction(Robot robot) {
        logger.log(Level.ALL, "Unimplemented functionality replayAction.");
    }
}
