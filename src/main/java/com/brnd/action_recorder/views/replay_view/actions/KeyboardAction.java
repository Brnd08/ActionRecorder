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

import java.awt.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A replayable user-computer interaction representing a keyboard key press or release
 */
public class KeyboardAction extends ReplayableAction {
    private static final Logger logger = LogManager.getLogger(KeyboardAction.class);
    private final KeyActionType keyActionType;
    private final KeyType keyType;
    /**
     * Creates a new KeyboardAction from the given NativeInputEvent
     * @param nativeKeyEvent A NativeKeyEvent representing either a key release or key press.
     * @throws IllegalStateException if the given NativeKeyEvent is other event type than key press or release
     */
    public KeyboardAction(NativeKeyEvent nativeKeyEvent) throws IllegalStateException{
        super.actionType = ActionType.KEYBOARD_INPUT; // sets the actionType to keyboard input
        this.keyType = this.getKeyType(nativeKeyEvent);
        var nativeEventType = nativeKeyEvent.getID(); // gets the id of the given NativeKeyEvent
        this.keyActionType = switch (nativeEventType) { // assign the key action type based on the event id
            case NativeKeyEvent.NATIVE_KEY_PRESSED -> KeyActionType.PRESS;
            case NativeKeyEvent.NATIVE_KEY_RELEASED -> KeyActionType.RELEASE;
            default -> { // if the id is different from a key press or key release event throws an IllegalStateException
                logger.log(
                        Level.FATAL
                        , "Invalid NativeKeyEvent id value: ({}). Expecting NATIVE_KEY_PRESSED({}) or NATIVE_KEY_RELEASED({})"
                        , nativeEventType, NativeKeyEvent.NATIVE_KEY_PRESSED, NativeKeyEvent.NATIVE_KEY_RELEASED
                );
                throw new IllegalStateException("Unexpected value: " + nativeEventType);
            }
        };

    }
    
    private KeyType getKeyType(NativeKeyEvent keyEvent){
        logger.log(Level.ALL, "Unimplemented functionality getKeyType(NativeKeyEvent)");
        return KeyType.ALPHANUMERICAL;
    }

    /**
     * Executes needed steps to reproduce the KeyboardAction
     *
     * @param robot an Robot object which will be used to reproduce the action
     */
    @Override
    public void replayAction(Robot robot) {
        logger.log(Level.ALL, "Unimplemented functionality replayAction.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyboardAction{");
        sb.append("keyActionType=").append(keyActionType);
        sb.append(", keyType=").append(keyType);
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Constants describing the available KeyboardAction types
     * @see #PRESS
     * @see #RELEASE
     */
    protected enum KeyActionType {
        /**
         * Key actions describing a key press
         */
        PRESS,
        /**
         * Key actions describing a key release
         */
        RELEASE
    }
    /**
     * Enum describing the KeyboardActions key type
     * @see #ALPHANUMERICAL
     * @see #CONTROL
     * @see #FUNCTIONAL
     * @see #NAVIGATIONAL
     * @see #SPECIAL
     * @see #OTHER
     */
    protected enum KeyType {
        /**
         * An alphanumerical (numbers and letters only) key
         */
        ALPHANUMERICAL,
        /**
         * A control key (such as CTRL, ALT, ALT-GR, ESC, etc)
         */
        CONTROL,
        /**
         * A Functional key including they keys from F1 up to F12
         */
        FUNCTIONAL,
        /**
         * A navigational key are used to navigate through the contents of
         * web pages or documents (start, end, av-pag, re-pag, insert, delete, and key arrows).
         */
        NAVIGATIONAL,
        /**
         * Keys with special function such as Caps-Lock, Tab, Num-Lock, etc
         */
        SPECIAL,
        /**
         * Keys not included in other key types
         * @see #SPECIAL
         * @see #FUNCTIONAL
         * @see #CONTROL
         * @see #ALPHANUMERICAL
         * @see #NAVIGATIONAL
         */
        OTHER
    }
}
