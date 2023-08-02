package com.brnd.action_recorder.record;

import com.brnd.action_recorder.data.Database;
import com.brnd.action_recorder.data.DatabaseTable;
import org.apache.logging.log4j.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.brnd.action_recorder.data.Database.logger;
import com.brnd.action_recorder.record.utils.ObjectBytesConverter;
import com.github.kwhat.jnativehook.NativeInputEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class RecordingsRepository {

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
            = String.format("SELECT %s FROM %s WHERE %s =  (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_DATE_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s =  (?);",
            DatabaseTable.RECORDINGS.name(),
            RECORDING_DURATION_FIELD,
            RECORDING_ID_FIELD
    );
    private static final String UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE
            = String.format("SELECT %s FROM %s WHERE %s =  (?);",
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
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}"
                            , "RecordingsRepository.obtainRecordingTitle(int)", ex.getMessage());
                }
            }
        }

        return recordingTitle;
    }
    
    
    public void updateRecordingTitle(String newTitle, int recordingId) {
        PreparedStatement preparedStatement = null;

        try {
            logger.log(Level.ALL, "Updateing Recording title value ({}) in database with script {}"
                    , newTitle, UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);
            
            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);
            
            preparedStatement.setString(1, newTitle);
            preparedStatement.setInt(2, recordingId);
            
            int modifiedRows = preparedStatement.executeUpdate();
            
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);


        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not update Initial Recording value ({}) in database with following query {}"
                    ,newTitle, UPDATE_RECORDING_TITLE_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "RecordingsRepository.updateInitialRecordingTitle(String, int)", ex.getMessage());
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
            preparedStatement.setInt(1, recordingId);
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
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}"
                            , "RecordingsRepository.obtainRecordingTitle(int)", ex.getMessage());
                }
            }
        }

        return recordingDate;
    }
    
    
    
    public void updateRecordingDate(LocalDate newDate, int recordingId) {
        PreparedStatement preparedStatement = null;

        DateTimeFormatter dateFormatter = Recording.DATE_FORMATER;
        try {
            logger.log(Level.ALL, "Updating Recording date value ({}) in database with script {}, and formatter {}"
                    , newDate, UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE, dateFormatter);
            
            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE);
            
            String dateString = newDate.format(dateFormatter);
            
            preparedStatement.setString(1, dateString);
            preparedStatement.setInt(2, recordingId);
            
            int modifiedRows = preparedStatement.executeUpdate();
            
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);


        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could not Recording date value ({}) in database with following query {} , and formatter {}"
                    ,newDate, UPDATE_RECORDING_DATE_WHERE_ID_SENTENCE, dateFormatter);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "RecordingsRepository.updateRecordingDate(String, int)", ex.getMessage());
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
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}"
                            , "RecordingsRepository.obtainRecordingDuration(int)", ex.getMessage());
                }
            }
        }

        return recordingDuration;
    }
    
    public void updateRecordingDuration(float duration, int recordingId) {
        PreparedStatement preparedStatement = null;

        try {
            logger.log(Level.ALL, "Updating Recording duration ({}) in database with script {}"
                    , duration, UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);
            
            preparedStatement = connection.prepareStatement(UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);
            
            preparedStatement.setFloat(1, duration);
            preparedStatement.setInt(2, recordingId);
            
            int modifiedRows = preparedStatement.executeUpdate();
            
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);


        } catch (SQLException e) {
            logger.log(Level.ERROR, "Could Recording duration ({}) in database with following query {}"
                    ,duration, UPDATE_RECORDING_DURATION_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "RecordingsRepository.updateInitialRecordingTitle(String, int)", ex.getMessage());
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
            serializedInputEvent = resultSet.getBytes(1);
            inputEvents =  ObjectBytesConverter.objectFromBytes(serializedInputEvent, LinkedHashMap.class);

        } catch (SQLException | IOException | ClassNotFoundException  e) {
            logger.log(Level.ERROR, "Could not retrieve the recording duration with following query {}", SELECT_RECORDING_DURATION_BY_ID_SENTENCE);
            logger.log(Level.ERROR, e);
            logger.log(Level.ALL, "No saved recording duration value found in database, using default value {}", "null");
            inputEvents = null;
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Error msg: {}"
                            , "RecordingsRepository.obtainInputEvents(int)", ex.getMessage());
                }
            }
        }

        return inputEvents;
    }
    
    
    
    public void updateRecordingInputEvents(LinkedHashMap<Long, NativeInputEvent> inputEvents, int recordingId) {
        PreparedStatement preparedStatement = null;
        String inputEventsString =  inputEvents.values().stream().map(entry -> {return entry.paramString();}).collect(Collectors.joining("\t"));
        try {
            logger.log(Level.ALL, "Updating Recording InputEvents: %n ({}) %n in database with script {}"
                    , inputEventsString
                    , UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);
            
            preparedStatement = connection.prepareStatement(UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);
            
            preparedStatement.setBytes(1, ObjectBytesConverter.toBytes(inputEvents));
            preparedStatement.setInt(2, recordingId);
            
            int modifiedRows = preparedStatement.executeUpdate();
            
            logger.log(Level.ALL, "Succesfully execute script with a {} modified rows count"
                    , modifiedRows);


        } catch (SQLException | IOException  e) {
            logger.log(Level.ERROR, "Could not set Recording InputEvents in database with following query {}"
                    , UPDATE_INPUT_EVENTS_WHERE_ID_SENTENCE);
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "RecordingsRepository.updateRecordingInputEvents(String, int)", ex.getMessage());
                }
            }
        }
    }
    
    public void insertRecording (Recording recordingToInsert){
         PreparedStatement preparedStatement = null;
         String insertSentence = DatabaseTable.RECORDINGS.getInsertDefault();
        try {
            logger.log(Level.ALL, "Inserting new Empty row in {} database table with script {}"
                    , DatabaseTable.RECORDINGS
                    , insertSentence);
            
            preparedStatement = connection.prepareStatement(insertSentence);
            ResultSet resultSet = preparedStatement.executeQuery();
            int newRowId = resultSet.getInt(RECORDING_ID_FIELD);
            
            logger.log(Level.ALL, "Succesfully add a new row with {} as id"
                    , newRowId);
            
            this.updateRecordingTitle(recordingToInsert.getRecordingTitle(), newRowId);
            this.updateRecordingDate(recordingToInsert.getRecordingDate(), newRowId);
            this.updateRecordingDuration(recordingToInsert.getRecordingDuration(), newRowId);
            this.updateRecordingInputEvents(recordingToInsert.getInputEvents(), newRowId);
            


        } catch (SQLException  e) {
            logger.log(Level.ERROR, "Could not insert new Recording in database");
            logger.log(Level.ERROR, e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    logger.log(Level.ERROR, "Could not close PrepareStament on {} method. Exception msg: {}"
                            , "RecordingsRepository.insertRecording(Recording)", ex.getMessage());
                }
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
