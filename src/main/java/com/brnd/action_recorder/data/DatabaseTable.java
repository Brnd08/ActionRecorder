package com.brnd.action_recorder.data;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This enum stores database tables used in the app, and useful sql scripts
 */
public enum DatabaseTable {
    SETTINGS( //table used to store the app settings
            new String[]{"settings_id", "INTEGER PRIMARY KEY  UNIQUE"},
            new String[]{"always_on_top", "BOOLEAN NOT NULL DEFAULT FALSE"},
            new String[]{"initial_stage_location", "VARCHAR(30) NOT NULL DEFAULT 'CENTER'"}
    ),
    RECORDINGS(//table used to store recordings
            new String[]{"recording_id", "INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE"},
            new String[]{"recording_title", "VARCHAR(30)"},
            new String[]{"recording_date", "VARCHAR(20)"},
            new String[]{"recording_duration", "FLOAT"},
            new String[]{"recording_input_events", "BLOB"}
    );
	
    private final LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();
    private final String createTableSentence;
    private final String selectByIdSentence;
    private final String selectAllSentence;
    private final String insertNewRowSentence;
    private final String insertFirstSentence;
    private final String selectCountSentence;  
    private final String insertDefaultSentence;

    public String getSelectCountSentence() {
        return selectCountSentence;
    }

    public String getInsertFirstSentence() {
        return insertFirstSentence;
    }

    public String getCreateTableSentence() {
        return createTableSentence;
    }

    public String getInsertDefaultSentence() {
        return insertDefaultSentence;
    }

    public String getSelectByIdSentence() {
        return selectByIdSentence;
    }

    public String getSelectAllSentence() {
        return selectAllSentence;
    }

    public String getInsertNewRowSentence() {
        return insertNewRowSentence;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DatabaseTable.").append(this.name()).append('{');
        sb.append("fieldsMap=").append(fieldsMap);
        sb.append(", createTableSentence=").append(createTableSentence);
        sb.append(", selectByIdSentence=").append(selectByIdSentence);
        sb.append(", selectAllSentence=").append(selectAllSentence);
        sb.append(", insertNewRowSentence=").append(insertNewRowSentence);
        sb.append(", insertFirstSentence=").append(insertFirstSentence);
        sb.append(", selectCountSentence=").append(selectCountSentence);
        sb.append(", insertDefaultSentence=").append(insertDefaultSentence);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Initializes the constants members base on the given array of string indicating the table columns
     * @param columns Arrays indicating table columns. First index containing column name, second column type and constraints
     */
    private DatabaseTable(String[]... columns) {

    	final Logger logger = LogManager.getLogger(DatabaseTable.class);
    	
        String[] idColumn = columns[0];
        String idColumnTypeAndConstraints = idColumn[1];
        String idColumnName = columns[0][0];
        selectAllSentence = "SELECT * FROM " + this.name() + ";";
        selectByIdSentence = "SELECT * FROM " + this.name() + " WHERE " + idColumnName + " = ?;";
        selectCountSentence = "SELECT COUNT(*) FROM " + this.name() + ";";
        insertDefaultSentence = "INSERT INTO " + this.name() + " DEFAULT VALUES RETURNING "+ idColumnName +";";
        
        
        
        StringBuilder createSentence = 
                new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.name())
                .append(
                        Arrays.stream(columns).map(field -> (field[0] + " " + field[1]))
                                .collect(Collectors.joining(", ", "(", ")"))
                ) // appends column names followed by type and constraints separating them  by commas and enclose them with curly braces
                .append(";");
        createTableSentence =  createSentence.toString();

        StringBuilder insertSentence = 
                new StringBuilder("INSERT INTO " + this.name())
                .append(
                        Arrays.stream(columns).map(column -> column[0])
                        .collect(Collectors.joining(", ", "(", ")"))
                ) // appends comma separated column names enclosed between curly braces
                .append(" VALUES ")
                .append(
                        Arrays.stream(columns).map(column -> "?")
                                .collect(Collectors.joining(", ", "(", ")"))
                ) // appends a '?' sign for each column 
                .append(";");
        
        String insertSentenceString = insertSentence.toString();
        
        // if the primary key is auto-incremented removes primary key column and ? sign from the insertSentence
        if(idColumnTypeAndConstraints.contains("INTEGER PRIMARY KEY AUTOINCREMENT")){ 
            insertNewRowSentence 
                    = insertSentenceString
                            .replace(idColumnName + ", ", "")
                            .replace("(?, ", "(")
                            .replace(";",  " RETURNING "+ idColumnName +";");
        }else {
            insertNewRowSentence  = insertSentenceString;
        }
        
        insertFirstSentence = insertNewRowSentence.replace("INSERT INTO", "INSERT OR IGNORE INTO");// ignore exceptions if the row already exists

        logger.log(Level.TRACE, "Table constant {} created with following configuration: {}",
                this.name(), this);

    }

}
