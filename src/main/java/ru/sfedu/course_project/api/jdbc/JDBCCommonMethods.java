package ru.sfedu.course_project.api.jdbc;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JDBCCommonMethods {


    private static Statement statement;
    private static Connection connection;

    private static final Logger log = LogManager.getLogger(JDBCCommonMethods.class);

    public static Statement setConnection() throws SQLException, IOException {
        try {
            String databaseUser = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_USER);
            String databasePassword = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_PASSWORD);
            String databasePath = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_PATH);
            connection = DriverManager.getConnection(databasePath, databaseUser, databasePassword);
            statement = connection.createStatement();
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

    public static Result getCollection (CollectionType collectionType) {
        try {
            log.debug("Get collection: " + collectionType);
            Statement statement = setConnection();
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            String query = QueryBuilder.build(Method.get, CollectionType.presentation, null, null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_GET);
            }
            ResultSet resultSet = statement.executeQuery(query);
            Result result = getListFromResultSet(resultSet, collectionType);
            return result;
        } catch (RuntimeException | SQLException | IOException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, Optional.empty());
        }
    }

    public static Result getListFromResultSet(ResultSet resultSet, CollectionType collectionType) {
        try {
            switch (collectionType) {
                case presentation: {
                    return parseResultSetToPresentationList(resultSet);
                }
            }
            return new Result(Status.success, "");
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, "Unable to read result set");
        }
    }

    private static Result parseResultSetToPresentationList(ResultSet resultSet) {
        try {
            List list = new ArrayList<Presentation>();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String fillColor = resultSet.getString(3);
                String fontFamily = resultSet.getString(4);
                Presentation presentation = new Presentation();

                presentation.setId(UUID.fromString(id));
                presentation.setName(name);
                presentation.setFillColor(fillColor);
                presentation.setFontFamily(fontFamily);
                list.add(presentation);
                log.debug("Presentation: " + presentation);
            }
            return new Result(Status.success, list);
        } catch (RuntimeException | SQLException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }
}