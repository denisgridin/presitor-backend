package ru.sfedu.course_project.api.xml;

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

import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.presentation;
import static ru.sfedu.course_project.enums.CollectionType.slide;

public class XMLSlideMethods {

    private static final Logger log = LogManager.getLogger(XMLSlideMethods.class);

    public XMLSlideMethods() { }

    public static Result createPresentationSlide(HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.PRESENTATION_ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.PRESENTATION_ID);
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put(ConstantsField.ID, String.valueOf(arguments.get(ConstantsField.PRESENTATION_ID)));
            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceById(presentation, getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());

                Optional optional = (Optional) getPresentationSlides(arguments).getReturnValue();
                List<Slide> slides = (List<Slide>) optional.get();
                arguments.put(ConstantsField.INDEX, slides.size());
                Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments).getReturnValue();
                Slide slide = optionalSlide.orElse(new Slide());

                log.info(ConstantsInfo.SLIDE_CREATE + slide);
                log.debug(ConstantsInfo.PRESENTATION + slide.getPresentationId());


                ArrayList allSlides = (ArrayList) XMLCommonMethods.getCollection(CollectionType.slide).orElse(new ArrayList());
                allSlides.add(slide);
                Status statusCreateSlide = XMLCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
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
            boolean withElements = Boolean.parseBoolean((String) arguments.get(ConstantsField.WITH_ELEMENTS));
            getPresentationByIdParams.put(ConstantsField.ID, String.valueOf(presentationId));
            Optional<Presentation> presentation = XMLCommonMethods.getInstanceById(CollectionType.presentation, getPresentationByIdParams);
            if (presentation.isPresent()) {
                Optional<List> listSlides = XMLCommonMethods.getCollection(slide);
                Optional<List> optionalPresentationSlides = Optional.empty();
                ArrayList<Slide> presentationSlides = new ArrayList<>();
                if (listSlides.isPresent()) {
                    log.debug(ConstantsInfo.PRESENTATIONS_GET + presentationId);
                    List<Slide> list = listSlides.get();
                    optionalPresentationSlides = Optional.of(list.stream().filter(slide -> slide.getPresentationId().equals(presentationId)).collect(Collectors.toList()));
                    presentationSlides = (ArrayList) optionalPresentationSlides.orElse(new ArrayList());

                    log.debug(ConstantsInfo.ELEMENTS_GET);
                    if (withElements) {
                        Result resultSetElements = setElementsBySlide(presentationSlides, presentationId);
                        if (Status.error == resultSetElements.getStatus()) {
                            return resultSetElements;
                        }

                        Optional optional = (Optional) resultSetElements.getReturnValue();

                        presentationSlides = (ArrayList<Slide>) optional.get();
                        log.debug(ConstantsInfo.SLIDES + presentationSlides);
                    }
                }
                log.debug(ConstantsInfo.SLIDES + presentationSlides);
                return new Result(Status.success, Optional.of(presentationSlides));
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

    public static Result setSlideElements (Slide slide) {
        try {
            log.info(ConstantsInfo.ELEMENTS + slide.getId());
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(slide.getPresentationId()));
            args.put(ConstantsField.SLIDE_ID, String.valueOf(slide.getId()));
            Result resultGetElements = XMLElementMethods.getSlideElements(args);

            log.debug(ConstantsInfo.ELEMENTS + resultGetElements.getStatus());
            log.debug(ConstantsInfo.ELEMENTS + resultGetElements.getReturnValue());
            if (Status.success == resultGetElements.getStatus()) {
                Optional optionalList = (Optional) resultGetElements.getReturnValue();
                ArrayList elements = (ArrayList) optionalList.orElse(new ArrayList());
                slide.setElements(elements);
            }
            return new Result(Status.success, slide);
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result setElementsBySlide (ArrayList slides, UUID presentationId) {
        try {
            ArrayList<Slide> updatedSlides = (ArrayList<Slide>) slides.stream().map(el -> {
                Result result = setSlideElements((Slide) el);
                if (result.getStatus() == Status.success) {
                    return result.getReturnValue();
                } else {
                    return el;
                }
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.SLIDE + slides);
            return new Result(Status.success, Optional.of(updatedSlides));
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Result getSlideById (HashMap args) {
        try {
            if (args.get(ConstantsField.PRESENTATION_ID) == null) {
                log.error(String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.PRESENTATION_ID));
                return new Result(Status.error, String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.PRESENTATION_ID));
            }
            if (null == args.get(ConstantsField.ID)) {
                log.error(String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.SLIDE_ID));
                return new Result(Status.error, String.format(ConstantsError.ARGUMENT_IS_NOT_PROVIDED, ConstantsField.SLIDE_ID));
            }
            UUID presentationId = UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID));
            UUID slideId = UUID.fromString((String) args.get(ConstantsField.ID));
            Optional<List> optionalSlides = XMLCommonMethods.getCollection(slide);
            if (optionalSlides.isPresent()) {
                Optional<Slide> slide = optionalSlides.get().stream().filter(el -> {
                    Slide item = (Slide) el;
                    return item.getPresentationId().equals(presentationId) && item.getId().equals(slideId);
                }).limit(1).findFirst();
                if (slide.isPresent()) {
                    return new Result(Status.success, slide);
                } else {
                    return new Result(Status.error, ConstantsError.SLIDE_GET);
                }
            } else {
                log.error(ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION + args.get(ConstantsField.PRESENTATION_ID));
                return new Result(Status.error, ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION + args.get(ConstantsField.PRESENTATION_ID));
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
            Optional<Slide> optionalSlide = XMLCommonMethods.getInstanceById(slide, args);
            if (optionalSlide.isPresent()) {
                Optional<List> slides = XMLCommonMethods.getCollection(slide);
                if (slides.isPresent()) {
                    ArrayList updatedSlides = (ArrayList) slides.get().stream().map(el -> {
                        return updateSlide((Slide) el, args, slideId, presentationId);
                    }).collect(Collectors.toList());
                    Status status = XMLCommonMethods.writeCollection(updatedSlides, Slide.class, slide);
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
            log.debug(ConstantsInfo.SLIDES_REMOVE + id);
            Status status = XMLCommonMethods.removeRecordById(slide, Slide.class, id);
            log.debug(ConstantsInfo.STATUS + status);
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
