package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.api.csv.CSVCommonMethods;
import ru.sfedu.course_project.api.csv.CSVPresentationMethods;
import ru.sfedu.course_project.api.csv.CSVSlideMethods;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import ru.sfedu.course_project.utils.ConstantsField;

import static ru.sfedu.course_project.enums.CollectionType.*;

public class DataProviderCSV implements DataProvider {
    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

    public String getName () {
        return "CSV";
    }


//    @Override
//    public <T> Status addCollectionRecord (T record, UUID id) {
//        try {
//            String collectionType = record.getClass().getName().toLowerCase();
//            List itemsList = getCollection(CollectionType.valueOf(collectionType), record.getClass()).orElse(new ArrayList());
//            itemsList.add(record);
//            Status result = writeCollection(itemsList, record.getClass());
//            if (result == Status.success) {
//                log.info(String.format("[addCollectionRecord] Item was successfully added: %s %s", collectionType, id));
//                return Status.success;
//            } else {
//                log.error("[addCollectionRecord] Unable to create presentation");
//                return Status.error;
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            log.error(e);
//            log.error("[addCollectionRecord] Unable to add collection record ");
//            return Status.error;
//        }
//    }


    ///                      Presentations section                          \\\

    @Override
    public Result createPresentation(HashMap arguments) {
        return CSVPresentationMethods.createPresentation(arguments);
    }

    @Override
    public Result getPresentations () {
        return CSVPresentationMethods.getPresentations();
    }

    @Override
    public Result getPresentationById (HashMap arguments) {
        return CSVPresentationMethods.getPresentationById(arguments);
    }

    @Override
    public Result removePresentationById (HashMap arguments) {
        return CSVPresentationMethods.removePresentationById(arguments);
    }

