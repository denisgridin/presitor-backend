package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.Assessment;
import ru.sfedu.course_project.bean.Layout;
import ru.sfedu.course_project.bean.Shape;
import ru.sfedu.course_project.bean.Style;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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

            log.debug(ConstantsInfo.PRESENTATION_CHECK);

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
            log.debug(ConstantsInfo.QUERY + query);

            if (query.isEmpty()) {
                log.error(ConstantsError.QUERY_EMPTY);
                return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
            }
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.COMMENT_CREATE);
            return new Result(Status.success, ConstantsSuccess.ASSESSMENT_CREATE);

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

            log.debug(ConstantsInfo.PRESENTATION_CHECK);

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }


            Statement statement = JDBCCommonMethods.setConnection();

            String condition = String.format(SQLQuery.CONDITION_PRESENTATION_ID, arguments.get(ConstantsField.PRESENTATION_ID));

            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.assessment, condition);
            log.debug(ConstantsInfo.QUERY + query);


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
            marks.put(String.valueOf(Mark.bad), 0);
            marks.put(String.valueOf(Mark.normal), 0);
            marks.put(String.valueOf(Mark.good), 0);
            marks.put(String.valueOf(Mark.excellent), 0);

            log.debug(marks);

            assessments.stream().forEach(el -> {
                int currentCount = (int) marks.get(String.valueOf(el.getMark()));
                log.debug(String.format("Mark %s: %s", el.getMark(), currentCount + 1));
                marks.replace(String.valueOf(el.getMark()), currentCount + 1);
            });
            log.info(ConstantsInfo.MARKS + marks);
            return new Result(Status.success, Optional.of(marks));

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }

    public static Result getMarkById (HashMap args) {
        try {

            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ID);
            elementFields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(args, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Result checkPresentationResult = presentationCheck(args);

            if (checkPresentationResult.getStatus() == Status.error) {
                return checkPresentationResult;
            }

            Statement statement = JDBCCommonMethods.setConnection();
            log.debug(ConstantsInfo.SQL_BUILD);

            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, args.get(ConstantsField.ID));
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.assessment, condition);


            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));
            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            ResultSet resultSet = statement.executeQuery(query);
            log.info(ConstantsInfo.SQL_RESULT + resultSet);
            Result resultParseSet = JDBCCommonMethods.getInstanceFromResultSet(resultSet, QueryMember.assessment);
            JDBCCommonMethods.closeConnection();

            if (resultParseSet.getStatus() == Status.success) {
                return new Result(Status.success, Optional.of(resultParseSet.getReturnValue()));
            } else {
                return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_GET_ERROR);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }

    public static Result editPresentationMark (HashMap arguments) {
        try {
            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ID);
            elementFields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Result checkPresentationResult = presentationCheck(arguments);

            if (checkPresentationResult.getStatus() == Status.error) {
                return checkPresentationResult;
            }

            log.debug(ConstantsInfo.ASSESSMENTS_GET);
            Result resultGetMark = getMarkById(arguments);
            if (resultGetMark.getStatus() == Status.error) {
                return resultGetMark;
            }

            Optional optional = (Optional) resultGetMark.getReturnValue();
            Assessment assessment = (Assessment) optional.get();

            return updateAssessment(arguments, assessment);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_UPDATE);
            return new Result(Status.error, ConstantsError.ASSESSMENT_UPDATE);
        }
    }

    private static Result updateAssessment (HashMap arguments, Assessment assessment) {
        try {

            String query = SQLQuery.PREPARED_ASSESSMENT_UPDATE;
            log.debug(ConstantsInfo.QUERY + query);
            log.debug(ConstantsInfo.ASSESSMENT_UPDATE + assessment);

            String mark = (String) arguments.getOrDefault(ConstantsField.MARK, String.valueOf(assessment.getMark()));
            String id = (String) arguments.get(ConstantsField.ID);
            String presentationId = (String) arguments.get(ConstantsField.PRESENTATION_ID);

            Connection connection = JDBCCommonMethods.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, mark);
            preparedStatement.setString(2, String.valueOf(id));
            preparedStatement.setString(3, String.valueOf(presentationId));
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                return new Result(Status.success, ConstantsSuccess.ASSESSMENT_UPDATE);
            } else {
                return new Result(Status.error, ConstantsError.ASSESSMENT_UPDATE);
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_UPDATE);
            return new Result(Status.error, ConstantsError.ASSESSMENT_UPDATE);
        }
    }

    public static Result removePresentationMarkById(HashMap arguments) {
        try {
            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ID);
            elementFields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Result checkPresentationResult = presentationCheck(arguments);

            if (checkPresentationResult.getStatus() == Status.error) {
                return checkPresentationResult;
            }

            log.debug(ConstantsInfo.ASSESSMENTS_GET);
            Result resultGetMark = getMarkById(arguments);
            if (resultGetMark.getStatus() == Status.error) {
                return resultGetMark;
            }

            String query = QueryBuilder.build(Method.remove, QueryMember.assessment, null, arguments);
            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));
            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            log.debug(ConstantsInfo.QUERY + query);

            Statement statement = JDBCCommonMethods.setConnection();

            int rowsRemoved = statement.executeUpdate(query);

            if (rowsRemoved > 0) {
                return new Result(Status.success, ConstantsSuccess.ASSESSMENT_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.ASSESSMENT_REMOVE);
            }

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_REMOVE);
            return new Result(Status.error, ConstantsError.ASSESSMENT_REMOVE);
        }
    }

    private static Result presentationCheck (HashMap arguments) {
        try {
            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            return resultGetPresentation;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_NOT_FOUND);
            return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
        }
    }
}
