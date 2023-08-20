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
import com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository.RecordingMapper;
import com.github.kwhat.jnativehook.NativeInputEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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
    private static final String RECORDING_DATE_FIELD = "recording_date";
    private static final String RECORDING_DURATION_FIELD = "recording_duration";
    private static final String RECORDING_INPUT_EVENTS_FIELD = "recording_input_events";

    private static final String SELECT_RECORDING_TITLE_BY_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s = (?);",
                    RECORDING_TITLE_FIELD,
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

    public LocalDate obtainRecordingDate(int recordingId) {
        DateTimeFormatter dateFormater = Recording.DATE_FORMATER;
        logger.log(Level.ALL, "Retrieving Recording date from database. Date format: {}. Recording id: {}", dateFormater.toFormat(), recordingId);

        LocalDate recordingDate = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORDING_DATE_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String retrievedDate = resultSet.getString(1);
            recordingDate = LocalDate.parse(retrievedDate, dateFormater);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording  date, using default value: {}. Exception message: {}. Excecuted query {}",
                    recordingDate, e.getMessage(), SELECT_RECORDING_DURATION_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }
        return recordingDate;
    }

    public void updateRecordingDate(LocalDate newDate, int recordingId) {
        DateTimeFormatter dateFormatter = Recording.DATE_FORMATER;
        logger.log(
                Level.ALL, "Updating database Recording date ({}). Date format: {}. Recording id: {}",
                newDate, dateFormatter.toFormat(), recordingId
        );

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE)) {
            String dateString = newDate.format(dateFormatter);
            preparedStatement.setString(1, dateString);
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                    modifiedRows);
        } catch (SQLException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording date value. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE, e.getMessage()
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

    public LinkedHashMap<Long, NativeInputEvent> obtainInputEvents(int recordingId) {
        logger.log(Level.ALL, "Retrieving Recording input events from database. Recording id: {}", recordingId);
        LinkedHashMap<Long, NativeInputEvent> inputEvents = null;
        byte[] serializedInputEvent;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_INPUT_EVENTS_BY_ID_SENTENCE)) {
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            serializedInputEvent = resultSet.getBytes(1); // get the serialized Map in an array of bytes
            inputEvents = DataUtils.objectFromBytes(serializedInputEvent, LinkedHashMap.class); // deserializes the bytes to a LinkedHashMap object
        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve Recording input events, using default value: {}. Exception message: {}. Excecuted query {}",
                    inputEvents, e.getMessage(), SELECT_RECORDING_DURATION_BY_ID_SENTENCE
            );
            DataUtils.logSuppressedExceptions(logger, e.getSuppressed());
        }

        return inputEvents;
    }

    public void updateRecordingInputEvents(LinkedHashMap<Long, NativeInputEvent> inputEvents, int recordingId) {
        String inputEventsString = inputEvents.values().stream().map(entry -> entry.paramString()).collect(Collectors.joining("\t"));
        logger.log(Level.ALL, "Updating database Recording input events: ({}). \n Recording id: {}", inputEventsString, recordingId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE)) {
            preparedStatement.setBytes(1, DataUtils.toBytes(inputEvents)); // inserts the serialized object as a byte array
            preparedStatement.setInt(2, recordingId);
            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count", modifiedRows);
        } catch (SQLException | IOException e) {
            logger.log(
                    Level.ERROR,
                    "Could not update Recording input events value. Excecuted query {}. Exception message: {}",
                    UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE, e.getMessage()
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
            ResultSet resultSet = preparedStatement.executeQuery(); //obtains the resultset from the query execution
            retrievedRecording = RecordingMapper.mapRecordingFromResultSet(resultSet);//asign the retrievedRecording usin the Recording mapper
            logger.log(Level.ALL, "Return retrieved Recording {}", retrievedRecording);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(
                    Level.ERROR,
                    "Could not retrieve the Recording object, using default value: {}. Exception message: {}. Excecuted query {}",
                    retrievedRecording, e.getMessage(), SELECT_RECORDING_DURATION_BY_ID_SENTENCE
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
                /* prepared statement and resultset should be closed before updating recording row values to prevent sqlite Busy exception*/
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
            this.updateRecordingDate(recordingToInsert.getRecordingDate(), newRowId);
            this.updateRecordingDuration(recordingToInsert.getRecordingDuration(), newRowId);
            this.updateRecordingInputEvents(recordingToInsert.getInputEvents(), newRowId);
        }
        return newRowId;
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
         * recording by id
         * @return Recording containing the information specified in the result
         * set
         * @throws SQLException if an exception related to jdbc
         * @throws IOException if an exception occur while deserializing the
         * recording input events field content
         * @throws ClassNotFoundException if the class containing the input
         * events field can not be found
         */
        public static Recording mapRecordingFromResultSet(ResultSet resultSet) throws SQLException, IOException, ClassNotFoundException {
            try {
                resultSet.next();
            } catch (SQLException ex) {
                logger.log(Level.ERROR, "No result set rows available");
                throw ex;
            }
            int recordingId;
            LinkedHashMap<Long, NativeInputEvent> recordingInputEvents;
            String recordingTitle;
            float recordingDuration;
            LocalDate recordingDate;
            DateTimeFormatter dateFormater = Recording.DATE_FORMATER;
            try {
                recordingId = resultSet.getInt(RECORDING_ID_FIELD);
                recordingInputEvents
                        = DataUtils.objectFromBytes(
                                resultSet.getBytes(RECORDING_INPUT_EVENTS_FIELD), LinkedHashMap.class
                        );
                recordingTitle = resultSet.getString(RECORDING_TITLE_FIELD);
                recordingDuration = resultSet.getFloat(RECORDING_DURATION_FIELD);
                String retrievedDate = resultSet.getString(RECORDING_DATE_FIELD);
                recordingDate = LocalDate.parse(retrievedDate, dateFormater);
            } catch (SQLException | IOException | ClassNotFoundException ex) {
                logger.log(Level.ERROR, "Could not get recording from resultset. Exception: {}", ex.getMessage());
                throw ex;
            }
            return new Recording(recordingId, recordingInputEvents, recordingTitle, recordingDuration, recordingDate);
        }
    }
}
