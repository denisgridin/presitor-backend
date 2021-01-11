package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.converters.FontConverter;
import ru.sfedu.course_project.converters.LayoutConverter;
import ru.sfedu.course_project.converters.StyleConverter;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class JDBCCommonMethods {


    private static Statement statement;
    private static Connection connection;

    private static final Logger log = LogManager.getLogger(JDBCCommonMethods.class);


    private static String getFilePath () {
        try {
            String dataPath = System.getProperty("dataPath");

//            String root = System.getProperty("user.dir");
            String root = ".";
            String database = String.format("/%s/%s", ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_CATALOG), ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_NAME));
            String protocol = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_PROTOCOL);
            String path = String.format("%s/%s", root, dataPath, database).replace("\\", "/");

            log.debug("path: " + path );

            File directory = new File(path);

            log.debug("JDBC directory: " + directory);
            log.debug("JDBC directory exists: " + directory.exists());
            if (!directory.exists()){
                boolean directoriesCreated = directory.mkdirs();
                log.debug("Directories created: " + directoriesCreated);
            }

//            new File(path).createNewFile();
            String databasePath = String.format("%s%s%s", protocol, path, database);
            log.debug("databasePath: " + databasePath);
            return databasePath;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }

    public static Statement setConnection() throws SQLException, IOException {
        try {
            String databaseUser = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_USER);
            String databasePassword = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_PASSWORD);
            String databasePath = getFilePath();

            if (null == databasePath) {
                log.error(ConstantsError.CONNECTION_ERROR);
                return null;
            }

            log.info("Data base path: " + databasePath);

            Properties jdbcProperties = new Properties();
            jdbcProperties.put("user", databaseUser);
            jdbcProperties.put("password", databasePassword);
            jdbcProperties.put("v$session.program", "Presitor");


            connection = DriverManager.getConnection(databasePath, jdbcProperties);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            statement.execute(SQLQuery.CREATE_SCHEMA);
            statement.execute(SQLQuery.SET_SCHEMA);
            statement.execute(SQLQuery.CREATE_PRESENTATION_TABLE); // create table if not exist
            statement.execute(SQLQuery.CREATE_SLIDE_TABLE); // create table if not exist
            statement.execute(SQLQuery.CREATE_COMMENT_TABLE); // create table if not exist
            statement.execute(SQLQuery.CREATE_ASSESSMENT_TABLE); // create table if not exist
            statement.execute(SQLQuery.CREATE_SHAPE_TABLE); // create table if not exist
            statement.execute(SQLQuery.CREATE_CONTENT_TABLE); // create table if not exist

