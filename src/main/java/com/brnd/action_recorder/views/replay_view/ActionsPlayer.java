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
package com.brnd.action_recorder.views.replay_view;

import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.replay_view.actions.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Contains methods and functionalities to reproduce a list of InputEvents
 */
public class ActionsPlayer {
    private static final Logger logger = LogManager.getLogger(ActionsPlayer.class);
    private final ScheduledExecutorService actionsScheduler = Executors.newScheduledThreadPool(1);
    private final Robot robot;
    private final Map<Long, ReplayableAction> replayableActions = new HashMap<>();

    public ActionsPlayer(Map<Long, NativeInputEvent> inputEventMap) throws AWTException {
        this.robot = new Robot();
        inputEventMap.forEach(
                (executionTime, recordedAction) -> {
                    var replayableAction = this.parseReplayableAction(recordedAction);
                    this.replayableActions.put(executionTime, replayableAction);
                    logger.log(Level.INFO, "New ReplayableAction processed. Execution Time: {}. Action: {}"
                            , executionTime, replayableAction
                    );
                }
        );
    }

    public void startReplay(){
        logger.log(Level.INFO, "Starting Actions Replay");
        this.replayableActions.forEach(this::scheduleActionExecution);
    }

    public void stopReplay(){
        logger.log(Level.INFO, "Unsupported functionality stopReplay");
    }

    private void scheduleActionExecution(long executionTime, ReplayableAction action) {
        this.actionsScheduler.schedule(
                () -> {
                    action.replayAction(robot);
                    logger.log(Level.TRACE, "Execute Action: {}. System time: {}", action, System.nanoTime());
                }, executionTime, TimeUnit.NANOSECONDS);
    }

    private ReplayableAction parseReplayableAction(NativeInputEvent nativeEvent) {
        ReplayableAction parsedAction = null;
        if (nativeEvent instanceof NativeMouseWheelEvent mouseWheelEvent) { // scroll events
            parsedAction = new ScrollAction(mouseWheelEvent);
        } else if (nativeEvent instanceof NativeKeyEvent keyEvent) { // keyboard events
            parsedAction = new KeyboardAction(keyEvent);
        } else if (nativeEvent instanceof NativeMouseEvent mouseEvent) { // mouse clicks and movements
            parsedAction = (mouseEvent.getID() == NativeMouseEvent.NATIVE_MOUSE_MOVED) ?
                    new MouseMotionAction(mouseEvent) : new MouseButtonAction(mouseEvent);
        }
        return parsedAction;
    }
}
