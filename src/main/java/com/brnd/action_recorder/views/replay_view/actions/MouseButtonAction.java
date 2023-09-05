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

import java.awt.Robot;

public class MouseButtonAction extends MouseAction {
    private static final Logger logger = LogManager.getLogger(MouseButtonAction.class);
    private final ButtonActionType buttonActionType;
    private final int buttonId;

    public MouseButtonAction(NativeMouseEvent nativeMouseEvent) {
        var nativeMouseEventId = nativeMouseEvent.getID();
        switch (nativeMouseEventId) {
            case NativeMouseEvent.NATIVE_MOUSE_PRESSED -> this.buttonActionType = ButtonActionType.BUTTON_PRESS;
            case NativeMouseEvent.NATIVE_MOUSE_RELEASED -> this.buttonActionType = ButtonActionType.BUTTON_RELEASE;
            default -> {
                logger.log(Level.ERROR
                        , "Unexpected NativeMouseEvent, expecting NativeMouseEvent.NATIVE_MOUSE_PRESSED:({}) or NativeMouseEvent.NATIVE_MOUSE_RELEASED:({}) and got: ({}) instead "
                        , NativeMouseEvent.NATIVE_MOUSE_PRESSED, NativeMouseEvent.NATIVE_MOUSE_RELEASED, nativeMouseEventId
                );
                throw new UnsupportedOperationException("Only mouse presses and releases are admitted.");
            }
        }
        this.buttonId = this.parseButtonId(nativeMouseEvent.getButton());
        super.mouseEventType = MouseEventType.BUTTON_CLICK;
        super.mouseX = nativeMouseEvent.getX();
        super.mouseY = nativeMouseEvent.getY();
    }

    private int parseButtonId(int nativeMouseButtonId) {
        logger.log(Level.ALL, "Unimplemented method functionality parseButtonId, returning given buttonId: {}", nativeMouseButtonId);
        return nativeMouseButtonId;
    }

    @Override
    protected void replayAction(Robot robot) {
        logger.log(Level.ALL, "Unimplemented functionality replayAction.");
    }

    private enum ButtonActionType {
        BUTTON_PRESS, BUTTON_RELEASE
    }
}
