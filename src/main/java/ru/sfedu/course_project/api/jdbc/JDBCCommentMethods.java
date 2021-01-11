package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.Comment;
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

public class JDBCCommentMethods {
    private static final Logger log = LogManager.getLogger(JDBCCommentMethods.class);
    public JDBCCommentMethods(){}

    public static Result commentPresentation (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.debug(ConstantsInfo.PRESENTATION_CHECK);

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, args.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }


            Statement statement = JDBCCommonMethods.setConnection();

            Optional<Comment> optionalComment = (Optional<Comment>) new Creator().create(Comment.class, args).getReturnValue();

            if (!optionalComment.isPresent()) {
                log.error(ConstantsError.COMMENT_CREATE);
                return new Result(Status.error, ConstantsError.COMMENT_CREATE);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.comment, optionalComment.get(), null);
            log.debug(ConstantsInfo.QUERY + query);

            if (query.isEmpty()) {
                log.error(ConstantsError.QUERY_EMPTY);
                return new Result(Status.error, ConstantsError.COMMENT_CREATE);
            }
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.COMMENT_CREATE);
            return new Result(Status.success, optionalComment.get().getId());

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.COMMENT_CREATE);
            return new Result(Status.error, ConstantsError.COMMENT_CREATE);
        }
    }

    public static Result getPresentationComments (HashMap args) {
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }
            log.debug("Validation pass");

            log.debug(ConstantsInfo.PRESENTATION_CHECK);

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, args.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }

            log.info(ConstantsInfo.COMMENTS_GET);

            String conditionPresentationId = String.format(SQLQuery.CONDITION_PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));
            String condition = String.format("%s", conditionPresentationId);

            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.comment, condition);
            log.info(ConstantsInfo.QUERY + query);
            ResultSet resultSet = statement.executeQuery(query);
            log.info("Executed ");

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.comment);
            if (result.getStatus() == Status.success) {
                return new Result(Status.success, Optional.of(result.getReturnValue()));
            } else {
                return result;
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            log.error(throwables);
            return new Result(Status.error, ConstantsError.COMMENTS_GET);
        }
    }

    public static Result getPresentationCommentById (HashMap arguments) {
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            log.info(ConstantsInfo.ARGUMENTS_VALIDATE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            HashMap getPresParams = new HashMap();
            getPresParams.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));

            log.info(ConstantsInfo.PRESENTATION_CHECK);
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(getPresParams);

            if (Status.error == resultGetPresentation.getStatus()){
                log.info(ConstantsError.PRESENTATION_NOT_FOUND);
                return resultGetPresentation;
            }

            log.debug(ConstantsInfo.GET_COMMENT);
            String condition = String.format("id = '%s'", arguments.get(ConstantsField.ID));
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.comment, condition);

            log.debug(ConstantsInfo.QUERY + query);
            ResultSet resultSet = statement.executeQuery(query);
            JDBCCommonMethods.closeConnection();

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.comment);
            if (Status.success == result.getStatus()) {
                ArrayList list = (ArrayList) result.getReturnValue();
                return new Result(Status.success, Optional.of(list.get(0)));
            } else {
                return result;
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.COMMENTS_GET);
        }
    }

    public static Result editPresentationComment(HashMap arguments) {
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            log.info(ConstantsInfo.ARGUMENTS_VALIDATE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.info(ConstantsInfo.GET_COMMENT);
            Result resultGetComment = getPresentationCommentById(arguments);

            if (Status.error == resultGetComment.getStatus()){
                log.info(ConstantsError.COMMENT_NOT_FOUND);
                return resultGetComment;
            }

            Optional optional = (Optional) resultGetComment.getReturnValue();
            Comment comment = (Comment) optional.get();

            String query = QueryBuilder.build(Method.update, QueryMember.comment, comment, arguments);
            log.debug(ConstantsInfo.QUERY + query);
            int resultRows = statement.executeUpdate(query);
            log.debug(ConstantsInfo.UPDATED_ROWS + resultRows);
            JDBCCommonMethods.closeConnection();
            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.COMMENT_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.COMMENT_EDIT);
            }
        } catch (RuntimeException | SQLException | IOException e){
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
        }
    }

    public static Result removePresentationCommentById(HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            log.info(ConstantsInfo.ARGUMENTS_VALIDATE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }


            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);
            log.info(ConstantsInfo.COMMENTS_GET);
            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            String query = QueryBuilder.build(Method.remove, QueryMember.comment, null, arguments);
            log.debug(ConstantsInfo.QUERY + query);
            int resultRows = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();
            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.COMMENT_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.COMMENT_REMOVE);
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }
}
