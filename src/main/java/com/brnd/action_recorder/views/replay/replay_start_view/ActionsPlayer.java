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
package com.brnd.action_recorder.views.replay.replay_start_view;

import com.brnd.action_recorder.views.replay.replay_start_view.actions.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Robot;
import java.awt.AWTException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class contains needed functionalities for actions reproducing
 */
public class ActionsPlayer {
    private static final Logger logger = LogManager.getLogger(ActionsPlayer.class);
    private ScheduledExecutorService actionsScheduler = Executors.newScheduledThreadPool(1);
    private final Robot robot;
    private final Map<Long, ReplayableAction> replayableActions = new HashMap<>();
    private boolean mouseEvents = false;
    private boolean keyboardEvents = false;
    private boolean scrollEvents = false;
    private boolean clickEvents = false;
    private final float duration;

    /**
     * Instantiates a new ActionPlayer object
     *
     * @param inputEventMap Map containing each action replay time as key and its related NativeInputEvent as value.
     * @throws AWTException If the ActionPlayer could not be created due to application permissions
     */
    public ActionsPlayer(Map<Long, ReplayableAction> inputEventMap, float duration) throws AWTException {
        this.robot = new Robot();
        this.duration = duration;
        this.replayableActions.putAll(inputEventMap);
        inputEventMap.forEach(
                (executionTime, replayableAction) -> {
                    if (replayableAction instanceof MouseMotionAction && !mouseEvents) {
                        this.mouseEvents = true;
                    } else if (replayableAction instanceof MouseButtonAction && !clickEvents) {
                        this.clickEvents = true;
                    } else if (replayableAction instanceof KeyboardAction && !keyboardEvents) {
                        this.keyboardEvents = true;
                    } else if (replayableAction instanceof ScrollAction && !scrollEvents) {
                        this.scrollEvents = true;
                    }
                    logger.log(Level.INFO, "New ReplayableAction processed. Execution Time: {}. Action: {}"
                            , executionTime, replayableAction
                    );
                }
        );
    }

    /**
     * Pauses the current replay process
     */
    public void pauseReplay() {
        logger.log(Level.ALL, "Unimplemented functionality pauseRecording()");
    }

    /**
     * Starts actions replaying process
     *
     * @param finalizationCallback A runnable to execute when all events have been executed
     */
    public void startReplay(Runnable finalizationCallback) {
        if(this.actionsScheduler.isShutdown()){ // if the scheduler was shutdown, create another instance
            this.actionsScheduler = new ScheduledThreadPoolExecutor(1);
        }
        this.replayableActions.forEach(this::scheduleActionExecution);
        this.actionsScheduler.schedule(() -> {
            Platform.runLater(
                    finalizationCallback
            );
            logger.log(Level.TRACE, "Replay Finalization reached. System time: {}", System.nanoTime());
        }, Math.round(duration) + 2L, TimeUnit.SECONDS);
    }

    /**
     * Finalizes the current replay process
     */
    public void stopReplay() {
        logger.log(Level.INFO, "Unsupported functionality stopReplay");
    }

    /**
     * Schedules the given ReplayableAction for execution after given time
     *
     * @param executionTime the time the recording will wait from schedule time
     * @param action        The ReplayableAction to be scheduled
     */
    private void scheduleActionExecution(long executionTime, ReplayableAction action) {
        this.actionsScheduler.schedule(
                () -> {
                    action.replayAction(robot);
                    logger.log(Level.TRACE, "Execute Action: {}. System time: {}", action, System.nanoTime());
                }, executionTime, TimeUnit.NANOSECONDS);
    }


    public void resumeRecording() {
        logger.log(Level.ALL, "Unimplemented functionality resumeRecording(long)");
    }

    /**
     * Stops the replaying of the actions which has not been already executed
     * @return true if the replaying was correctly stopped
     */
    public boolean stopReplaying() {
        logger.log(Level.ALL, "Stoping events replay");
        this.actionsScheduler.shutdownNow();
        var shutdowned = this.actionsScheduler.isShutdown();
        if (shutdowned) {
            logger.log(Level.ALL, "Successfully shutdown replaying");
        } else {
            logger.log(Level.ALL, "Could not shutdown replaying");
        }

        return shutdowned;

    }

    public boolean containsMouseEvents() {
        return mouseEvents;
    }

    public boolean containsKeyboardEvents() {
        return keyboardEvents;
    }

    public boolean containsScrollEvents() {
        return scrollEvents;
    }

    public boolean containsClickEvents() {
        return clickEvents;
    }
}
