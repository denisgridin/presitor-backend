package ru.sfedu.course_project.api.jdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.QueryMember;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.awt.desktop.QuitEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class JDBCPresentationMethods {
    private static final Logger log = LogManager.getLogger(JDBCPresentationMethods.class);

    public static Result createPresentation(HashMap args) {
        try {
            log.debug(ConstantsInfo.PRESENTATIONS_CREATE);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();

            if (!optionalPresentation.isPresent()) {
                log.error(ConstantsError.PRESENTATION_CREATE);
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.presentation, optionalPresentation.get(), null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }
            log.debug("Create table if not exist");
            statement.execute(SQLQuery.CREATE_PRESENTATION_TABLE);
//            statement.execute("DROP TABLE PRESENTATION");
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.PRESENTATION_CREATE);
            return new Result(Status.success, optionalPresentation.get().getId());
        } catch (IndexOutOfBoundsException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
        }
    }

    public static Result getPresentations () {
        try {
            log.debug("Attempt to get presentations");
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Result result = JDBCCommonMethods.getCollection(QueryMember.presentation);
            JDBCCommonMethods.closeConnection();
            return result;
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result getPresentationById(HashMap arguments) {
        if (null == arguments.get(ConstantsField.ID)) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
        }
        try {
            log.debug("Attempt to get presentations");
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            log.debug(ConstantsInfo.SQL_BUILD);
            String query = QueryBuilder.build(Method.get, QueryMember.presentation, null, arguments);

            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));

            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            ResultSet resultSet = statement.executeQuery(query);
            log.info("SQL exec result: " + resultSet);

            Result resultParseSet = JDBCCommonMethods.getInstanceFromResultSet(resultSet, QueryMember.presentation);
            JDBCCommonMethods.closeConnection();

            return resultParseSet;
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_NOT_FOUND);
            return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        if (null == arguments.get(ConstantsField.ID)) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            String query = QueryBuilder.build(Method.remove, QueryMember.presentation, null, arguments);
            log.debug("Query string: " + query);
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            return new Result(Status.success, ConstantsSuccess.PRESENTATION_REMOVE);
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }

    public static Result editPresentationOptions (HashMap arguments) {
        if (null == arguments.get(ConstantsField.ID)) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            Presentation presentation = (Presentation) resultGetPresentation.getReturnValue();

            String query = QueryBuilder.build(Method.update, QueryMember.presentation, presentation, arguments);
            log.debug("Query string: " + query);
            int resultRows = statement.executeUpdate(query);
            log.debug("Rows updated: " + resultRows);
            JDBCCommonMethods.closeConnection();
            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.PRESENTATION_UPDATE);
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
            }
        } catch (RuntimeException | SQLException | IOException e){
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
        }
    }
}