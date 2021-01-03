package ru.sfedu.course_project.api.jdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class JDBCPresentationMethods {
    private static final Logger log = LogManager.getLogger(JDBCPresentationMethods.class);

    public static Result createPresentation(HashMap args) {
        if (args.get("id") == null) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
        }
        try {
            log.debug(ConstantsInfo.PRESENTATIONS_CREATE);
            Statement statement = JDBCCommonMethods.setConnection();
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();

            if (!optionalPresentation.isPresent()) {
                log.error(ConstantsError.PRESENTATION_CREATE);
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }

            String query = QueryBuilder.build(Method.create, CollectionType.presentation, optionalPresentation.get(), null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }
            statement.execute("CREATE TABLE IF NOT EXISTS PRESENTATION (id varchar(36), name varchar(200), fillColor varchar(200), fontFamily varchar(200))");
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
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            JDBCCommonMethods.closeConnection();
            return JDBCCommonMethods.getCollection(CollectionType.presentation);
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result getPresentationById(HashMap arguments) {
        if (arguments.get("id") == null) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
        }
        try {
            log.debug("Attempt to get presentations");
            Statement statement = JDBCCommonMethods.setConnection();
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Result result = JDBCCommonMethods.getCollection(CollectionType.presentation);
            if (result.getStatus() == Status.error) {
                return result;
            }

            UUID presentationId = UUID.fromString((String) arguments.get("id"));
            ArrayList<Presentation> list = (ArrayList<Presentation>) result.getReturnValue();
            Optional<Presentation> optionalPresentation = list.stream().filter(el -> el.getId().equals(presentationId)).findFirst();
            JDBCCommonMethods.closeConnection();
            return new Result(Status.success, optionalPresentation.orElse(null));
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_NOT_FOUND);
            return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        if (arguments.get("id") == null) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            String query = QueryBuilder.build(Method.remove, CollectionType.presentation, null, arguments);
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
        if (arguments.get("id") == null) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (statement == null) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            Presentation presentation = (Presentation) resultGetPresentation.getReturnValue();

            String query = QueryBuilder.build(Method.update, CollectionType.presentation, presentation, arguments);
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