package com.brnd.action_recorder.views.settings_view;

import com.brnd.action_recorder.views.utils.StageLocation;

/**
 * This class is used to store Settings properties for the app
 */
public class Settings {
    public static final Settings DEFAULT_SETTINGS = new Settings(StageLocation.CENTER, true);
    private StageLocation initialViewLocation;
    private boolean showAlwaysOnTop;

    public Settings(StageLocation initialViewLocation, boolean showAlwaysOnTop) {
        this.initialViewLocation = initialViewLocation;
        this.showAlwaysOnTop = showAlwaysOnTop;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Settings{");
        sb.append("initialViewLocation=").append(initialViewLocation);
        sb.append(", showAlwaysOnTop=").append(showAlwaysOnTop);
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

}
