package ru.sfedu.course_project.api.csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.presentation;
import static ru.sfedu.course_project.enums.CollectionType.slide;

public class CSVSlideMethods {

    private static final Logger log = LogManager.getLogger(CSVSlideMethods.class);

    public CSVSlideMethods() { }

    public static Result createPresentationSlide(HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.PRESENTATION_ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put(ConstantsField.ID, String.valueOf(arguments.get(ConstantsField.PRESENTATION_ID)));
            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceById(Presentation.class, presentation, getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());

                List<Slide> slides = (List<Slide>) getPresentationSlides(arguments).getReturnValue();
                arguments.put(ConstantsField.INDEX, slides.size());
                Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments).getReturnValue();
                Slide slide = optionalSlide.orElse(new Slide());

                log.info("[createPresentationSlide] Create slide: " + slide);
                log.debug("[createPresentationSlide] For presentation: " + slide.getPresentationId());


                ArrayList allSlides = (ArrayList) CSVCommonMethods.getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
                allSlides.add(slide);
                Status statusCreateSlide = CSVCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
                if (Status.success == statusCreateSlide) {
                    log.info(ConstantsSuccess.SLIDE_CREATE + arguments.get(ConstantsField.PRESENTATION_ID));
                    return new Result(Status.success, slide.getId());
                } else {
                    log.error(ConstantsError.SLIDE_CREATE + arguments.get(ConstantsField.PRESENTATION_ID));
                    return new Result(Status.error, ConstantsError.SLIDE_CREATE + arguments.get(ConstantsField.PRESENTATION_ID));
                }
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + arguments.get(ConstantsField.PRESENTATION_ID));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.SLIDE_CREATE);
        }
    }

    public static Result getPresentationSlides (HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.PRESENTATION_ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            UUID presentationId = UUID.fromString((String) arguments.get(ConstantsField.PRESENTATION_ID));
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put(ConstantsField.ID, String.valueOf(presentationId));
            Optional<Presentation> presentation = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, getPresentationByIdParams);
            if (presentation.isPresent()) {
                Optional<List> listSlides = CSVCommonMethods.getCollection(slide, Slide.class);
                Optional<List> presentationSlides = Optional.empty();
                if (listSlides.isPresent()) {
                    log.debug("[getPresentationSlides] Attempt to find presentation slides for: " + presentationId);
                    List<Slide> list = listSlides.get();
                    presentationSlides = Optional.of(list.stream().filter(slide -> slide.getPresentationId().equals(presentationId)).collect(Collectors.toList()));
                }
                log.debug("[getPresentationSlides] Found presentation slides: " + presentationSlides.orElse(new ArrayList()));
                return new Result(Status.success, presentationSlides.orElse(new ArrayList()));
            } else {
                log.error(ConstantsError.PRESENTATION_NOT_FOUND + presentationId);
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + presentationId);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.SLIDES_GET);;
            return new Result(Status.error, ConstantsError.SLIDES_GET);
        }
    }

    public static Result getSlideById (HashMap args) {
        try {
            if (args.get(ConstantsField.PRESENTATION_ID) == null) {
                log.error(String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.PRESENTATION_ID));
                return new Result(Status.error, String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.PRESENTATION_ID));
            }
            if (null == args.get("id")) {
                log.error(String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.SLIDE_ID));
                return new Result(Status.error, String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.SLIDE_ID));
            }
            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            UUID slideId = UUID.fromString((String) args.get("id"));
            Optional<List> optionalSlides = CSVCommonMethods.getCollection(slide, Slide.class);
            if (optionalSlides.isPresent()) {
                Optional<Slide> slide = optionalSlides.get().stream().filter(el -> {
                    Slide item = (Slide) el;
                    return item.getPresentationId().equals(presentationId) && item.getId().equals(slideId);
                }).limit(1).findFirst();
                if (optionalSlides.isPresent()) {
                    return new Result(Status.success, slide.get());
                } else {
                    return new Result(Status.error, ConstantsError.SLIDE_GET);
                }
            } else {
                log.error(ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION + args.get(ConstantsField.PRESENTATION_ID));
                return new Result(Status.success, ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION + args.get(ConstantsField.PRESENTATION_ID));
            }
        } catch (RuntimeException e) {
            log.error(ConstantsError.SLIDE_GET);
            return new Result(Status.error, ConstantsError.SLIDE_GET);
        }
    }

    public static Result editPresentationSlideById (HashMap args) {
        try {
            if (null == args.get(ConstantsField.PRESENTATION_ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            if (null == args.get(ConstantsField.ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            }
            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            UUID slideId = UUID.fromString((String) args.get(ConstantsField.ID));
            Optional<Slide> optionalSlide = CSVCommonMethods.getInstanceById(Slide.class, slide, args);
            if (optionalSlide.isPresent()) {
                Optional<List> slides = CSVCommonMethods.getCollection(slide, Slide.class);
                if (slides.isPresent()) {
                    ArrayList updatedSlides = (ArrayList) slides.get().stream().map(el -> {
                        return updateSlide((Slide) el, args, slideId, presentationId);
                    }).collect(Collectors.toList());
                    Status status = CSVCommonMethods.writeCollection(updatedSlides, Slide.class, slide);
                    if (Status.success == status) {
                        log.debug(ConstantsSuccess.SLIDE_EDIT + slideId);
                        return new Result(status, ConstantsSuccess.SLIDE_EDIT + slideId);
                    } else {
                        log.debug(ConstantsError.SLIDE_EDIT + slideId);
                        return new Result(status, ConstantsError.SLIDE_EDIT + slideId);
                    }
                } else {
                    log.error(ConstantsError.SLIDES_GET);
                    return new Result(Status.error, ConstantsError.SLIDES_GET);
                }
            } else {
                log.error(ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION);
                return new Result(Status.error, ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION);
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SLIDE_EDIT);
            return new Result(Status.error, ConstantsError.SLIDE_EDIT);
        }
    }

    public static Slide updateSlide (Slide slide, HashMap args, UUID slideId, UUID presentationId) {
        if (slide.getPresentationId().equals(presentationId) && slide.getId().equals(slideId)) {
            String name = (String) args.getOrDefault(ConstantsField.NAME, slide.getName());
            String index = (String) args.getOrDefault(ConstantsField.INDEX, String.valueOf(slide.getIndex()));
            slide.setName(name);
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.NAME + name);
            slide.setIndex(Integer.valueOf(index));
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.INDEX + index);
        }
        return slide;
    }

    public static Result removePresentationSlideById (HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            }
            if (null == arguments.get(ConstantsField.PRESENTATION_ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
            String presentationId = (String) arguments.get(ConstantsField.PRESENTATION_ID);
            log.debug("[removePresentationSlideById] Attempt to remove slide: " + id);
            Status status = CSVCommonMethods.removeRecordById(slide, Slide.class, id);
            log.debug("[removePresentationSlideById] Removed from data source: " + status);
            if (Status.success == status) {
                return new Result(Status.success, ConstantsSuccess.SLIDES_REMOVE + id);
            } else {
                return new Result(Status.error, ConstantsError.SLIDE_REMOVE + id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.SLIDE_REMOVE);
        }
    }
}
