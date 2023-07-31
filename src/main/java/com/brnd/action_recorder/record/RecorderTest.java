package com.brnd.action_recorder.record;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.util.Timer;
import java.util.TimerTask;

public class RecorderTest {

    public static void main(String[] args) throws NativeHookException {

        InteractionRecorder interactionRecorder = new InteractionRecorder();


        RecordConfiguration recordConfiguration = new RecordConfiguration(false, false, true, false);

        interactionRecorder.setRecordConfiguration(recordConfiguration);
        System.out.println("Start recording");
        interactionRecorder.startRecording();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                interactionRecorder.stopRecording();
                System.out.println(interactionRecorder.getRecording().getInteractions());
                System.exit(0);
            }
        }, 15 * 1000);

    }
}
