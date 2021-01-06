package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.QueryMember;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


public class JDBCSlideMethods {
    private static final Logger log = LogManager.getLogger(JDBCSlideMethods.class);
    public JDBCSlideMethods(){}


    public static int getSlideIndex (HashMap args) {
        try {
            Result resultGet = getPresentationSlides(args);
            if (Status.success == resultGet.getStatus()) {
                ArrayList slides = (ArrayList) resultGet.getReturnValue();
                return slides.size() + 1;
            } else return 0;
        } catch (RuntimeException e) {
            log.error(e);
            return 0;
        }
    }

    public static Result createPresentationSlide (HashMap arguments) {
        try {
            log.debug(ConstantsInfo.SLIDE_CREATE);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            String index = (String) arguments.getOrDefault(ConstantsField.INDEX, String.valueOf(getSlideIndex(arguments)));

            arguments.put(ConstantsField.INDEX, Integer.valueOf(index));
            Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments).getReturnValue();

            if (!optionalSlide.isPresent()) {
                log.error(ConstantsError.SLIDE_CREATE);
                return new Result(Status.error, ConstantsError.SLIDE_CREATE);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.slide, optionalSlide.get(), null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.SLIDE_CREATE);
            }
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.SLIDE_CREATE);
            return new Result(Status.success, optionalSlide.get().getId());
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDE_CREATE);
            return new Result(Status.error, ConstantsError.SLIDE_CREATE);
        }
    }


    public static Result getSlideById (HashMap arguments) {
        try {
            log.debug("Attempt to get slide by id");
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            log.debug(ConstantsInfo.SQL_BUILD);

            String query = QueryBuilder.build(Method.get, QueryMember.slide, null, arguments);

            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));

            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            ResultSet resultSet = statement.executeQuery(query);
            log.info(ConstantsInfo.SQL_RESULT + resultSet);

            Result resultParseSet = JDBCCommonMethods.getInstanceFromResultSet(resultSet, QueryMember.slide);
            JDBCCommonMethods.closeConnection();

            return resultParseSet;
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDE_GET);
            return new Result(Status.error, ConstantsError.SLIDE_GET);
        }
    }


    public static Result getPresentationSlides (HashMap args) {
        try {

            Statement statement = JDBCCommonMethods.setConnection();

            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }
            String condition = String.format(SQLQuery.CONDITION_PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));

            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.slide, condition);
            log.debug("Query string: " + query);


            ResultSet resultSet = statement.executeQuery(query);
            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.slide);

            JDBCCommonMethods.closeConnection();

            return result;

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDES_GET);
            return new Result(Status.error, ConstantsError.SLIDES_GET);
        }
    }
}
