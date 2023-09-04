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

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

public class ScrollAction extends MouseAction {
    private static final Logger logger = LogManager.getLogger(ScrollAction.class);
    private final ScrollDirection scrollDirection;
    private final int whellScroll;

    public ScrollAction(NativeMouseWheelEvent nativeMouseWheelEvent) {
        super.mouseEventType = MouseEventType.SCROLL;
        super.mouseX = nativeMouseWheelEvent.getX();
        super.mouseY = nativeMouseWheelEvent.getY();
        this.whellScroll = nativeMouseWheelEvent.getScrollAmount();

        var wheelDirection = nativeMouseWheelEvent.getWheelDirection();
        var wheelRotation = nativeMouseWheelEvent.getWheelRotation();
        this.scrollDirection = switch (wheelDirection) {
            case NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION -> (wheelRotation < 0)? ScrollDirection.VERTICAL_NEGATIVE: ScrollDirection.VERTICAL_POSITIVE;
            case NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION -> (wheelRotation < 0)? ScrollDirection.HORIZONTAL_NEGATIVE: ScrollDirection.HORIZONTAL_POSITIVE;
            default -> {
                logger.log(
                        Level.FATAL
                        , "Invalid nativeMouseWheelEvent wheel direction: ({}). Expecting NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION({}) or NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION({})"
                        , wheelDirection, NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION, NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION
                );
                throw new IllegalStateException("Unexpected value: " + wheelDirection);
            }
        };
    }

    @Override
    protected void replayAction(Robot robot) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private enum ScrollDirection{
        VERTICAL_POSITIVE, VERTICAL_NEGATIVE, HORIZONTAL_POSITIVE, HORIZONTAL_NEGATIVE
    }
}

