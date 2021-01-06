package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.bean.Assessment;
import ru.sfedu.course_project.bean.Comment;
import ru.sfedu.course_project.enums.*;
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
import java.util.stream.Collectors;

public class JDBCAssessmentMethod {

    private static final Logger log = LogManager.getLogger(JDBCAssessmentMethod.class);
    public JDBCAssessmentMethod(){}

    public static Result rateByMark(HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.MARK);
            fields.add(ConstantsField.ROLE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.debug("Check presentation existence");

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }


            Statement statement = JDBCCommonMethods.setConnection();

            Optional<Assessment> optionalAssessment = (Optional<Assessment>) new Creator().create(Assessment.class, arguments).getReturnValue();

            if (!optionalAssessment.isPresent()) {
                log.error(ConstantsError.ASSESSMENT_ADD_ERROR);
                return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.assessment, optionalAssessment.get(), null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
            }
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.COMMENT_CREATE);
            return new Result(Status.success, optionalAssessment.get().getId());

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
        }
    }


    public static Result getPresentationMarks(HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.debug("Check presentation existence");

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }


            Statement statement = JDBCCommonMethods.setConnection();

            String condition = String.format(SQLQuery.CONDITION_PRESENTATION_ID, arguments.get(ConstantsField.PRESENTATION_ID));

            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.assessment, condition);
            log.debug("Query string: " + query);


            ResultSet resultSet = statement.executeQuery(query);
            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.assessment);
            JDBCCommonMethods.closeConnection();

            if (Status.error == result.getStatus()) {
                return result;
            }

            ArrayList<Assessment> assessments = (ArrayList<Assessment>) result.getReturnValue();

            UUID presentationId = UUID.fromString((String) arguments.get(ConstantsField.PRESENTATION_ID));

            HashMap marks = new HashMap();

            marks.put(String.valueOf(Mark.awful), 0);
            marks.put(String.valueOf(Mark.bed), 0);
            marks.put(String.valueOf(Mark.normal), 0);
            marks.put(String.valueOf(Mark.good), 0);
            marks.put(String.valueOf(Mark.excellent), 0);

            log.debug(marks);

            assessments.stream().forEach(el -> {
                int currentCount = (int) marks.get(String.valueOf(el.getMark()));
                log.debug(String.format("Mark %s: %s", el.getMark(), currentCount + 1));
                marks.replace(String.valueOf(el.getMark()), currentCount + 1);
            });
            log.info("Presentation marks: " + marks);
            return new Result(Status.success, marks);

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }
}
