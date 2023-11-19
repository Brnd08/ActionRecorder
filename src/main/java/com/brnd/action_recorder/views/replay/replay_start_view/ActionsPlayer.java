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
import java.util.Queue;
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
    private final Queue<ReplayableAction> replayableActions;
    private final float duration;

    /**
     * Instantiates a new ActionPlayer object
     *
     * @param inputEvents Queue containing each action to be replayed
     * @throws AWTException If the ActionPlayer could not be created due to application permissions
     */
    public ActionsPlayer(Queue<ReplayableAction> inputEvents, float duration) throws AWTException {
        this.robot = new Robot();
        this.duration = duration;
        this.replayableActions = inputEvents;
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
     * @param action The ReplayableAction to be scheduled
     */
    private void scheduleActionExecution(ReplayableAction action) {
        this.actionsScheduler.schedule(
                () -> {
                    action.replayAction(robot);
                    logger.log(Level.TRACE, "Execute Action: {}. System time: {}", action, System.nanoTime());
                }, action.getRelativeExecutionTime(), TimeUnit.NANOSECONDS);
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

}
