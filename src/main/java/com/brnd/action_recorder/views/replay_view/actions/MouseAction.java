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

/**
 * Represents a ReplayableAction which is related to the system mouse
 */
public abstract class MouseAction extends ReplayableAction{
  /**
   * Instantiates a new ReplayableAction type object with MOUSE_INPUT as it
   * ActionType
   */
  protected MouseAction(){
    super.actionType = ActionType.MOUSE_INPUT;
  }
    protected int mouseX;
    protected int mouseY;
    protected MouseEventType mouseEventType;

  /**
   * Constants describing the MouseAction Type
   */
  protected enum MouseEventType {
        BUTTON_CLICK, SCROLL, MOTION
    }
}
