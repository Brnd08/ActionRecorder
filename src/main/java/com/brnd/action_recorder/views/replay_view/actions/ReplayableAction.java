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
 * Represents an user-computer interaction which can be reproduced by the program
 */
public abstract class ReplayableAction{
    protected ActionType actionType;

    /**
     * Executes needed steps to reproduce the ReplayableAction
     * @param robot an Robot object which will be used to reproduce the action
     */
    public abstract void replayAction(Robot robot);

    /**
     * Constants for each type of ReplayableAction types.
     */
    enum ActionType {
        MOUSE_INPUT
        , KEYBOARD_INPUT
    }
}
