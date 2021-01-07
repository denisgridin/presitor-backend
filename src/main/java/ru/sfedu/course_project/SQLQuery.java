package ru.sfedu.course_project;

public class SQLQuery {
    public static final String RECORD_INSERT = "INSERT INTO %s %s VALUES %s";
    public static final String RECORD_GET = "SELECT * FROM %s";
    public static final String RECORD_REMOVE = "DELETE FROM %s WHERE %s";
    public static final String RECORD_UPDATE = "UPDATE %s SET %s WHERE %s";

    public static final String RECORD_GET_WITH_CONDITION = "SELECT * FROM %s WHERE %s";

    public static final String PRESENTATION_VALUES_SET = "name = '%s', fillColor = '%s', fontFamily = '%s'";
    public static final String SLIDE_VALUES_SET = "name = '%s', index = '%s'";
    public static final String COMMENT_VALUES_SET = "id = '%s', role = '%s', datetime = '%s', presentationId = '%s', text = '%s'";

    public static final String CONDITION_ITEM_ID = "id = '%s'";
    public static final String CONDITION_SLIDE_ID = "slideId = '%s'";
    public static final String CONDITION_PRESENTATION_ID = "presentationId = '%s'";


    public static final String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS PUBLIC;";
    public static final String SET_SCHEMA = "SET SCHEMA PUBLIC;";


    public static final String CREATE_PRESENTATION_TABLE = "CREATE TABLE IF NOT EXISTS PRESENTATION (id varchar(36), name varchar(200), fillColor varchar(200), fontFamily varchar(200))";
    public static final String CREATE_SLIDE_TABLE = "CREATE TABLE IF NOT EXISTS SLIDE (id varchar(36), name varchar(200), index int, presentationId varchar(36))";
    public static final String CREATE_COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS COMMENT (id varchar(36), role varchar(200), datetime varchar(50), presentationId varchar(36), text varchar(200))";
    public static final String CREATE_ASSESSMENT_TABLE = "CREATE TABLE IF NOT EXISTS ASSESSMENT (id varchar(36), role varchar(200), presentationId varchar(36), mark varchar(20))";
    public static final String CREATE_SHAPE_TABLE = "CREATE TABLE IF NOT EXISTS SHAPE (elementType varchar(20), figure varchar(20), id varchar(36), layout varchar(200), name varchar(100), presentationId varchar(36), slideId varchar(36), style varchar(200), text varchar(500))";
}
