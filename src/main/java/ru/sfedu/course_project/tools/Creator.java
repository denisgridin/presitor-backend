package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Comment;
import ru.sfedu.course_project.bean.Element;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
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
            default: {
                return new Result(Status.error, Optional.empty());
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
}
