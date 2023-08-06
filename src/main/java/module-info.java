module com.brnd.action_recorder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.base;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.github.kwhat.jnativehook;
    requires java.base;
    requires com.dustinredmond.fxtrayicon;

    opens com.brnd.action_recorder.views.main_view to javafx.fxml;
    opens com.brnd.action_recorder.views.settings_view to javafx.fxml;
    opens com.brnd.action_recorder.views.recording_start_view to javafx.fxml;
    opens com.brnd.action_recorder.views.utils to javafx.fxml;
    opens com.brnd.action_recorder.record.capturing to javafx.fxml;


    exports com.brnd.action_recorder.views.main_view;
    exports com.brnd.action_recorder.views.settings_view;
    exports com.brnd.action_recorder.views.utils;
    exports com.brnd.action_recorder.record.capturing;
}