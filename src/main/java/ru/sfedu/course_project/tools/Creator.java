package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.Role;
import ru.sfedu.course_project.enums.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Creator {
    public Creator () {}

    private static Logger log = LogManager.getLogger(Creator.class);

    public Result create (Class cl, HashMap args) {
        String className = cl.getSimpleName().toLowerCase();
        switch (className) {
            case "presentation": {
                Optional presentation = createPresentation(args);
                Status status = presentation.isPresent() ? Status.success : Status.error;
                return new Result(status, presentation);
            }
            case "slide": {
                Optional slide = createSlide(args);
                Status status = slide.isPresent() ? Status.success : Status.error;
                return new Result(status, slide);
            }
            case "comment": {
                return createComment(args);
            }
            case "shape": {
                return createShape(args);
            }
            case "content": {
                return createContent(args);
            }
            default: {
                log.error("Unable to create instance");
                return new Result(Status.error, "Unable to create instance");
            }
        }
    }

    private Optional<Presentation> createPresentation (HashMap args) {
        Map defaults = Constants.DEFAULT_PRESENTATION;
        Presentation presentation = new Presentation();
        try {
            UUID id = UUID.fromString((String) args.getOrDefault("id", defaults.get("id")));
            String name = (String) args.getOrDefault("name", defaults.get("name"));
            String fillColor = (String) args.getOrDefault("fillColor", defaults.get("fillColor"));
            String fontFamily = (String) args.getOrDefault("fontFamily", defaults.get("fontFamily"));
            ArrayList slides = new ArrayList();
            ArrayList comments = new ArrayList();
            ArrayList marks = new ArrayList();
            presentation.setId(id);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " id " + id);
            presentation.setName(name);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " name " + name);
            presentation.setFillColor(fillColor);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " fillColor " + fillColor);
            presentation.setFontFamily(fontFamily);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " fontFamily " + fontFamily);
            presentation.setSlides(slides);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " slides " + slides);
            presentation.setComments(comments);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " comments " + comments);
            presentation.setMarks(marks);
            log.debug("[createPresentation] " + ConstantsInfo.FIELD_SET + " marks " + marks);
            log.debug(SuccessConstants.ARGUMENTS_VALIDATE);
            return Optional.of(presentation);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.ARGUMENTS_VALIDATE);
            return Optional.empty();
        }
    }

    private Optional<Slide> createSlide (HashMap args) {
        Map defaults = Constants.DEFAULT_SLIDE;
        Slide slide = new Slide();
        try {
            log.info("[createSlide] Arguments: " + args.entrySet());
            log.info("[createSlide] Default slide options: " + defaults.entrySet());
            slide.setId(UUID.fromString((String) args.getOrDefault("id", defaults.get("id"))));
            log.debug("[createSlide] Set id");
            slide.setName((String) args.getOrDefault("name", defaults.get("name")));
            log.debug("[createSlide] Set name");
            slide.setIndex((Integer) args.get("index"));
            log.debug("[createSlide] Set index");
            slide.setPresentationId(UUID.fromString((String) args.get("presentationId")));
            log.debug("[createSlide] Set presentation id");
            slide.setElements(new ArrayList<Element>());
            log.debug("[createSlide] Set elements");
            log.debug(slide.toString());
            log.debug(SuccessConstants.ARGUMENTS_VALIDATE);
            return Optional.of(slide);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.ARGUMENTS_VALIDATE);
            return Optional.empty();
        }
    }

    private Result createComment (HashMap args) {
        try {
            Comment comment = new Comment();

            UUID presentationId = UUID.fromString((String) args.get("presentationId"));
            UUID id = UUID.randomUUID();
            String text = (String) args.get("text");
            Role role = Role.valueOf((String)args.get("role"));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String datetime = dtf.format(now);

            comment.setPresentationId(presentationId);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "presentationId", presentationId));

            comment.setId(id);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "id", id));

            comment.setText(text);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "text", text));

            comment.setRole(role);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "role", role));

            comment.setDatetime(datetime);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "datetime", datetime));

            return new Result(Status.success, comment);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.COMMENT_CREATE);
            return new Result(Status.error, ErrorConstants.COMMENT_CREATE);
        }
    }

    private Result createShape (HashMap args) {
        try {

            HashMap defaultsElement = (HashMap) Constants.DEFAULT_ELEMENT;
            HashMap defaultsShape = (HashMap) Constants.DEFAULT_SHAPE;

            Shape shape = new Shape();

            shape.setSlideId(UUID.fromString((String) args.get("slideId")));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "slideId", args.get("slideId")));

            shape.setPresentationId(UUID.fromString((String) args.get("presentationId")));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "presentationId", args.get("presentationId")));

            ElementType elementType = ElementType.valueOf((String) args.get("elementType"));
            shape.setElementType(elementType);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "elementType", elementType));

            Figure figure = Figure.valueOf((String) args.get("figure"));
            shape.setFigure(figure);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "figure", figure));

            Style style = Constants.DEFAULT_STYLE(args);
            shape.setStyle(style);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "style", style));

            Layout layout = Constants.DEFAULT_LAYOUT(args);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "layout", layout));
            shape.setLayout(layout);

            UUID id = UUID.fromString((String) args.getOrDefault("id", String.valueOf(defaultsElement.get("id"))));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "id", id));
            shape.setId(id);

            String name = (String) args.getOrDefault("name", defaultsShape.get("name"));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "id", id));
            shape.setName(name);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "name", name));

            log.info("Created: " + shape);
            return new Result(Status.success, shape);

        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("Unable to create shape");
            return new Result(Status.error, "Unable to create shape");
        }
    }

    public Result createContent (HashMap args) {
        try {
            HashMap defaultsElement = (HashMap) Constants.DEFAULT_ELEMENT;
            HashMap defaultsContent = (HashMap) Constants.DEFAULT_CONTENT;

            Content content = new Content();

            Font font = Constants.DEFAULT_FONT(args);
            content.setFont(font);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "font", font));

            String name = (String) args.getOrDefault("name", defaultsContent.get("name"));
            content.setName(name);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "name", name));

            String text = (String) args.getOrDefault("text", defaultsContent.get("text"));
            content.setText(text);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "text", text));

            Layout layout = Constants.DEFAULT_LAYOUT(args);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "layout", layout));
            content.setLayout(layout);

            content.setSlideId(UUID.fromString((String) args.get("slideId")));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "slideId", content.getSlideId()));

            content.setPresentationId(UUID.fromString((String) args.get("presentationId")));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "presentationId", args.get("presentationId")));

            ElementType elementType = ElementType.valueOf((String) args.get("elementType"));
            content.setElementType(elementType);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "elementType", elementType));

            UUID id = UUID.fromString((String) args.getOrDefault("id", String.valueOf(defaultsElement.get("id"))));
            content.setId(id);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "id", id));

            log.info("[Creator] Content created: " + content);

            return new Result(Status.success, content);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.success, ErrorConstants.CONTENT_CREATE);
        }
    }
}