//            statement.execute(SQLQuery.CREATE_SCHEMA);
            return statement;
        } catch (SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.CONNECTION_ERROR);
            return null;
        }
    }

    public static void closeConnection() throws SQLException {
        connection.close();
        statement.close();
    }

    public static Result getInstance (QueryMember queryMember, String condition) {
        try {
            String query = QueryBuilder.build(Method.get, QueryMember.presentation, null, null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_GET);
            }
            ResultSet resultSet = statement.executeQuery(query);
            Result result = getInstanceFromResultSet(resultSet, queryMember);
            closeConnection();
            return result;
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.INSTANCE_GET);
            return new Result(Status.error, ConstantsError.INSTANCE_GET);
        }
    }

    public static Result getCollection (QueryMember queryMember) {
        try {
            log.debug("Get collection: " + queryMember);
            Statement statement = setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            String query = QueryBuilder.build(Method.get, queryMember, null, null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_GET);
            }
            ResultSet resultSet = statement.executeQuery(query);
            Result result = getListFromResultSet(resultSet, queryMember);

            closeConnection();

            return result;
        } catch (RuntimeException | SQLException | IOException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, Optional.empty());
        }
    }

    public static Result getInstanceFromResultSet (ResultSet resultSet, QueryMember queryMember) {
        try {
            resultSet.next();
            switch (queryMember) {
                case presentation: {
                    return parseResultSetToPresentation(resultSet);
                }
                case slide: {
                    return parseResultSetToSlide(resultSet);
                }
                case assessment: {
                    return parseResultSetToAssessment(resultSet);
                }
            }
            return new Result(Status.success, "");
        } catch (RuntimeException | SQLException e){
            log.error(e);
            return new Result(Status.error, ConstantsError.INSTANCE_GET);
        }
    }

    public static Result parseResultSetToContent (ResultSet resultSet) {
        try {
            Content content = new Content();

            String elementType = resultSet.getString(1);
            String layout = resultSet.getString(2);
            String name = resultSet.getString(3);
            String presentationId = resultSet.getString(4);
            String slideId = resultSet.getString(5);
            String text = resultSet.getString(6);
            String id = resultSet.getString(7);
            String font = resultSet.getString(8);


            Result resultParseLayout = parseLayout(layout);
            if (Status.error == resultParseLayout.getStatus()) {
                return resultParseLayout;
            }

            Layout parsedLayout = (Layout) resultParseLayout.getReturnValue();


            Result resultParseFont = parseFont(font);
            if (Status.error == resultParseFont.getStatus()) {
                return resultParseFont;
            }

            Font parsedFont = (Font) resultParseFont.getReturnValue();

            content.setElementType(ElementType.valueOf(elementType));
            content.setId(UUID.fromString(id));
            content.setText(text);
            content.setPresentationId(UUID.fromString(presentationId));
            content.setSlideId(UUID.fromString(slideId));
            content.setLayout(parsedLayout);
            content.setName(name);
            content.setFont(parsedFont);

            log.info("Content parsed: " + content);

            return new Result(Status.success, content);

        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.CONTENT_GET);
            return new Result(Status.error, ConstantsError.CONTENT_GET);
        }
    }

    public static Result parseResultSetToShape (ResultSet resultSet) {
        try {
            Shape shape = new Shape();

            String elementType = resultSet.getString(1);
            String figure = resultSet.getString(2);
            String id = resultSet.getString(3);
            String layout = resultSet.getString(4);
            String name = resultSet.getString(5);
            String presentationId = resultSet.getString(6);
            String slideId = resultSet.getString(7);
            String style = resultSet.getString(8);
            String text = resultSet.getString(9);

            Result resultParseLayout = parseLayout(layout);
            if (Status.error == resultParseLayout.getStatus()) {
                return resultParseLayout;
            }

            Layout parsedLayout = (Layout) resultParseLayout.getReturnValue();


            Result resultParseStyle = parseStyle(style);
            if (Status.error == resultParseStyle.getStatus()) {
                return resultParseStyle;
            }

            Style parsedStyle = (Style) resultParseStyle.getReturnValue();

            shape.setElementType(ElementType.valueOf(elementType));
            shape.setFigure(Figure.valueOf(figure));
            shape.setId(UUID.fromString(id));
            shape.setText(text);
            shape.setPresentationId(UUID.fromString(presentationId));
            shape.setSlideId(UUID.fromString(slideId));
            shape.setLayout(parsedLayout);
            shape.setStyle(parsedStyle);
            shape.setName(name);

            log.info("Shape parsed: " + shape);

            return new Result(Status.success, shape);

        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_GET);
            return new Result(Status.error, ConstantsError.SHAPE_GET);
        }
    }

    private static Result parseFont (String value) {
        log.debug("Parsing font: " + value);
        try {
            Result resultParse = FontConverter.convertFont(value);
            log.debug("Font convert result: " + resultParse.getReturnValue());
            return resultParse;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PARSE_LAYOUT);
            return new Result(Status.error, ConstantsError.PARSE_LAYOUT);
        }
    }

    private static Result parseLayout (String value) {
        try {
            Result resultParse = LayoutConverter.convertLayout(value);
            log.debug("Layout convert result: " + resultParse.getReturnValue());
            return resultParse;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PARSE_LAYOUT);
            return new Result(Status.error, ConstantsError.PARSE_LAYOUT);
        }
    }

    private static Result parseStyle (String value) {
        try {
            Result resultParse = StyleConverter.convertStyle(value);
            log.debug("Style convert result: " + resultParse.getReturnValue());
            return resultParse;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PARSE_STYLE);
            return new Result(Status.error, ConstantsError.PARSE_STYLE);
        }
    }

    public static Result parseResultSetToAssessment (ResultSet resultSet) {
        try {
            Assessment assessment = new Assessment();

            String id = resultSet.getString(1);
            String role = resultSet.getString(2);
            String presentationId = resultSet.getString(3);
            String mark = resultSet.getString(4);

            assessment.setId(UUID.fromString(id));
            assessment.setRole(Role.valueOf(role));
            assessment.setMark(Mark.valueOf(mark));
            assessment.setPresentationId(UUID.fromString(presentationId));

            return new Result(Status.success, assessment);

        } catch (RuntimeException | SQLException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }

    public static Result parseResultSetToComment (ResultSet resultSet) {
        try {
            Comment comment = new Comment();
            String id = resultSet.getString(1);
            String role = resultSet.getString(2);
            String datetime = resultSet.getString(3);
            String presentationId = resultSet.getString(4);
            String text = resultSet.getString(5);

            comment.setId(UUID.fromString(id));
            comment.setRole(Role.valueOf(role));
            comment.setDatetime(datetime);
            comment.setPresentationId(UUID.fromString(presentationId));
            comment.setText(text);

            log.debug(ConstantsInfo.PARSED_VALUE + comment);

            return new Result(Status.success, comment);
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SQL_ERROR);
            return new Result(Status.error, ConstantsError.SQL_ERROR);
        }
    }

    public static Result parseResultSetToSlide (ResultSet resultSet) {
        try {
            log.info(ConstantsInfo.SQL_PARSE);
            log.debug(String.format(ConstantsInfo.RESULT_SET_OF, QueryMember.slide, resultSet));
            Slide slide = new Slide();

            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            int index = resultSet.getInt(3);
            String presentationId = resultSet.getString(4);

            slide.setId(UUID.fromString(id));
            slide.setName(name);
            slide.setIndex(index);
            slide.setPresentationId(UUID.fromString(presentationId));
            log.debug(ConstantsInfo.SLIDE + slide);
            return new Result(Status.success, slide);
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SQL_ERROR);
            return new Result(Status.error, ConstantsError.SQL_ERROR);
        }
    }

    public static Result parseResultSetToPresentation (ResultSet resultSet) {
        try {
            log.info(ConstantsInfo.SQL_PARSE);
            Presentation presentation = new Presentation();
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String fillColor = resultSet.getString(3);
            String fontFamily = resultSet.getString(4);

            presentation.setId(UUID.fromString(id));
            presentation.setName(name);
            presentation.setFillColor(fillColor);
            presentation.setFontFamily(fontFamily);
            log.debug(ConstantsInfo.PRESENTATION + presentation);
            return new Result(Status.success, presentation);
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SQL_ERROR);
            return new Result(Status.error, ConstantsError.SQL_ERROR);
        }
    }

    public static Result getListFromResultSet(ResultSet resultSet, QueryMember queryMember) {
        try {
            ArrayList list = new ArrayList();
            log.debug("Result set: " + resultSet);
            resultSet.beforeFirst();
            while (resultSet.next()) {
                Result currentResult = new Result();
                switch (queryMember) {
                    case presentation: {
                        currentResult = parseResultSetToPresentation(resultSet);
                        break;
                    }
                    case slide: {
                        currentResult = parseResultSetToSlide(resultSet);
                        break;
                    }
                    case comment: {
                        currentResult = parseResultSetToComment(resultSet);
                        break;
                    }
                    case assessment: {
                        currentResult = parseResultSetToAssessment(resultSet);
                        break;
                    }
                    case shape: {
                        currentResult = parseResultSetToShape(resultSet);
                        break;
                    }
                    case content: {
                        currentResult = parseResultSetToContent(resultSet);
                        break;
                    }
                }
                if (Status.success == currentResult.getStatus()) {
                    log.info(ConstantsInfo.INSTANCE_GET + currentResult.getReturnValue());
                    list.add(currentResult.getReturnValue());
                } else {
                    log.error(ConstantsError.SQL_ERROR);
                }
            }
            return new Result(Status.success, list);
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.SQL_ERROR);
        }
    }

    public static Connection getConnection() throws IOException, SQLException {
        setConnection();
        return connection;
    }
}