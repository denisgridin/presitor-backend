package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.api.xml.XMLElementMethods;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.ElementType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Helpers;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.*;
import static ru.sfedu.course_project.enums.CollectionType.content;

public class XMLElementMethods {
    public XMLElementMethods() { }

    private static final Logger log = LogManager.getLogger(XMLElementMethods.class);

    public static Result addElementInSlide (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            fields.add(ConstantsField.ELEMENT_TYPE);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            Optional<Presentation> optionalSlide = XMLCommonMethods.getInstanceExistenceByField(slide, Slide.class, ConstantsField.ID, (String) args.get(ConstantsField.SLIDE_ID));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.SLIDE_ID);
            }

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));
            log.debug("Attempt to add: " + elementType);

            switch (elementType) {
                case shape: {
                    log.info("Shape creating");
                    ArrayList shapeFields = new ArrayList();
                    shapeFields.add(ConstantsField.FIGURE);
                    Result isShapeArgsValid = new ArgsValidator().validate(args, fields);
                    if (Status.error == isShapeArgsValid.getStatus()) {
                        return isShapeArgsValid;
                    }
                    return createShape(args);
                }
                case content: {
                    log.info("Content creating");
                    ArrayList contentFields = new ArrayList();
                    contentFields.add(ConstantsField.TEXT);
                    Result isContentArgsValid = new ArgsValidator().validate(args, fields);
                    if (Status.error == isContentArgsValid.getStatus()) {
                        return isContentArgsValid;
                    }
                    return createContent(args);
                }
                default: {
                    return new Result(Status.error, ConstantsError.FIGURE_UNDEFINED);
                }
            }


        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.FIGURE_CREATE);
            return new Result(Status.error, ConstantsError.FIGURE_CREATE);
        }
    }

    public static Result createContent (HashMap args) {
        try {
            Result resultCreateContent= new Creator().create(Content.class, args);
            if (Status.error == resultCreateContent.getStatus()) {
                return resultCreateContent;
            }

            Content content = (Content) resultCreateContent.getReturnValue();
            log.info("Create new content: " + content);
            ArrayList<Content> contents = (ArrayList<Content>) XMLCommonMethods.getCollection(CollectionType.content).orElse(new ArrayList());
            contents.add(content);
            Status status = XMLCommonMethods.writeCollection(contents, Content.class, CollectionType.content);
            log.debug("Content added in collection: " + status);
            if (Status.success == status) {
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

    public static Result createShape (HashMap args) {
        try {
            Result resultCreateShape = new Creator().create(Shape.class, args);
            if (Status.error == resultCreateShape.getStatus()) {
                return resultCreateShape;
            }

            Shape shape = (Shape) resultCreateShape.getReturnValue();
            log.info("Create new shape: " + shape);
            ArrayList<Shape> shapes = (ArrayList<Shape>) XMLCommonMethods.getCollection(CollectionType.shape).orElse(new ArrayList());
            log.debug("Shapes: " + shapes);
            shapes.add(shape);
            Status status = XMLCommonMethods.writeCollection(shapes, Shape.class, CollectionType.shape);
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

    public static Result removeSlideElement (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            fields.add(ConstantsField.ELEMENT_TYPE);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            Optional<Presentation> optionalSlide = XMLCommonMethods.getInstanceExistenceByField(slide, Slide.class, ConstantsField.ID, (String) args.get(ConstantsField.SLIDE_ID));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.SLIDE_ID);
            }

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));

            switch (elementType) {
                case shape: {
                    return removeShape(args);
                }
                case content: {
                    return removeContent(args);
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

    public static Result removeShape (HashMap args) {
        try {
            Optional<Shape> optionalShape = XMLCommonMethods.getInstanceExistenceByField(shape, Shape.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
            if (!optionalShape.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " shape " + args.get(ConstantsField.ID));
            }

            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));

            List<Shape> collection = XMLCommonMethods.getCollection(CollectionType.shape).orElse(new ArrayList());
            List<Shape> updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
            Status writeStatus = XMLCommonMethods.writeCollection(updatedCollection, Shape.class, CollectionType.shape);
            if (Status.success == writeStatus) {
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

    public static Result removeContent (HashMap args) {
        try {
            Optional<Shape> optionalContent = XMLCommonMethods.getInstanceExistenceByField(content, Content.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
            if (!optionalContent.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " content " + args.get(ConstantsField.ID));
            }

            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));

            List<Shape> collection = XMLCommonMethods.getCollection(CollectionType.shape).orElse(new ArrayList());
            List<Shape> updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
            Status writeStatus = XMLCommonMethods.writeCollection(updatedCollection, Shape.class, CollectionType.shape);
            if (Status.success == writeStatus) {
                return new Result(Status.success, ConstantsSuccess.CONTENT_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_REMOVE);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.CONTENT_REMOVE);
            return new Result(Status.error, ConstantsError.CONTENT_REMOVE);
        }
    }

    public static Result editSlideElement (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            fields.add(ConstantsField.ELEMENT_TYPE);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            Optional<Presentation> optionalSlide = XMLCommonMethods.getInstanceExistenceByField(slide, Slide.class, ConstantsField.ID, (String) args.get(ConstantsField.SLIDE_ID));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.SLIDE_ID);
            }

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));

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

    public static Result editShape (HashMap args) {
        try {
            Optional<Shape> optionalShape = XMLCommonMethods.getInstanceExistenceByField(shape, Shape.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
            if (!optionalShape.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " shape " + args.get(ConstantsField.ID));
            }

            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));

            List<Shape> collection = XMLCommonMethods.getCollection(CollectionType.shape).orElse(new ArrayList());
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
            Status writeStatus = XMLCommonMethods.writeCollection(updatedCollection, Shape.class, CollectionType.shape);
            if (Status.success == writeStatus) {
                return new Result(Status.success, ConstantsSuccess.SHAPE_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_EDIT);
            }
        } catch (RuntimeException e) {
            log.error(ConstantsError.SHAPE_EDIT);
            return new Result(Status.error, ConstantsError.SHAPE_EDIT);
        }
    }

    public static Result editContent (HashMap args) {
        log.debug("{editContent}");
        try {
            Optional<Shape> optionalContent = XMLCommonMethods.getInstanceExistenceByField(content, Content.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
            if (!optionalContent.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + " content " + args.get(ConstantsField.ID));
            }

            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));

            List<Content> collection = XMLCommonMethods.getCollection(CollectionType.content).orElse(new ArrayList());
            List<Content> updatedCollection = collection.stream().map(el -> {
                if (el.getId().equals(id)) {
                    Result result = Helpers.editContentBean(args, el);
                    if (Status.success == result.getStatus()) {
                        el = (Content) result.getReturnValue();
                    } else {
                        log.error(ConstantsError.CONTENT_EDIT);
                    }
                }
                return el;
            }).collect(Collectors.toList());
            Status writeStatus = XMLCommonMethods.writeCollection(updatedCollection, Content.class, CollectionType.content);
            if (Status.success == writeStatus) {
                return new Result(Status.success, ConstantsSuccess.CONTENT_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_EDIT);
            }
        } catch (RuntimeException e) {
            log.error(ConstantsError.SHAPE_EDIT);
            return new Result(Status.error, ConstantsError.SHAPE_EDIT);
        }
    }

    public static Result getSlideElementById (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            fields.add(ConstantsField.ID);
            fields.add(ConstantsField.ELEMENT_TYPE);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            Optional<Presentation> optionalSlide = XMLCommonMethods.getInstanceExistenceByField(slide, Slide.class, ConstantsField.ID, (String) args.get(ConstantsField.SLIDE_ID));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.SLIDE_ID);
            }

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));
            log.debug("Attempt to get: " + elementType);

            switch (elementType) {
                case shape: {
                    Optional<Shape> optionalElement = XMLCommonMethods.getInstanceExistenceByField(shape, Shape.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
                    if (!optionalSlide.isPresent()) {
                        return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.ID);
                    }
                    return new Result(Status.success, optionalElement.get());
                }
                case content: {
                    Optional<Content> optionalElement = XMLCommonMethods.getInstanceExistenceByField(content, Content.class, ConstantsField.ID, (String) args.get(ConstantsField.ID));
                    if (!optionalSlide.isPresent()) {
                        return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.ID);
                    }
                    return new Result(Status.success, optionalElement.get());
                }
                default: {
                    return new Result(Status.error, ConstantsError.ELEMENT_NOT_FOUND);
                }
            }

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Result getSlideElements (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }

            log.info("Presentation found: " +  optionalPresentation.get());

            Optional<Presentation> optionalSlide = XMLCommonMethods.getInstanceExistenceByField(slide, Slide.class, ConstantsField.ID, (String) args.get(ConstantsField.SLIDE_ID));
            if (!optionalSlide.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.SLIDE_ID);
            }
            log.info("Slide found: " +  optionalSlide.get());

            log.debug("Set slide elements");
            ArrayList elements = new ArrayList();
            List shapes = XMLCommonMethods.getCollection(shape).orElse(new ArrayList());
            List contents = XMLCommonMethods.getCollection(content).orElse(new ArrayList());

            elements.addAll(shapes);
            elements.addAll(contents);

            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            UUID slideId = UUID.fromString((String) args.get(ConstantsField.SLIDE_ID));

            ArrayList slideElements = (ArrayList) elements.stream().filter(el -> {
                Element item = (Element) el;
                return item.getPresentationId().equals(presentationId) && item.getSlideId().equals(slideId);
            }).collect(Collectors.toList());

            log.info("Slides found: " + slideElements);

            return new Result(Status.success, slideElements);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }
}