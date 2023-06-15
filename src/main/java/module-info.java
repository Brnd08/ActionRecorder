module com.example.actionrecorder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.actionrecorder to javafx.fxml;
    exports com.example.actionrecorder;
}