package ru.sfedu.course_project.api.jdbc;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.QueryMember;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
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

            String root = System.getProperty("user.dir");
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
            statement = connection.createStatement();;
            statement.execute(SQLQuery.CREATE_SCHEMA);
            statement.execute(SQLQuery.SET_SCHEMA);

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
            statement.execute(SQLQuery.CREATE_PRESENTATION_TABLE); // create table if not exist
            ResultSet resultSet = statement.executeQuery(query);
            return getListFromResultSet(resultSet, queryMember);
        } catch (RuntimeException | SQLException | IOException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, Optional.empty());
        }
    }

    public static Result getInstanceFromResultSet (ResultSet resultSet, QueryMember queryMember) {
        try {
            switch (queryMember) {
                case presentation: {
                    return parseResultSetToPresentation(resultSet);
                }
            }
            return new Result(Status.success, "");
        } catch (RuntimeException e){
            log.error(e);
            return new Result(Status.error, ConstantsError.INSTANCE_GET);
        }
    }

    public static Result parseResultSetToPresentation (ResultSet resultSet) {
        try {
            if (resultSet.next()) {
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
                log.debug("Presentation: " + presentation);
                return new Result(Status.success, presentation);
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_GET);
            }
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SQL_ERROR);
            return new Result(Status.error, ConstantsError.SQL_ERROR);
        }
    }

    public static Result getListFromResultSet(ResultSet resultSet, QueryMember queryMember) {
        try {
            ArrayList list = new ArrayList();
            while (resultSet.next()) {
                Result currentResult = parseResultSetToPresentation(resultSet);
                if (Status.success == currentResult.getStatus()) {
                    log.info(ConstantsInfo.PRESENTATIONS_GET + currentResult.getReturnValue());
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
}