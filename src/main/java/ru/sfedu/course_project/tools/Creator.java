package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
            presentation.setId(UUID.fromString((String) args.getOrDefault("id", defaults.get("id"))));
            presentation.setName((String) args.getOrDefault("name", defaults.get("name")));
            presentation.setFillColor((String) args.getOrDefault("fillColor", defaults.get("fillColor")));
            presentation.setFontFamily((String) args.getOrDefault("fontFamily", defaults.get("fontFamily")));
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
            slide.setName((String) args.getOrDefault("name", defaults.get("name")));
            slide.setIndex((Integer) args.get("index"));
            slide.setPresentationId(UUID.fromString((String) args.get("presentationId")));
            log.debug(SuccessConstants.ARGUMENTS_VALIDATE);
            return Optional.of(slide);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.ARGUMENTS_VALIDATE);
            return Optional.empty();
        }
    }
}
