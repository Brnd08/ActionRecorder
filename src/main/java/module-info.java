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