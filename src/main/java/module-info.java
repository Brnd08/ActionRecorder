module com.example.actionrecorder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.brnd08.action_recorder.views.main_view to javafx.fxml;
    opens com.brnd08.action_recorder.views.settings_view to javafx.fxml;
    opens com.brnd08.action_recorder.views.utils to javafx.fxml;

    exports com.brnd08.action_recorder.views.main_view;
    exports com.brnd08.action_recorder.views.settings_view;
    exports com.brnd08.action_recorder.views.utils;
}