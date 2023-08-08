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
package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.views.utils.StageLocation;

/**
 * This class is used to store Settings properties for the app
 */
public class Settings {
    public static final Settings DEFAULT_SETTINGS = new Settings(StageLocation.CENTER, true, false);
    private StageLocation initialViewLocation;
    private boolean showAlwaysOnTop;
    private boolean useSystemTrayOnRecordingEnabled;

    public Settings(StageLocation initialViewLocation, boolean showAlwaysOnTop) {
        this.initialViewLocation = initialViewLocation;
        this.showAlwaysOnTop = showAlwaysOnTop;
    }
    public Settings(StageLocation initialViewLocation, boolean showAlwaysOnTop, boolean useSystemTray){
        this.initialViewLocation = initialViewLocation;
        this.showAlwaysOnTop = showAlwaysOnTop;
        this.useSystemTrayOnRecordingEnabled = useSystemTray;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Settings{");
        sb.append("initialViewLocation=").append(initialViewLocation);
        sb.append(", showAlwaysOnTop=").append(showAlwaysOnTop);
        sb.append(", useSystemTrayOnRecording=").append(useSystemTrayOnRecordingEnabled);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings settings = (Settings) o;

        if (showAlwaysOnTop != settings.showAlwaysOnTop) return false;
        return getInitialViewLocation() == settings.getInitialViewLocation();
    }

    @Override
    public int hashCode() {
        int result = getInitialViewLocation().hashCode();
        result = 31 * result + (showAlwaysOnTop ? 1 : 0);
        return result;
    }

    public StageLocation getInitialViewLocation() {
        return initialViewLocation;
    }

    public void setInitialViewLocation(StageLocation initialViewLocation) {
        this.initialViewLocation = initialViewLocation;
    }

    public boolean isShowAlwaysOnTopEnabled() {
        return showAlwaysOnTop;
    }

    public void setShowAlwaysOnTop(boolean showAlwaysOnTop) {
        this.showAlwaysOnTop = showAlwaysOnTop;
    }

    public boolean isUseSystemTrayOnRecordingEnabled() {
        return useSystemTrayOnRecordingEnabled;
    }

    public void setUseSystemTrayOnRecordingEnabled(boolean useSystemTrayOnRecordingEnabled) {
        this.useSystemTrayOnRecordingEnabled = useSystemTrayOnRecordingEnabled;
    }

}
