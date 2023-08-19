package com.brnd.action_recorder.views.utils;

import de.intelligence.windowstoolbar.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskbarButtonsUtils {
    private static final Logger logger = LogManager.getLogger(TaskbarButtonsUtils.class);
    
    private TaskbarButtonsUtils(){
        logger.log(Level.ALL, "Utility classes should not be instantiated");
    }

    public static IWindowsTaskbar addButtonToStage( String icontitle, String tooltip, String icoPath
            , ITaskbarButtonClickListener onClickedListener) 
    {
        logger.log(Level.INFO, "Adding new button with icontitle: {}, tooltip: {}, iconPath: {}, onclickedListener: {}"
                , icontitle, tooltip, icoPath, onClickedListener
        );
        
        final var testIcon = Icon.fromPath(icontitle, icoPath);
        var taskbarButtonListBuilder
                = TaskbarButtonListBuilder
                        .builder()
                        .buttonBuilder()
                        .setIcon(testIcon)
                        .setTooltip(tooltip)                        
                        .withFlag(TaskbarButtonFlag.ENABLED)
                        .setOnClicked(onClickedListener)
                        .build();
        return TaskbarBuilder.builder()
                .setHWnd(WindowHandleFinder.getFromActiveWindow())
                .autoInit()
                .overrideWndProc()
                .addButtons(
                        taskbarButtonListBuilder.build()
                )
                .build();
    }
    public static void addThumbnailButtons(){
        TaskbarButtonsUtils.addButtonToStage(
                "Resume"
                ,"Resumir Grabación"
                , "C:\\Users\\brdn\\OneDrive - Instituto Politecnico Nacional\\Desktop\\CLASSROOM 6TO\\ActionRecorder\\src\\main\\resources\\com\\brnd\\action_recorder\\views\\images\\playIcon.ico"
                , buttonClickEvent -> logger.log(Level.INFO, "Resume taskbar thumbnail button clicked")
                );
        TaskbarButtonsUtils.addButtonToStage(
                "Pause"
                ,"Pausar Grabación"
                , "C:\\Users\\brdn\\OneDrive - Instituto Politecnico Nacional\\Desktop\\CLASSROOM 6TO\\ActionRecorder\\src\\main\\resources\\com\\brnd\\action_recorder\\views\\images\\pauseIcon.ico"
                , buttonClickEvent -> logger.log(Level.INFO, "Pause taskbar thumbnail button clicked")
                );
        TaskbarButtonsUtils.addButtonToStage(
                "Stop"
                ,"Detener Grabación"
                , "C:\\Users\\brdn\\OneDrive - Instituto Politecnico Nacional\\Desktop\\CLASSROOM 6TO\\ActionRecorder\\src\\main\\resources\\com\\brnd\\action_recorder\\views\\images\\stopIcon.ico"
                , buttonClickEvent -> logger.log(Level.INFO, "Stop taskbar thumbnail button clicked")
                );
    }
}
