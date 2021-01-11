package ru.sfedu.course_project.api.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.Comment;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.comment;
import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class CSVCommentMethods {
    public CSVCommentMethods() {}

    private static final Logger log = LogManager.getLogger(CSVCommentMethods.class);

    public static Result commentPresentation (HashMap arguments) {
        try {
            log.info("{ commentPresentation }");
            if (null == arguments.get(ConstantsField.TEXT)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "text");
            }
            if (null == arguments.get(ConstantsField.PRESENTATION_ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            HashMap params = new HashMap();
            params.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));
            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceById(Presentation.class, presentation, params);
            if (optionalPresentation.isPresent()) {
                log.info("{ commentPresentation } Comment for " + optionalPresentation.get());
                return createAndWriteComment(optionalPresentation.get(), arguments);
            } else {
                log.error(ConstantsError.PRESENTATION_NOT_FOUND + arguments.get(ConstantsField.PRESENTATION_ID));
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + arguments.get(ConstantsField.PRESENTATION_ID));
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENT_CREATE);
            return new Result(Status.error, ConstantsError.COMMENT_CREATE);
        }
    }

    public static Result createAndWriteComment (Presentation presentation, HashMap arguments) {
        log.info("{ createAndWriteComment } Comment creating");
        Result result = new Creator().create(Comment.class, arguments);
        if (Status.success == result.getStatus()) {
            Optional resultValue = (Optional) result.getReturnValue();
            Comment comment = (Comment) resultValue.get();
            log.debug("{ createAndWriteComment } Comment created: " + comment);
            log.info(ConstantsInfo.PRESENTATIONS_GET + presentation);
            Status resultWrite = writeCommentsCollection(comment);
            if (Status.success == resultWrite) {
                log.info("{ createAndWriteComment } Comment wrote");
                return new Result(Status.success, comment.getId());
            } else {
                log.error("{ createAndWriteComment } Comment not wrote");
                return new Result(Status.success, ConstantsError.UNEXPECTED_ERROR);
            }
        } else {
            return result;
        }
    }

    public static Result getPresentationComments (HashMap args) {
        try {
            log.info(ConstantsInfo.COMMENTS_GET);

            if (null == args.get(ConstantsField.PRESENTATION_ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }

            HashMap params = new HashMap();
            params.put(ConstantsField.ID, args.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPres = CSVPresentationMethods.getPresentationById(params);


            if (Status.success != resultGetPres.getStatus()) {
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + params.get(ConstantsField.ID));
            }

            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));

            Collection<Comment> comments = (ArrayList<Comment>) CSVCommonMethods.getCollection(comment, Comment.class).orElse(new ArrayList());
            ArrayList<Comment> presentationComments = (ArrayList<Comment>) comments.stream().filter(el -> {
                Comment item = el;
                return item.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.info(ConstantsSuccess.COMMENTS_GET + presentationComments);
            return new Result(Status.success, Optional.of(presentationComments));
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENTS_GET);
            return new Result(Status.error, ConstantsError.COMMENTS_GET);
        }
    }

    public static Status writeCommentsCollection (Comment comment) {
        try {
            log.info("{ writeCommentsCollection } writing comment in collection: " + comment);
            ArrayList comments = (ArrayList) CSVCommonMethods.getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            comments.add(comment);
            Status status = CSVCommonMethods.writeCollection(comments, Comment.class, CollectionType.comment);
            return status;
        } catch (RuntimeException e) {
            log.error(e);
            return Status.error;
        }
    }

    public static Result editPresentationComment (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            fields.add(ConstantsField.TEXT);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = CSVCommonMethods.getInstanceExistenceByField(comment, Comment.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalComment.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }


            Comment comment = optionalComment.get();
            comment.setText((String) arguments.get(ConstantsField.TEXT));
            Result resultUpdate = CSVCommonMethods.updateRecordInCollection(Comment.class, CollectionType.comment, comment, comment.getId());
            return resultUpdate;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENT_EDIT);
            return new Result(Status.error, ConstantsError.COMMENT_EDIT);
        }
    }

    public static Result removePresentationCommentById (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = CSVCommonMethods.getInstanceExistenceByField(comment, Comment.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalComment.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Comment comment = optionalComment.get();
            Status status = CSVCommonMethods.removeRecordById(CollectionType.comment, Comment.class, comment.getId());
            if (status == Status.success) {
                log.info(ConstantsSuccess.COMMENT_REMOVE);
                return new Result(Status.success, ConstantsSuccess.COMMENT_REMOVE + comment.getId());
            } else {
                log.error(ConstantsError.COMMENT_REMOVE);
                return new Result(Status.error, ConstantsError.COMMENT_REMOVE + comment.getId());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(ConstantsError.COMMENT_REMOVE);
            return new Result(Status.error, ConstantsError.COMMENT_REMOVE);
        }
    }
}
