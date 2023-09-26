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
package com.brnd.action_recorder.views.utils;

import java.util.Arrays;
import java.util.Objects;
import javafx.scene.image.Image;

/**
 * This enum contains constants for the views in the project, each constant has
 * attributes as path to fxml file and the stage title for the view. The class
 * contains an Image containing the app Icon
 */
public enum ViewEnum {

    MAIN(
            "Grabadora de Acciones - brdn",
            "/main_view/mainView.fxml"
    ), SETTINGS(
            "Configuraciones - Grabadora de Acciones",
            "/settings_view/settingsView.fxml"
    ), RECORDING_START(
            "Comenzar Grabación - Grabadora de Acciones",
             "/recording/recording_start_view/recordingStartView.fxml"
    ), RECORDING_SAVING(
            "Guardar Grabación - Grabadora de Acciones",
             "/recording/recording_saving_view/recordingSavingView.fxml"
    ), REPLAY(
            "Reproducir Grabación - Grabadora de Acciones",
            "/replay/replay_selection_view/replaySelectionView.fxml"
    ), REPLAY_START(
            "Reproducir Grabación - Grabadora de Acciones",
                    "/replay/replay_start_view/replayStartView.fxml"
    );

    private static final String PATH_TO_VIEWS_PACKAGE_FROM_SOURCE_ROOT
            = "/com/brnd/action_recorder/views";

    private static final Image APP_ICON
            = new Image(
                    Objects.requireNonNull(
                            ViewEnum.class.getResourceAsStream(
                                    PATH_TO_VIEWS_PACKAGE_FROM_SOURCE_ROOT + "/images/appIcon.png"
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

    public static Image getAppIcon() {
        return APP_ICON;
    }

    /**
     * Maps a ViewEnum constant using the specified title
     *
     * @param title the title of the view enum
     * @return the ViewEnum associated to the given title
     */
    public static ViewEnum fromTitle(String title) {
        return Arrays.stream(ViewEnum.values())
                .filter(viewEnum -> viewEnum.getStageTitle().equals(title))
                .findFirst().orElseThrow();
    }
}
