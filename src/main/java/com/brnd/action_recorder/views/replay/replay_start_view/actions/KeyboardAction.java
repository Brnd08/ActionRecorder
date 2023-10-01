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
package com.brnd.action_recorder.views.replay.replay_start_view.actions;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A replayable user-computer interaction representing a keyboard key press or release
 */
public class KeyboardAction extends ReplayableAction {
    private static final Logger logger = LogManager.getLogger(KeyboardAction.class);
    /**
     * Contains every VC keycode constants declared on the @{link NativeInputEvent} class as keys and it corresponding
     * JWT VK constants representation declared on @{link KeyEvent} class.
     * NOTE: Only contains the VC keycodes that has a direct representation as a VK keycode
     */
    private static final HashMap<Integer, Integer> keyCodesDictionary = HashMap.newHashMap(50);
    private final KeyActionType keyActionType;
    private final KeyType keyType;
    private final int actionKeyCode;
    private final String keyText;
    private final String nativeKeyText;

    /**
     * Creates a new KeyboardAction from the given NativeInputEvent
     *
     * @param nativeKeyEvent A NativeKeyEvent representing either a key release or key press.
     * @throws IllegalStateException if the given NativeKeyEvent is other event type than key press or release
     */
    public KeyboardAction(NativeKeyEvent nativeKeyEvent) throws IllegalStateException {
        super.actionType = ActionType.KEYBOARD_INPUT; // sets the actionType to keyboard input
        this.keyType = this.getKeyType(nativeKeyEvent);
        this.nativeKeyText = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
        this.actionKeyCode = KeyboardAction.keyCodesDictionary
                .getOrDefault(nativeKeyEvent.getKeyCode(), KeyEvent.VK_UNDEFINED);

        this.keyText = KeyEvent.getKeyText(this.actionKeyCode);
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

    private KeyType getKeyType(NativeKeyEvent keyEvent) {
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
        logger.log(Level.ALL, "Executing action: {}", this);
        if (this.actionKeyCode == KeyEvent.VK_UNDEFINED) {
            logger.log(Level.ALL, "Can not execute action with undefined keycode {}", this);
        } else if (keyActionType == KeyActionType.RELEASE) {
            robot.keyRelease(this.actionKeyCode);
        } else {
            robot.keyPress(this.actionKeyCode);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyboardAction{");
        sb.append("keyActionType=").append(keyActionType);
        sb.append(", keyType=").append(keyType);
        sb.append(", actionKeyCode=").append(actionKeyCode);
        sb.append(", keyText='").append(keyText).append('\'');
        sb.append(", nativeKeyText='").append(nativeKeyText).append('\'');
        sb.append(", actionType=").append(actionType);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Constants describing the available KeyboardAction types
     *
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
     *
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
         *
         * @see #SPECIAL
         * @see #FUNCTIONAL
         * @see #CONTROL
         * @see #ALPHANUMERICAL
         * @see #NAVIGATIONAL
         */
        OTHER
    }


    static {
        /*
            Adds every VC keycode and its related VK keycode to the keycodes dictionary
         */
        keyCodesDictionary.put(NativeKeyEvent.VC_ESCAPE, KeyEvent.VK_ESCAPE);
        keyCodesDictionary.put(NativeKeyEvent.VC_F1, KeyEvent.VK_F1);
        keyCodesDictionary.put(NativeKeyEvent.VC_F2, KeyEvent.VK_F2);
        keyCodesDictionary.put(NativeKeyEvent.VC_F3, KeyEvent.VK_F3);
        keyCodesDictionary.put(NativeKeyEvent.VC_F4, KeyEvent.VK_F4);
        keyCodesDictionary.put(NativeKeyEvent.VC_F5, KeyEvent.VK_F5);
        keyCodesDictionary.put(NativeKeyEvent.VC_F6, KeyEvent.VK_F6);
        keyCodesDictionary.put(NativeKeyEvent.VC_F7, KeyEvent.VK_F7);
        keyCodesDictionary.put(NativeKeyEvent.VC_F8, KeyEvent.VK_F8);
        keyCodesDictionary.put(NativeKeyEvent.VC_F9, KeyEvent.VK_F9);
        keyCodesDictionary.put(NativeKeyEvent.VC_F10, KeyEvent.VK_F10);
        keyCodesDictionary.put(NativeKeyEvent.VC_F11, KeyEvent.VK_F11);
        keyCodesDictionary.put(NativeKeyEvent.VC_F12, KeyEvent.VK_F12);
        keyCodesDictionary.put(NativeKeyEvent.VC_F13, KeyEvent.VK_F13);
        keyCodesDictionary.put(NativeKeyEvent.VC_F14, KeyEvent.VK_F14);
        keyCodesDictionary.put(NativeKeyEvent.VC_F15, KeyEvent.VK_F15);
        keyCodesDictionary.put(NativeKeyEvent.VC_F16, KeyEvent.VK_F16);
        keyCodesDictionary.put(NativeKeyEvent.VC_F17, KeyEvent.VK_F17);
        keyCodesDictionary.put(NativeKeyEvent.VC_F18, KeyEvent.VK_F18);
        keyCodesDictionary.put(NativeKeyEvent.VC_F19, KeyEvent.VK_F19);
        keyCodesDictionary.put(NativeKeyEvent.VC_F20, KeyEvent.VK_F20);
        keyCodesDictionary.put(NativeKeyEvent.VC_F21, KeyEvent.VK_F21);
        keyCodesDictionary.put(NativeKeyEvent.VC_F22, KeyEvent.VK_F22);
        keyCodesDictionary.put(NativeKeyEvent.VC_F23, KeyEvent.VK_F23);
        keyCodesDictionary.put(NativeKeyEvent.VC_F24, KeyEvent.VK_F24);
        keyCodesDictionary.put(NativeKeyEvent.VC_BACKQUOTE, KeyEvent.VK_BACK_QUOTE);
        keyCodesDictionary.put(NativeKeyEvent.VC_1, KeyEvent.VK_1);
        keyCodesDictionary.put(NativeKeyEvent.VC_2, KeyEvent.VK_2);
        keyCodesDictionary.put(NativeKeyEvent.VC_3, KeyEvent.VK_3);
        keyCodesDictionary.put(NativeKeyEvent.VC_4, KeyEvent.VK_4);
        keyCodesDictionary.put(NativeKeyEvent.VC_5, KeyEvent.VK_5);
        keyCodesDictionary.put(NativeKeyEvent.VC_6, KeyEvent.VK_6);
        keyCodesDictionary.put(NativeKeyEvent.VC_7, KeyEvent.VK_7);
        keyCodesDictionary.put(NativeKeyEvent.VC_8, KeyEvent.VK_8);
        keyCodesDictionary.put(NativeKeyEvent.VC_9, KeyEvent.VK_9);
        keyCodesDictionary.put(NativeKeyEvent.VC_0, KeyEvent.VK_0);
        keyCodesDictionary.put(NativeKeyEvent.VC_MINUS, KeyEvent.VK_MINUS);
        keyCodesDictionary.put(NativeKeyEvent.VC_EQUALS, KeyEvent.VK_EQUALS);
        keyCodesDictionary.put(NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE);
        keyCodesDictionary.put(NativeKeyEvent.VC_TAB, KeyEvent.VK_TAB);
        keyCodesDictionary.put(NativeKeyEvent.VC_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
        keyCodesDictionary.put(NativeKeyEvent.VC_A, KeyEvent.VK_A);
        keyCodesDictionary.put(NativeKeyEvent.VC_B, KeyEvent.VK_B);
        keyCodesDictionary.put(NativeKeyEvent.VC_C, KeyEvent.VK_C);
        keyCodesDictionary.put(NativeKeyEvent.VC_D, KeyEvent.VK_D);
        keyCodesDictionary.put(NativeKeyEvent.VC_E, KeyEvent.VK_E);
        keyCodesDictionary.put(NativeKeyEvent.VC_F, KeyEvent.VK_F);
        keyCodesDictionary.put(NativeKeyEvent.VC_G, KeyEvent.VK_G);
        keyCodesDictionary.put(NativeKeyEvent.VC_H, KeyEvent.VK_H);
        keyCodesDictionary.put(NativeKeyEvent.VC_I, KeyEvent.VK_I);
        keyCodesDictionary.put(NativeKeyEvent.VC_J, KeyEvent.VK_J);
        keyCodesDictionary.put(NativeKeyEvent.VC_K, KeyEvent.VK_K);
        keyCodesDictionary.put(NativeKeyEvent.VC_L, KeyEvent.VK_L);
        keyCodesDictionary.put(NativeKeyEvent.VC_M, KeyEvent.VK_M);
        keyCodesDictionary.put(NativeKeyEvent.VC_N, KeyEvent.VK_N);
        keyCodesDictionary.put(NativeKeyEvent.VC_O, KeyEvent.VK_O);
        keyCodesDictionary.put(NativeKeyEvent.VC_P, KeyEvent.VK_P);
        keyCodesDictionary.put(NativeKeyEvent.VC_Q, KeyEvent.VK_Q);
        keyCodesDictionary.put(NativeKeyEvent.VC_R, KeyEvent.VK_R);
        keyCodesDictionary.put(NativeKeyEvent.VC_S, KeyEvent.VK_S);
        keyCodesDictionary.put(NativeKeyEvent.VC_T, KeyEvent.VK_T);
        keyCodesDictionary.put(NativeKeyEvent.VC_U, KeyEvent.VK_U);
        keyCodesDictionary.put(NativeKeyEvent.VC_V, KeyEvent.VK_V);
        keyCodesDictionary.put(NativeKeyEvent.VC_W, KeyEvent.VK_W);
        keyCodesDictionary.put(NativeKeyEvent.VC_X, KeyEvent.VK_X);
        keyCodesDictionary.put(NativeKeyEvent.VC_Y, KeyEvent.VK_Y);
        keyCodesDictionary.put(NativeKeyEvent.VC_Z, KeyEvent.VK_Z);
        keyCodesDictionary.put(NativeKeyEvent.VC_OPEN_BRACKET, KeyEvent.VK_OPEN_BRACKET);
        keyCodesDictionary.put(NativeKeyEvent.VC_CLOSE_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
        keyCodesDictionary.put(NativeKeyEvent.VC_BACK_SLASH, KeyEvent.VK_BACK_SLASH);
        keyCodesDictionary.put(NativeKeyEvent.VC_SEMICOLON, KeyEvent.VK_SEMICOLON);
        keyCodesDictionary.put(NativeKeyEvent.VC_QUOTE, KeyEvent.VK_QUOTE);
        keyCodesDictionary.put(NativeKeyEvent.VC_ENTER, KeyEvent.VK_ENTER);
        keyCodesDictionary.put(NativeKeyEvent.VC_COMMA, KeyEvent.VK_COMMA);
        keyCodesDictionary.put(NativeKeyEvent.VC_PERIOD, KeyEvent.VK_PERIOD);
        keyCodesDictionary.put(NativeKeyEvent.VC_SLASH, KeyEvent.VK_SLASH);
        keyCodesDictionary.put(NativeKeyEvent.VC_SPACE, KeyEvent.VK_SPACE);
        keyCodesDictionary.put(NativeKeyEvent.VC_PRINTSCREEN, KeyEvent.VK_PRINTSCREEN);
        keyCodesDictionary.put(NativeKeyEvent.VC_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
        keyCodesDictionary.put(NativeKeyEvent.VC_PAUSE, KeyEvent.VK_PAUSE);
        keyCodesDictionary.put(NativeKeyEvent.VC_INSERT, KeyEvent.VK_INSERT);
        keyCodesDictionary.put(NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE);
        keyCodesDictionary.put(NativeKeyEvent.VC_HOME, KeyEvent.VK_HOME);
        keyCodesDictionary.put(NativeKeyEvent.VC_END, KeyEvent.VK_END);
        keyCodesDictionary.put(NativeKeyEvent.VC_PAGE_UP, KeyEvent.VK_PAGE_UP);
        keyCodesDictionary.put(NativeKeyEvent.VC_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        keyCodesDictionary.put(NativeKeyEvent.VC_UP, KeyEvent.VK_UP);
        keyCodesDictionary.put(NativeKeyEvent.VC_LEFT, KeyEvent.VK_LEFT);
        keyCodesDictionary.put(NativeKeyEvent.VC_CLEAR, KeyEvent.VK_CLEAR);
        keyCodesDictionary.put(NativeKeyEvent.VC_RIGHT, KeyEvent.VK_RIGHT);
        keyCodesDictionary.put(NativeKeyEvent.VC_DOWN, KeyEvent.VK_DOWN);
        keyCodesDictionary.put(NativeKeyEvent.VC_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
        keyCodesDictionary.put(NativeKeyEvent.VC_SEPARATOR, KeyEvent.VK_SEPARATOR);
        keyCodesDictionary.put(NativeKeyEvent.VC_SHIFT, KeyEvent.VK_SHIFT);
        keyCodesDictionary.put(NativeKeyEvent.VC_CONTROL, KeyEvent.VK_CONTROL);
        keyCodesDictionary.put(NativeKeyEvent.VC_ALT, KeyEvent.VK_ALT);
        keyCodesDictionary.put(NativeKeyEvent.VC_META, KeyEvent.VK_WINDOWS);
        keyCodesDictionary.put(NativeKeyEvent.VC_CONTEXT_MENU, KeyEvent.VK_CONTEXT_MENU);
        keyCodesDictionary.put(NativeKeyEvent.VC_KATAKANA, KeyEvent.VK_KATAKANA);
        keyCodesDictionary.put(NativeKeyEvent.VC_UNDERSCORE, KeyEvent.VK_UNDERSCORE);
        keyCodesDictionary.put(NativeKeyEvent.VC_KANJI, KeyEvent.VK_KANJI);
        keyCodesDictionary.put(NativeKeyEvent.VC_HIRAGANA, KeyEvent.VK_HIRAGANA);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_HELP, KeyEvent.VK_HELP);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_STOP, KeyEvent.VK_STOP);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_PROPS, KeyEvent.VK_PROPS);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_FIND, KeyEvent.VK_FIND);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_AGAIN, KeyEvent.VK_AGAIN);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_COPY, KeyEvent.VK_COPY);
        keyCodesDictionary.put(NativeKeyEvent.VC_SUN_CUT, KeyEvent.VK_CUT);
    }
}
