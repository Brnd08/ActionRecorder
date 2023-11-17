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
package com.brnd.action_recorder.views.recording.recording_saving_view;

import com.brnd.action_recorder.data.DataUtils;
import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.data.DatabaseTable;
import com.brnd.action_recorder.views.recording.Recording;
import com.brnd.action_recorder.views.replay.replay_start_view.actions.ReplayableAction;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class has the needed functionalities to insert, select and update
 * Recordings from the database
 */
public class RecordingsRepository {

    private static final Logger logger = LogManager.getLogger(RecordingsRepository.class);
    private final Connection connection;

    private static final String RECORDING_ID_FIELD = "recording_id";
    private static final String RECORDING_TITLE_FIELD = "recording_title";
    private static final String RECORDING_DESCRIPTION_FIELD = "recording_description";
    private static final String RECORDING_DATE_FIELD = "recording_timestamp";
    private static final String RECORDING_DURATION_FIELD = "recording_duration";
    private static final String RECORDING_INPUT_EVENTS_FIELD = "recording_input_events";

    private static final String SELECT_RECORDING_TITLE_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
            RECORDING_TITLE_FIELD,
            DatabaseTable.RECORDINGS.name(),
            RECORDING_ID_FIELD
    );
    private static final String SELECT_RECORDING_DESCRIPTION_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
            RECORDING_DESCRIPTION_FIELD,
            DatabaseTable.RECORDINGS.name(),
            RECORDING_ID_FIELD
    );
    private static final String SELECT_RECORDING_DATE_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
            RECORDING_DATE_FIELD,
            DatabaseTable.RECORDINGS.name(),
            RECORDING_ID_FIELD
    );
    private static final String SELECT_RECORDING_DURATION_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
            RECORDING_DURATION_FIELD,
            DatabaseTable.RECORDINGS.name(),
            RECORDING_ID_FIELD
    );
    private static final String SELECT_INPUT_EVENTS_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
            RECORDING_INPUT_EVENTS_FIELD,
            DatabaseTable.RECORDINGS.name(),
            RECORDING_ID_FIELD
    );

    private static final String UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_TITLE_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_RECORDING_DESCRIPTION_WHERE_ID_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_DESCRIPTION_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_DATE_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_DURATION_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE
            = String.format("UPDATE %s SET %s = (?) WHERE %s = (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_INPUT_EVENTS_FIELD,
            RECORDING_ID_FIELD
    );

    public RecordingsRepository() {
        try {
            connection = Database.getSqliteConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String obtainRecordingTitle(int recordingId) {
        logger.log(Level.ALL, "Retrieving recording title from database. Recording id: {}", recordingId);
        String recordingTitle = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORDING_TITLE_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            recordingTitle = resultSet.getString(1);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording title, using default value: {}. Exception message: {}. Excecuted query {}",
                    recordingTitle, e.getMessage(), SELECT_RECORDING_DURATION_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingTitle;
    }

    public void updateRecordingTitle(String newTitle, int recordingId) {
        logger.log(Level.ALL, "Updating database Recording title ({}). Recording id: {}", newTitle, recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE)) {
            preparedStatement.setString(1, newTitle);
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count", modifiedRows);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording title. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }

    public String obtainRecordingDescription(int recordingId) {
        logger.log(Level.ALL, "Retrieving recording description from database. Recording id: {}", recordingId);
        String recordingDescription = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORDING_DESCRIPTION_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            recordingDescription = resultSet.getString(1);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording description, using default value: {}. Exception message: {}. Excecuted query {}",
                    recordingDescription, e.getMessage(), SELECT_RECORDING_DESCRIPTION_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingDescription;
    }

    public void updateRecordingDescription(String newDescription, int recordingId) {
        logger.log(Level.ALL, "Updating database Recording description ({}). Recording id: {}", newDescription, recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DESCRIPTION_WHERE_ID_SENTENCE)) {
            preparedStatement.setString(1, newDescription);
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count", modifiedRows);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording description. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DESCRIPTION_WHERE_ID_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }

    public LocalDate obtainRecordingDate(int recordingId) {
        logger.log(Level.ALL, "Retrieving Recording date from database. Date format: {}. Recording id: {}", Recording.DATE_TIME_FORMAT, recordingId);
        LocalDate recordingDate = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORDING_DATE_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String retrievedDate = resultSet.getString(1);
            recordingDate = LocalDate.parse(retrievedDate, DateTimeFormatter.ofPattern(Recording.DATE_TIME_FORMAT));
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording  date, using default value: {}. Exception message: {}. Excecuted query {}",
                    recordingDate, e.getMessage(), SELECT_RECORDING_DATE_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingDate;
    }

    public void updateRecordingDate(LocalDateTime newDateTime, int recordingId) {
        var dateTimeFormatter = DateTimeFormatter.ofPattern(Recording.DATE_TIME_FORMAT);
        logger.log(
                Level.ALL, "Updating database Recording date ({}). Date format: {}. Recording id: {}",
                newDateTime, Recording.DATE_TIME_FORMAT, recordingId
        );

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE)) {
            String dateString = newDateTime.format(dateTimeFormatter);
            preparedStatement.setString(1, dateString);
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                    modifiedRows);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording date value. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }

    public float obtainRecordingDuration(int recordingId) {
        float recordingDuration = 0f;
        logger.log(Level.ALL, "Retrieving recording duration from database. Recording id: {}", recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORDING_DURATION_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            recordingDuration = resultSet.getFloat(1);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording duration, using default value: {}. Exception message: {}. Excecuted query {}",
                    recordingDuration, e.getMessage(), SELECT_RECORDING_DURATION_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingDuration;
    }

    public void updateRecordingDuration(float duration, int recordingId) {
        logger.log(Level.ALL, "Updating database Recording duration ({}). Recording id: {}", duration, recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE)) {
            preparedStatement.setFloat(1, duration); // specifies recording duration
            preparedStatement.setInt(2, recordingId);// specifies recording id
            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count", modifiedRows);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording duration value. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
    }

    public Map<Long, ReplayableAction> obtainInputEvents(int recordingId) {
        logger.log(Level.ALL, "Retrieving Recording input events from database. Recording id: {}", recordingId);
        Map<Long, ReplayableAction> inputEvents = null;
        byte[] serializedInputEvent;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_INPUT_EVENTS_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            serializedInputEvent = resultSet.getBytes(1); // get the serialized Map in an array of bytes
            inputEvents = DataUtils.objectFromBytes(serializedInputEvent, LinkedHashMap.class); // deserializes the bytes to a LinkedHashMap object
        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording input events, using default value: {}. Exception message: {}. Executed query {}",
                    inputEvents, e.getMessage(), SELECT_INPUT_EVENTS_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }

        return inputEvents;
    }

    public <T extends Map<Long, ReplayableAction>  & Serializable> void updateRecordingInputEvents(T inputEvents, int recordingId) {
        String inputEventsString = inputEvents.values().stream().map(ReplayableAction::toString).collect(Collectors.joining("\t"));
        logger.log(Level.ALL, "Updating database Recording input events: ({}). \n Recording id: {}", inputEventsString, recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE)) {
            preparedStatement.setBytes(1, DataUtils.toBytes(inputEvents)); // inserts the serialized object as a byte array
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Sucessfully execute script with a {} modified rows count", modifiedRows);
        } catch (SQLException | IOException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording input events value. Executed query {}. Exception message: {}",
                    UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }

    }

    public Recording getRecordingById(int recordingId) {
        logger.log(Level.ALL, "Obtaining Recording from database. Recording id {}", recordingId);

        String selectSentence = DatabaseTable.RECORDINGS.getSelectByIdSentence();
        Recording retrievedRecording = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSentence)) {

            preparedStatement.setInt(1, recordingId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) { //obtains the resultset from the query execution
                resultSet.next(); // moves iterator to first result
                retrievedRecording = RecordingMapper.mapRecordingFromResultSet(resultSet);//asign the retrievedRecording usin the Recording mapper
            }
            logger.log(Level.ALL, "Return retrieved Recording {}", retrievedRecording);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve the Recording object, using default value: {}. Exception message: {}. Executed query {}",
                    retrievedRecording, e.getMessage(), selectSentence
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return retrievedRecording;
    }

    public int insertRecording(Recording recordingToInsert) {
        String insertSentence = DatabaseTable.RECORDINGS.getInsertDefaultSentence();
        logger.log(Level.ALL, "Inserting new Recording row in {} database table", DatabaseTable.RECORDINGS.name());
        int newRowId = 0;

        /* Adds a new row on the recordings table with default values*/
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(insertSentence);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            newRowId = resultSet.getInt(1);
            if (newRowId == 0) {
                newRowId = resultSet.getInt(RECORDING_ID_FIELD);
            }
            logger.log(Level.ALL, "A new empty Recording was inserted on database with id: {}", newRowId);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not insert the Recording. Returning default value for insertedId {}. Excecuted query {}. Exception message: {}",
                    newRowId, insertSentence, e.getMessage()
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }

        if (newRowId != 0) {
            logger.log(Level.ALL, "Updating recording database recording properties.");
            this.updateRecordingTitle(recordingToInsert.getRecordingTitle(), newRowId);
            this.updateRecordingDescription(recordingToInsert.getRecordingDescription(), newRowId);
            this.updateRecordingDate(recordingToInsert.getRecordingDateTime(), newRowId);
            this.updateRecordingDuration(recordingToInsert.getRecordingDuration(), newRowId);
            this.updateRecordingInputEvents((LinkedHashMap<Long, ReplayableAction>) recordingToInsert.getInputEvents(), newRowId);
        }
        return newRowId;
    }

    public List<Recording> getAllRecordings() {
        logger.log(Level.ALL, "Obtaining all Recordings from database. Recording id");

        var recordingsList = new ArrayList<Recording>();
        String selectAllScript = DatabaseTable.RECORDINGS.getSelectAllSentence();

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(selectAllScript);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                recordingsList.add(RecordingMapper.mapRecordingFromResultSet(resultSet));
            }
            StringBuilder retrievedRecordings = new StringBuilder();
            for (Recording recording : recordingsList) {
                retrievedRecordings.append("\n ").append(recording.toShortString().replaceAll("\n", ""));
            }
            logger.log(Level.ALL, "Returning retrieved Recordings {}", retrievedRecordings);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve the Recording List, using default value: {}. Exception message: {}. Executed query {}",
                    recordingsList, e.getMessage(), selectAllScript
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingsList;
    }

    /**
     * This utility class is intended to be use when saving, updating and
     * retrieving recordings from the database
     */
    static class RecordingMapper {

        private RecordingMapper() { // To prevent class instantiation in utility classes
            throw new UnsupportedOperationException("Utility class should not be instantiated");
        }

        /**
         * Maps a Recording using the given Result set
         *
         * @param resultSet the result set obtained after executing the select
         *                  recording by id
         * @return Recording containing the information specified in the result
         * set
         * @throws SQLException           if an exception related to jdbc
         * @throws IOException            if an exception occur while deserializing the
         *                                recording input events field content
         * @throws ClassNotFoundException if the class containing the input
         *                                events field can not be found
         */
        public static Recording mapRecordingFromResultSet(ResultSet resultSet) throws SQLException, IOException, ClassNotFoundException {

            Recording mappedRecording;
            var recordingInputEvents = DataUtils.objectFromBytes(
                    resultSet.getBytes(RECORDING_INPUT_EVENTS_FIELD), LinkedHashMap.class
            );
            var recordingDuration = resultSet.getFloat(RECORDING_DURATION_FIELD);
            var retrievedDateTime = resultSet.getString(RECORDING_DATE_FIELD);
            var recordingTitle = resultSet.getString(RECORDING_TITLE_FIELD);
            var recordingDescription = resultSet.getString(RECORDING_DESCRIPTION_FIELD);
            var recordingId = resultSet.getInt(RECORDING_ID_FIELD);
            mappedRecording = new Recording(
                    recordingId,
                    recordingInputEvents,
                    recordingTitle,
                    recordingDescription,
                    recordingDuration,
                    LocalDateTime.parse(retrievedDateTime, DateTimeFormatter.ofPattern(Recording.DATE_TIME_FORMAT))
            );
            return mappedRecording;
        }
    }
}
