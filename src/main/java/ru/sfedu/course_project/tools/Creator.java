package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Status;

import java.util.*;

public class Creator {
    public Creator () {}

    private static Logger log = LogManager.getLogger(Creator.class);

    public Optional create (Class cl, HashMap args) {
        String className = cl.getSimpleName().toLowerCase();
        switch (className) {
            case "presentation": {
                return createPresentation(args);
            }
            case "slide": {
                return createSlide(args);
            }
            default: {
                return Optional.empty();
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
            slide.setElements(new ArrayList<UUID>());
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
}
