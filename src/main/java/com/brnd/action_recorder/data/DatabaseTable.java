package com.brnd.action_recorder.data;

import static com.brnd.action_recorder.data.Database.logger;
import java.util.LinkedHashMap;
import org.apache.logging.log4j.Level;

public enum DatabaseTable {
    SETTINGS(
            new String[]{"settings_id", "INT PRIMARYKEY UNIQUE"},
            new String[]{"always_on_top", "BOOLEAN NOT NULL DEFAULT FALSE"},
            new String[]{"initial_stage_location", "VARCHAR(30) NOT NULL DEFAULT 'CENTER'"}
    );

    private final LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();
    private final String createTableSentence;
    private final String selectByIdSentence;
    private final String selectAllSentence;
    private final String insertNewRowSentence;
    private final String insertFirst;

    public String getInsertFirst() {
        return insertFirst;
    }

    public String getCreateTableSentence() {
        return createTableSentence;
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
        sb.append("createTableSentence=").append(createTableSentence);
//        sb.append(", selectByIdSentence=").append(selectByIdSentence);
//        sb.append(", selectAllSentence=").append(selectAllSentence);
//        sb.append(", insertNewRowSentence=").append(insertNewRowSentence);
        sb.append('}');
        return sb.toString();
    }

    private DatabaseTable(String[]... fields) {
        selectAllSentence = "SELECT * FROM " + this.name() + ";";
        selectByIdSentence = "SELECT * FROM " + this.name() + " WHERE " + fields[0][0] + " = ?;";

        StringBuilder insertSentence = new StringBuilder("INSERT INTO " + this.name() + " VALUES (");
        StringBuilder createSentence = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.name() + "(");
        for (String[] field : fields) {
            createSentence
                    .append(field[0])
                    .append(" ")
                    .append(field[1])
                    .append(",");

            insertSentence.append("?,");

            fieldsMap.put(field[0], field[1]);
        }

        createTableSentence = createSentence
                .deleteCharAt(createSentence.length() - 1)
                .append(");")
                .toString();
        insertNewRowSentence = insertSentence
                .deleteCharAt(insertSentence.length() - 1)
                .append(");")
                .toString();
        insertFirst = insertNewRowSentence.replace("INSERT INTO", "INSERT OR IGNORE INTO");// ignore exceptions if the row already exists
        logger.log(Level.TRACE,
                "Table {} created with following configuration: {}",
                this.name(), this.toString());

    }

}
