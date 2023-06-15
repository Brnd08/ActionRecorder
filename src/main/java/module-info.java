module com.example.actionrecorder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.brnd08.action_recorder to javafx.fxml;
    exports com.brnd08.action_recorder;
}