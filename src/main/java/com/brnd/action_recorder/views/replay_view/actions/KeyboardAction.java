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
import java.awt.Robot;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeyboardAction extends ReplayableAction {

    private static final Logger logger = LogManager.getLogger(KeyboardAction.class);
    protected KeyActionType keyActionType;
    protected KeyType keyType;

    public KeyboardAction(NativeKeyEvent nativeKeyEvent) {
        super.actionType = ActionType.KEYBOARD_INPUT;
        
        
        var nativeEventType = nativeKeyEvent.getID();
        this.keyActionType = switch (nativeEventType) {
            case NativeKeyEvent.NATIVE_KEY_PRESSED -> KeyActionType.PRESS;
            case NativeKeyEvent.NATIVE_KEY_RELEASED -> KeyActionType.RELEASE;
            default -> {
                logger.log(
                        Level.FATAL
                        , "Invalid NativeKeyEvent id value: {}({}). Expecting NATIVE_KEY_PRESSED({}) or NATIVE_KEY_RELEASED({})"
                );
            }
        };

    }

    @Override
    protected void replayAction(Robot robot) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    protected enum KeyActionType {
        PRESS, RELEASE
    }

    protected enum KeyType {
        ALPHANUMERICAL, CONTROL, FUNCTIONAL, NAVIGATIONAL, ESPECIAL, OTHER
    }
}
