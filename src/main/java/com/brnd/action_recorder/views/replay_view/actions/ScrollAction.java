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

import com.github.kwhat.jnativehook.mouse.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

/**
 * A ReplayableAction representing a mouse scroll input
 */
public class ScrollAction extends MouseAction {
    private static final Logger logger = LogManager.getLogger(ScrollAction.class);
    private final ScrollDirection scrollDirection;
    private final int wheelScroll;

    /**
     * Creates a new ScrollAction object using the given NativeInputEvent
     *
     * @param nativeMouseWheelEvent The NativeMouseWheelEvent to be used for the object instantiation
     */
    public ScrollAction(NativeMouseWheelEvent nativeMouseWheelEvent) {
        super(MouseEventType.SCROLL, nativeMouseWheelEvent.getX(), nativeMouseWheelEvent.getY());
        this.wheelScroll = nativeMouseWheelEvent.getScrollAmount();

        var wheelDirection = nativeMouseWheelEvent.getWheelDirection();
        var wheelRotation = nativeMouseWheelEvent.getWheelRotation();
        this.scrollDirection = switch (wheelDirection) {
            case NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION ->
                    (wheelRotation < 0) ? ScrollDirection.VERTICAL_NEGATIVE : ScrollDirection.VERTICAL_POSITIVE;
            case NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION ->
                    (wheelRotation < 0) ? ScrollDirection.HORIZONTAL_NEGATIVE : ScrollDirection.HORIZONTAL_POSITIVE;
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

    /**
     * Executes needed steps to reproduce this ScrollAction
     *
     * @param robot a Robot object which will be used to reproduce the action
     */
    @Override
    public void replayAction(Robot robot) {
        switch (scrollDirection) {
            case VERTICAL_NEGATIVE -> {
                logger.log(Level.ALL, "Executing action: {}", this);
                robot.mouseWheel(-1);
            }
            case VERTICAL_POSITIVE -> {
                logger.log(Level.ALL, "Executing action: {}", this);
                robot.mouseWheel(1);
            }
            case HORIZONTAL_NEGATIVE, HORIZONTAL_POSITIVE -> {
                logger.log(Level.ALL, "Scroll inputs with a scroll direction of {} are currently unsupported for execution", scrollDirection);
            }
            default -> logger.log(Level.ALL, "Unsupported scrollDirection {}", scrollDirection);

        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScrollAction{");
        sb.append("scrollDirection=").append(scrollDirection);
        sb.append(", wheelScroll=").append(wheelScroll);
        sb.append(", mouseX=").append(super.getMouseX());
        sb.append(", mouseY=").append(super.getMouseY());
        sb.append(", mouseEventType=").append(super.getMouseEventType());
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Constant Describing the a ScrollAction type
     *
     * @see #VERTICAL_NEGATIVE
     * @see #VERTICAL_POSITIVE
     * @see #HORIZONTAL_NEGATIVE
     * @see #HORIZONTAL_POSITIVE
     */
    public enum ScrollDirection {
        /**
         * A vertical scroll from top to bottom
         */
        VERTICAL_POSITIVE,
        /**
         * A vertical scroll from bottom to top
         */
        VERTICAL_NEGATIVE,
        /**
         * A horizontal scroll from left to right
         */
        HORIZONTAL_POSITIVE,
        /**
         * A vertical scroll from right to left
         */
        HORIZONTAL_NEGATIVE

    }
}

