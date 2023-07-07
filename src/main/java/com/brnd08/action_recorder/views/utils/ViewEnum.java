package com.brnd08.action_recorder.views.utils;

import javafx.scene.image.Image;

import java.util.Objects;

public enum ViewEnum {

    MAIN(
            "Action Recorder || By brdn",
            "/main_view/mainView.fxml"
    ),
    SETTINGS(
            "Settings || Action Recorder",
            "/settings_view/settingsView.fxml"
    );

    private static final String PATH_TO_VIEWS_PACKAGE_FROM_SOURCE_ROOT
            = "/com/brnd08/action_recorder/views";

    private static final Image APP_ICON =
            new Image(
                    Objects.requireNonNull(
                            ViewEnum.class.getResourceAsStream(
                                    PATH_TO_VIEWS_PACKAGE_FROM_SOURCE_ROOT + "/images/appIcon.gif"
                            )
                    )
            );

    private final String stageTitle;

    private final String fxmlPath;

    ViewEnum(String stageTitle, String fxmlUrlFromViewsPackage) {
        this.stageTitle = stageTitle;
        this.fxmlPath = PATH_TO_VIEWS_PACKAGE_FROM_SOURCE_ROOT + fxmlUrlFromViewsPackage;
    }

    public String getStageTitle() {
        return this.stageTitle;
    }

    public String getFxmlPath() {
        return this.fxmlPath;
    }

    public Image getAppIcon() {
        return APP_ICON;
    }

}