    @Override
    public Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        return CSVPresentationMethods.editPresentationOptions(arguments);
    }


    ///                         Slides section                          \\\

    @Override
    public Result createPresentationSlide (HashMap arguments) {
        return CSVSlideMethods.createPresentationSlide(arguments);
    }

    @Override
    public Result getPresentationSlides (HashMap arguments) {
        return CSVSlideMethods.getPresentationSlides(arguments);
    }

    @Override
    public Result getSlideById (HashMap args) {
        return CSVSlideMethods.getSlideById(args);
    }

    @Override
    public Result editPresentationSlideById (HashMap args) {
        return CSVSlideMethods.editPresentationSlideById(args);
    }

    @Override
    public Result removePresentationSlideById (HashMap arguments) {
        return CSVSlideMethods.removePresentationSlideById(arguments);
    }


    @Override
    public Result commentPresentation (HashMap arguments) {
        try {
            if (null == arguments.get("text")) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "text");
            }
            if (arguments.get(ConstantsField.PRESENTATION_ID) == null) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            HashMap params = new HashMap();
            params.put("id", arguments.get(ConstantsField.PRESENTATION_ID));
            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceById(Presentation.class, presentation, params);
            if (optionalPresentation.isPresent()) {
                Result result = new Creator().create(Comment.class, arguments);
                if (result.getStatus() == Status.success) {
                    Comment comment = (Comment) result.getReturnValue();
                    if (optionalPresentation.isPresent()) {
                        log.info(ConstantsInfo.PRESENTATIONS_GET + optionalPresentation.get().toString());
//                        Status resultAddComment = addPresentationComment(comment, optionalPresentation.get());
                        Status resultWrite = writeCommentsCollection(comment);
//                        if (resultWrite == Status.success && resultAddComment == Status.success) {
                        if (resultWrite == Status.success) {
                            return new Result(Status.success, comment.getId());
                        } else {
                            return new Result(Status.success, ConstantsError.UNEXPECTED_ERROR);
                        }
                    } else {
                        log.error(ConstantsError.PRESENTATION_NOT_FOUND);
                        return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
                    }
                } else {
                    return result;
                }
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

    @Override
    public Result getPresentationComments (HashMap args) {
        try {
            log.info(ConstantsInfo.COMMENTS_GET);

            if (args.get(ConstantsField.PRESENTATION_ID) == null) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }

            HashMap params = new HashMap();
            params.put("id", args.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPres = getPresentationById(params);


            if (resultGetPres.getStatus() != Status.success) {
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + params.get("id"));
            }

            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));

            ArrayList<Comment> comments = (ArrayList<Comment>) CSVCommonMethods.getCollection(comment, Comment.class).orElse(new ArrayList());
            ArrayList<Comment> presentationComments = (ArrayList<Comment>) comments.stream().filter(el -> {
                Comment item = el;
                return item.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.info(ConstantsSuccess.COMMENTS_GET + presentationComments);
            return new Result(Status.success, presentationComments);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENTS_GET);
            return new Result(Status.error, ConstantsError.COMMENTS_GET);
        }
    }

    public static Status writeCommentsCollection (Comment comment) {
        try {
            ArrayList comments = (ArrayList) CSVCommonMethods.getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            comments.add(comment);
            Status status = CSVCommonMethods.writeCollection(comments, Comment.class, CollectionType.comment);
            return status;
        } catch (RuntimeException e) {
            log.error(e);
            return Status.error;
        }
    }

    @Override
    public Result editPresentationComment (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add("id");
            fields.add("text");
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = CSVCommonMethods.getInstanceExistenceByField(comment, Comment.class, "id", (String) arguments.get("id"));
            if (!optionalComment.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }


            Comment comment = optionalComment.get();
            comment.setText((String) arguments.get("text"));
            Result resultUpdate = CSVCommonMethods.updateRecordInCollection(Comment.class, CollectionType.comment, comment, comment.getId());
            return resultUpdate;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENT_EDIT);
            return new Result(Status.error, ConstantsError.COMMENT_EDIT);
        }
    }

    @Override
    public Result removePresentationComment (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add("id");
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Optional<Comment> optionalComment = CSVCommonMethods.getInstanceExistenceByField(comment, Comment.class, "id", (String) arguments.get("id"));
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


    @Override
    public Result addElementInSlide (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add("slideId");
            fields.add("elementType");
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " presentationId");
            }
            Optional<Presentation> optionalSlide = CSVCommonMethods.getInstanceExistenceByField(slide, Slide.class, "id", (String) args.get("slideId"));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " slideId");
            }

            ElementType elementType = ElementType.valueOf((String) args.get("elementType"));
            log.debug("Attempt to add: " + elementType);

            switch (elementType) {
                case shape: {
                    log.info("Shape creating");
                    ArrayList shapeFields = new ArrayList();
                    shapeFields.add("figure");
                    Result isShapeArgsValid = new ArgsValidator().validate(args, fields);
                    if (isShapeArgsValid.getStatus() == Status.error) {
                        return isShapeArgsValid;
                    }
                    return createShape(args);
                }
                case content: {
                    log.info("Content creating");
                    ArrayList contentFields = new ArrayList();
                    contentFields.add("text");
                    Result isContentArgsValid = new ArgsValidator().validate(args, fields);
                    if (isContentArgsValid.getStatus() == Status.error) {
                        return isContentArgsValid;
                    }
                    return createContent(args);
                }
                default: {
                    return new Result(Status.error, ConstantsError.FIGURE_UNDEFINED);
                }
            }


        } catch (RuntimeException e) {
            return new Result(Status.error, ConstantsError.FIGURE_CREATE);
        }
    }

    public Result createContent (HashMap args) {
        try {
            Result resultCreateContent= new Creator().create(Content.class, args);
            if (resultCreateContent.getStatus() == Status.error) {
                return resultCreateContent;
            }

            Content content = (Content) resultCreateContent.getReturnValue();
            log.info("Create new content: " + content);
            ArrayList<Content> contents = (ArrayList<Content>) CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());
            contents.add(content);
            Status status = CSVCommonMethods.writeCollection(contents, Content.class, CollectionType.content);
            log.debug("Content added in collection: " + status);
            if (status == Status.success) {
                return new Result(Status.success, content);
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_CREATE);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.CONTENT_CREATE);
            return new Result(Status.error, ConstantsError.CONTENT_CREATE);
        }
    }

    public Result createShape (HashMap args) {
        try {
            Result resultCreateShape = new Creator().create(Shape.class, args);
            if (resultCreateShape.getStatus() == Status.error) {
                return resultCreateShape;
            }

            Shape shape = (Shape) resultCreateShape.getReturnValue();
            log.info("Create new shape: " + shape);
            ArrayList<Shape> shapes = (ArrayList<Shape>) CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            shapes.add(shape);
            Status status = CSVCommonMethods.writeCollection(shapes, Shape.class, CollectionType.shape);
            log.debug("Shape added in collection: " + status);
            if (status == Status.success) {
                return new Result(Status.success, shape);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_CREATE);
            }

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_CREATE);
            return new Result(Status.error, ConstantsError.SHAPE_CREATE);
        }
    }

    @Override
    public Result removeSlideElement (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add("slideId");
            fields.add("elementType");
            fields.add("id");
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " presentationId");
            }
            Optional<Presentation> optionalSlide = CSVCommonMethods.getInstanceExistenceByField(slide, Slide.class, "id", (String) args.get("slideId"));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " slideId");
            }

            ElementType elementType = ElementType.valueOf((String) args.get("elementType"));

            switch (elementType) {
                case shape: {
                    return removeShape(args);
                }
                default: {
                    return new Result(Status.error, ConstantsError.ELEMENT_NOT_FOUND + elementType);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.ELEMENT_REMOVE);
            return new Result(Status.error, ConstantsError.ELEMENT_REMOVE);
        }
    }

    public Result removeShape (HashMap args) {
        try {
            Optional<Shape> optionalShape = CSVCommonMethods.getInstanceExistenceByField(shape, Shape.class, "id", (String) args.get("id"));
            if (!optionalShape.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " shape " + args.get("id"));
            }

            UUID id = UUID.fromString((String) args.get("id"));

            List<Shape> collection = CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            List<Shape> updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
            Status writeStatus = CSVCommonMethods.writeCollection(updatedCollection, Shape.class, CollectionType.shape);
            if (writeStatus == Status.success) {
                return new Result(Status.success, ConstantsSuccess.SHAPE_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_REMOVE);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.SHAPE_REMOVE);
            return new Result(Status.error, ConstantsError.SHAPE_REMOVE);
        }
    }

    @Override
    public Result editSlideElement (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add("slideId");
            fields.add("elementType");
            fields.add("id");
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " presentationId");
            }
            Optional<Presentation> optionalSlide = CSVCommonMethods.getInstanceExistenceByField(slide, Slide.class, "id", (String) args.get("slideId"));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " slideId");
            }

            ElementType elementType = ElementType.valueOf((String) args.get("elementType"));

            switch (elementType) {
                case shape: {
                    return editShape(args);
                }
                case content: {
                    return editContent(args);
                }
                default: {
                    return new Result(Status.error, ConstantsError.ELEMENT_NOT_FOUND + elementType);
                }
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENT_EDIT);
            return new Result(Status.error, ConstantsError.ELEMENT_EDIT);
        }
    }

    public Result editShape (HashMap args) {
        try {
            Optional<Shape> optionalShape = CSVCommonMethods.getInstanceExistenceByField(shape, Shape.class, "id", (String) args.get("id"));
            if (!optionalShape.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " shape " + args.get("id"));
            }

            UUID id = UUID.fromString((String) args.get("id"));

            List<Shape> collection = CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            List<Shape> updatedCollection = collection.stream().map(el -> {
                if (el.getId().equals(id)) {
                    Result result = Helpers.editShapeBean(args, el);
                    if (result.getStatus() == Status.success) {
                        el = (Shape) result.getReturnValue();
                    } else {
                        log.error(ConstantsError.SHAPE_EDIT);
                    }
                }
                return el;
            }).collect(Collectors.toList());
            Status writeStatus = CSVCommonMethods.writeCollection(updatedCollection, Shape.class, CollectionType.shape);
            if (writeStatus == Status.success) {
                return new Result(Status.success, ConstantsSuccess.SHAPE_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_EDIT);
            }
        } catch (RuntimeException e) {
            log.error(ConstantsError.SHAPE_EDIT);
            return new Result(Status.error, ConstantsError.SHAPE_EDIT);
        }
    }
    public Result editContent (HashMap args) {
        log.debug("{editContent}");
        try {
            Optional<Shape> optionalContent = CSVCommonMethods.getInstanceExistenceByField(content, Content.class, "id", (String) args.get("id"));
            if (!optionalContent.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " content " + args.get("id"));
            }

            UUID id = UUID.fromString((String) args.get("id"));

            List<Content> collection = CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());
            List<Content> updatedCollection = collection.stream().map(el -> {
                if (el.getId().equals(id)) {
                    Result result = Helpers.editContentBean(args, el);
                    if (result.getStatus() == Status.success) {
                        el = (Content) result.getReturnValue();
                    } else {
                        log.error(ConstantsError.CONTENT_EDIT);
                    }
                }
                return el;
            }).collect(Collectors.toList());
            Status writeStatus = CSVCommonMethods.writeCollection(updatedCollection, Content.class, CollectionType.content);
            if (writeStatus == Status.success) {
                return new Result(Status.success, ConstantsSuccess.CONTENT_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_EDIT);
            }
        } catch (RuntimeException e) {
            log.error(ConstantsError.SHAPE_EDIT);
            return new Result(Status.error, ConstantsError.SHAPE_EDIT);
        }
    }

//    @Override
//    public Result removePresentationComment (HashMap arguments) {
////        try {
////
////        } catch (RuntimeException e) {
////
////        }
//    }




//    @Override
//    public Result editPresentationSlideOptionsById (HashMap arguments) {
//
//    }
}
