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

import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.data.DatabaseTable;
import com.brnd.action_recorder.data.ObjectBytesConverter;
import com.brnd.action_recorder.views.recording.Recording;
import com.github.kwhat.jnativehook.NativeInputEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static com.brnd.action_recorder.views.recording.recording_saving_view.RecordingsRepository.RecordingMapper.mapRecordingFromResultSet;

/**
 * This class has the needed functionalities to insert, select and update Recordings from the
 * database
 */
public class RecordingsRepository {

    private static Logger logger = LogManager.getLogger(RecordingsRepository.class);
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
        PreparedStatement preparedStatement = null;
        String recordingTitle;

        try {
            preparedStatement = connection.prepareStatement(SELECT_RECORDING_TITLE_BY_ID_SENTENCE);
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            recordingTitle = resultSet.getString(1);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not retrieve the recording title with following query {}", SELECT_RECORDING_TITLE_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved obtain show on top value found in database, using default value {}", "Unnamed Recording");
            recordingTitle = "Unnamed Recording";
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}",
                             "RecordingsRepository.obtainRecordingTitle(int)", ex.getMessage());
                }
            }
        }

        return recordingTitle;
    }

    public void updateRecordingTitle(String newTitle, int recordingId) {
        PreparedStatement preparedStatement = null;

        try {
            logger.log(Level.ALL, "Updateing Recording title value ({}) in database with script {}",
                     newTitle, UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);

            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);

            preparedStatement.setString(1, newTitle);
            preparedStatement.setInt(2, recordingId);

            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                     modifiedRows);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not update Initial Recording value ({}) in database with following query {}",
                     newTitle, UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}",
                             "RecordingsRepository.updateInitialRecordingTitle(String, int)", ex.getMessage());
                }
            }
        }
    }

    public LocalDate obtainRecordingDate(int recordingId) {
        PreparedStatement preparedStatement = null;
        DateTimeFormatter dateFormater = Recording.DATE_FORMATER;
        LocalDate recordingDate;

        try {
            preparedStatement = connection.prepareStatement(SELECT_RECORDING_DATE_BY_ID_SENTENCE);
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String retrievedDate = resultSet.getString(1);
            recordingDate = LocalDate.parse(retrievedDate, dateFormater);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not retrieve the recording date with following query {}", SELECT_RECORDING_DATE_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved recording date value found in database, using default value {}", "null");
            recordingDate = null;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}",
                             "RecordingsRepository.obtainRecordingTitle(int)", ex.getMessage());
                }
            }
        }

        return recordingDate;
    }

    public void updateRecordingDate(LocalDate newDate, int recordingId) {
        PreparedStatement preparedStatement = null;

        DateTimeFormatter dateFormatter = Recording.DATE_FORMATER;
        try {
            logger.log(Level.ALL, "Updating Recording date value ({}) in database with script {}, and formatter {}",
                     newDate, UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE, dateFormatter);

            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE);

            String dateString = newDate.format(dateFormatter);

            preparedStatement.setString(1, dateString);
            preparedStatement.setInt(2, recordingId);

            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                     modifiedRows);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not Recording date value ({}) in database with following query {} , and formatter {}",
                     newDate, UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE, dateFormatter);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}",
                             "RecordingsRepository.updateRecordingDate(String, int)", ex.getMessage());
                }
            }
        }
    }

    public float obtainRecordingDuration(int recordingId) {
        PreparedStatement preparedStatement = null;
        float recordingDuration;

        try {
            preparedStatement = connection.prepareStatement(SELECT_RECORDING_DURATION_BY_ID_SENTENCE);
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            recordingDuration = resultSet.getFloat(1);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not retrieve the recording duration with following query {}", SELECT_RECORDING_DURATION_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved recording duration value found in database, using default value {}", 0);
            recordingDuration = 0f;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}",
                             "RecordingsRepository.obtainRecordingDuration(int)", ex.getMessage());
                }
            }
        }

        return recordingDuration;
    }

    public void updateRecordingDuration(float duration, int recordingId) {
        PreparedStatement preparedStatement = null;

        try {
            logger.log(Level.ALL, "Updating Recording duration ({}) in database with script {}",
                     duration, UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);

            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);

            preparedStatement.setFloat(1, duration);
            preparedStatement.setInt(2, recordingId);

            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                     modifiedRows);

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not update Recording duration ({}) in database with following query {}",
                     duration, UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}",
                             "RecordingsRepository.updateInitialRecordingTitle(String, int)", ex.getMessage());
                }
            }
        }
    }

    public LinkedHashMap<Long, NativeInputEvent> obtainInputEvents(int recordingId) {
        PreparedStatement preparedStatement = null;
        LinkedHashMap<Long, NativeInputEvent> inputEvents;
        byte[] serializedInputEvent;

        try {
            preparedStatement = connection.prepareStatement(SELECT_INPUT_EVENTS_BY_ID_SENTENCE);
            preparedStatement.setInt(1, recordingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            serializedInputEvent = resultSet.getBytes(1); // get the serialized Map in an array of bytes
            inputEvents = ObjectBytesConverter.objectFromBytes(serializedInputEvent, LinkedHashMap.class); //deserializes the bytes to a LinkedHashMap object

        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(Level.ERROR, "Could not retrieve the recording duration with following query {}", SELECT_RECORDING_DURATION_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved recording duration value found in database, using default value {}", "null");
            inputEvents = null;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}",
                             "RecordingsRepository.obtainInputEvents(int)", ex.getMessage());
                }
            }
        }

        return inputEvents;
    }

    public void updateRecordingInputEvents(LinkedHashMap<Long, NativeInputEvent> inputEvents, int recordingId) {
        PreparedStatement preparedStatement = null;
        String inputEventsString = inputEvents.values().stream().map(entry -> {
            return entry.paramString();
        }).collect(Collectors.joining("\t"));
        try {
            logger.log(Level.ALL, "Updating Recording InputEvents: %n ({}) %n in database with script {}",
                     inputEventsString,
                     UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);

            preparedStatement = connection.prepareStatement(UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);

            preparedStatement.setBytes(1, ObjectBytesConverter.toBytes(inputEvents)); // inserts the serialized object as a byte array
            preparedStatement.setInt(2, recordingId);

            int modifiedRows = preparedStatement.executeUpdate();

            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count",
                     modifiedRows);

        } catch (SQLException | IOException e) {
            logger.log(Level.ERROR, "Could not set Recording InputEvents in database with following query {}",
                     UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}",
                             "RecordingsRepository.updateRecordingInputEvents(String, int)", ex.getMessage());
                }
            }
        }
    }

    public Recording getRecordingById(int recordingId) {

        PreparedStatement preparedStatement = null;
        String selectSentence = DatabaseTable.RECORDINGS.getSelectByIdSentence();
        Recording retrievedRecording = null;
        
        logger.log(Level.ALL, "Trying to obtain Recording with id {} from database with the following script: {}",
                 recordingId, selectSentence);
        
        try {
            preparedStatement = connection.prepareStatement(selectSentence);

            preparedStatement.setInt(1, recordingId);

            ResultSet resultSet = preparedStatement.executeQuery(); //obtains the resultset from the query execution

            retrievedRecording = mapRecordingFromResultSet(resultSet);//asign the retrievedRecording usin the Recording mapper

        } catch (SQLException | IOException | ClassNotFoundException e) {
            logger.log(Level.ERROR, "Could not retrieve the recording duration with following query {}", SELECT_RECORDING_DURATION_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved recording duration value found in database, using default value {}", "'null'");
            retrievedRecording = null;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}",
                             "RecordingsRepository.obtainRecordingDuration(int)", ex.getMessage());
                }
            }
        }
        logger.log(Level.ALL, "Return retrieved Recording {} database table with primary key: {}",
                 retrievedRecording, recordingId);

        return retrievedRecording;
    }

    public void insertRecording(Recording recordingToInsert) {
        PreparedStatement preparedStatement = null;
        String insertSentence = DatabaseTable.RECORDINGS.getInsertDefaultSentence();
        try {
            logger.log(Level.ALL, "Inserting new Empty row in {} database table with script {}",
                     DatabaseTable.RECORDINGS.name(),
                     insertSentence);

            preparedStatement = connection.prepareStatement(insertSentence);
            ResultSet resultSet = preparedStatement.executeQuery();

            int newRowId = resultSet.getInt(1);
            if (newRowId == 0) {
                newRowId = resultSet.getInt(RECORDING_ID_FIELD);
            }
            recordingToInsert.setId(newRowId);

            /* Need to close pstmt and rst to prevent sqlite Busy exception*/
            preparedStatement.close();
            resultSet.close();

            this.updateRecordingTitle(recordingToInsert.getRecordingTitle(), newRowId);
            this.updateRecordingDate(recordingToInsert.getRecordingDate(), newRowId);
            this.updateRecordingDuration(recordingToInsert.getRecordingDuration(), newRowId);
            this.updateRecordingInputEvents(recordingToInsert.getInputEvents(), newRowId);
            logger.log(Level.ALL, "Succesfully add a new Record with id: {} ",
                     newRowId);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not insert the Recording in database");
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}",
                             "RecordingsRepository.insertRecording(Recording)", ex.getMessage());
                }
            }
        }
    }

    /**
     * This utility class is intended to be use when saving, updating and retrieving recordings from the database
     */
    static class RecordingMapper {

        private RecordingMapper() { // To prevent class instantiation in utility classes
            throw new UnsupportedOperationException("Utility class should not be instantiated");
        }

        /**
         * Maps a Recording using the given Result set
         * @param resultSet the result set obtained after executing the select recording by id
         * @return Recording containing the information specified in the result set
         * @throws SQLException if an exception related to jdbc
         * @throws IOException if an exception occur while deserializing the recording input events field content
         * @throws ClassNotFoundException if the class containing the input events field can not be found
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
                        = ObjectBytesConverter.objectFromBytes(
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

            return new Recording(
                    recordingId,
                     recordingInputEvents,
                     recordingTitle,
                     recordingDuration,
                     recordingDate
            );
        }

    }
}
