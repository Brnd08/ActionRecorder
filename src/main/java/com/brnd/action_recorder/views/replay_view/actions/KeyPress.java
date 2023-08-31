package com.brnd.action_recorder.views.replay_view.actions;

import java.awt.*;

public class KeyPress extends KeyboardAction{
    public KeyPress(){
        super.eventType = KeyEventType.PRESS;

    }

    @Override
    protected void executeAction(Robot robot) {

    }

    @Override
    protected void replayAction(Robot robot) {

    }
}
