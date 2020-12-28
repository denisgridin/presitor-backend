package ru.sfedu.course_project;

public class SQLQuery {
    public static final String RECORD_INSERT = "INSERT INTO PRESITOR.PUBLIC.%s %s VALUES %s";
    public static final String RECORD_GET = "SELECT * FROM PRESITOR.PUBLIC.%s";
    public static final String RECORD_REMOVE = "DELETE FROM PRESITOR.PUBLIC.%s WHERE %s";
    public static final String RECORD_UPDATE = "UPDATE PRESITOR.PUBLIC.%s SET %s WHERE %s";

    public static final String PRESENTATION_VALUES_SET = "name = '%s', fillColor = '%s', fontFamily = '%s'";

    public static final String CONDITION_ITEM_ID = "id = '%s'";
}
