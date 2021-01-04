package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.utils.ConstantsField;

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
            case "assessment": {
                return createAssessment(args);
            }
            default: {
                log.error("Unable to create instance");
                return new Result(Status.error, "Unable to create instance");
            }
        }
    }

    private Result createAssessment (HashMap args) {
        try {
            UUID id = UUID.fromString((String) args.getOrDefault(ConstantsField.ID, String.valueOf(UUID.randomUUID())));
            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            Mark mark = Mark.valueOf((String) args.get(ConstantsField.MARK));
            Role role = Role.valueOf((String) args.getOrDefault(ConstantsField.ROLE, String.valueOf(Role.guest)));
            Assessment assessment = new Assessment();


            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));
            assessment.setId(id);

            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.PRESENTATION_ID, presentationId));
            assessment.setPresentationId(presentationId);

            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.MARK, mark));
            assessment.setMark(mark);

            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ROLE, role));
            assessment.setRole(role);

            log.info("Assessment created: " + assessment);
            return new Result(Status.success, assessment);

        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("Unable to create Assessment");
            return new Result(Status.error, ConstantsError.ASSESSMENT_CREATE_ERROR);
        }
    }

    private Optional<Presentation> createPresentation (HashMap args) {
        Map defaults = Constants.DEFAULT_PRESENTATION;
        Presentation presentation = new Presentation();
        try {
            UUID id = UUID.fromString((String) args.getOrDefault(ConstantsField.ID, defaults.get(ConstantsField.ID)));
            String name = (String) args.getOrDefault(ConstantsField.NAME, defaults.get(ConstantsField.NAME));
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
            log.debug(ConstantsSuccess.ARGUMENTS_VALIDATE);
            return Optional.of(presentation);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ARGUMENTS_VALIDATE);
            return Optional.empty();
        }
    }

    private Optional<Slide> createSlide (HashMap args) {
        Map defaults = Constants.DEFAULT_SLIDE;
        Slide slide = new Slide();
        try {
            log.info("[createSlide] Arguments: " + args.entrySet());
            log.info("[createSlide] Default slide options: " + defaults.entrySet());
            slide.setId(UUID.fromString((String) args.getOrDefault(ConstantsField.ID, defaults.get(ConstantsField.ID))));
            log.debug("[createSlide] Set id");
            slide.setName((String) args.getOrDefault(ConstantsField.NAME, defaults.get(ConstantsField.NAME)));
            log.debug("[createSlide] Set name");
            slide.setIndex((Integer) args.get("index"));
            log.debug("[createSlide] Set index");
            slide.setPresentationId(UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID)));
            log.debug("[createSlide] Set presentation id");
            slide.setElements(new ArrayList<Element>());
            log.debug("[createSlide] Set elements");
            log.debug(slide.toString());
            log.debug(ConstantsSuccess.ARGUMENTS_VALIDATE);
            return Optional.of(slide);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ARGUMENTS_VALIDATE);
            return Optional.empty();
        }
    }

    private Result createComment (HashMap args) {
        try {
            Comment comment = new Comment();

            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            UUID id = UUID.randomUUID();
            String text = (String) args.get(ConstantsField.TEXT);
            Role role = Role.valueOf((String)args.get("role"));

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String datetime = dtf.format(now);

            comment.setPresentationId(presentationId);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.PRESENTATION_ID, presentationId));

            comment.setId(id);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));

            comment.setText(text);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.TEXT, text));

            comment.setRole(role);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "role", role));

            comment.setDatetime(datetime);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, "datetime", datetime));

            return new Result(Status.success, comment);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENT_CREATE);
            return new Result(Status.error, ConstantsError.COMMENT_CREATE);
        }
    }

    private Result createShape (HashMap args) {
        try {

            HashMap defaultsElement = (HashMap) Constants.DEFAULT_ELEMENT;
            HashMap defaultsShape = (HashMap) Constants.DEFAULT_SHAPE;

            Shape shape = new Shape();

            String text = (String) args.getOrDefault(ConstantsField.TEXT, "");
            shape.setText(text);

            shape.setSlideId(UUID.fromString((String) args.get(ConstantsField.SLIDE_ID)));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.SLIDE_ID, args.get(ConstantsField.SLIDE_ID)));

            shape.setPresentationId(UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID)));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID)));

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));
            shape.setElementType(elementType);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ELEMENT_TYPE, elementType));

            Figure figure = Figure.valueOf((String) args.get(ConstantsField.FIGURE));
            shape.setFigure(figure);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.FIGURE, figure));

            Style style = Constants.DEFAULT_STYLE(args);
            shape.setStyle(style);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.STYLE, style));

            Layout layout = Constants.DEFAULT_LAYOUT(args);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.LAYOUT, layout));
            shape.setLayout(layout);

            UUID id = UUID.fromString((String) args.getOrDefault(ConstantsField.ID, String.valueOf(defaultsElement.get(ConstantsField.ID))));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));
            shape.setId(id);

            String name = (String) args.getOrDefault(ConstantsField.NAME, defaultsShape.get(ConstantsField.NAME));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));
            shape.setName(name);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.NAME, name));

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
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.FONT, font));

            String name = (String) args.getOrDefault(ConstantsField.NAME, defaultsContent.get(ConstantsField.NAME));
            content.setName(name);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.NAME, name));

            String text = (String) args.getOrDefault(ConstantsField.TEXT, defaultsContent.get(ConstantsField.TEXT));
            content.setText(text);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.TEXT, text));

            Layout layout = Constants.DEFAULT_LAYOUT(args);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.LAYOUT, layout));
            content.setLayout(layout);

            content.setSlideId(UUID.fromString((String) args.get(ConstantsField.SLIDE_ID)));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.SLIDE_ID, content.getSlideId()));

            content.setPresentationId(UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID)));
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID)));

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));
            content.setElementType(elementType);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ELEMENT_TYPE, elementType));

            UUID id = UUID.fromString((String) args.getOrDefault(ConstantsField.ID, String.valueOf(defaultsElement.get(ConstantsField.ID))));
            content.setId(id);
            log.debug(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));

            log.info("[Creator] Content created: " + content);

            return new Result(Status.success, content);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.success, ConstantsError.CONTENT_CREATE);
        }
    }
}
