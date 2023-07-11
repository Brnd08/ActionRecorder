module com.brnd.action_recorder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.base;

    opens com.brnd.action_recorder.views.main_view to javafx.fxml;
    opens com.brnd.action_recorder.views.settings_view to javafx.fxml;
    opens com.brnd.action_recorder.views.utils to javafx.fxml;

    exports com.brnd.action_recorder.views.main_view;
    exports com.brnd.action_recorder.views.settings_view;
    exports com.brnd.action_recorder.views.utils;
}