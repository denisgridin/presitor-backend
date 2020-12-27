package ru.sfedu.course_project.api.xml;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.api.csv.CSVCommonMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class XMLPresentationMethods {
    private static final Logger log = LogManager.getLogger(XMLPresentationMethods.class);
    public static Result createPresentation(HashMap args) {
        try {
            List<Presentation> listPresentations = XMLCommonMethods.getCollection(presentation).orElse(new ArrayList<Presentation>());
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (XMLCommonMethods.isIdInUse(id, listPresentations, presentation)) {
                    return new Result(Status.error, ErrorConstants.ID_IN_USE);
                };
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
            if (!optionalPresentation.isPresent()) {
                log.error(ErrorConstants.ARGUMENTS_ERROR);
                return new Result(Status.error, ErrorConstants.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);
            Status result = XMLCommonMethods.writeCollection(listPresentations, Presentation.class, CollectionType.presentation);

//            Boolean asTemplate = Boolean.valueOf( (String) args.getOrDefault("asTemplate", "false"));
//            if (asTemplate) {
//                addPresentationInTemplate(presentation);
//            }

            if (result == Status.success) {
                log.info(SuccessConstants.PRESENTATION_CREATE + presentation.getId());
                return new Result(Status.success, presentation.getId());
            } else {
                log.error(ErrorConstants.PRESENTATION_CREATE);
                return new Result(Status.error, ErrorConstants.PRESENTATION_CREATE);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATION_CREATE);
            return new Result(Status.error, ErrorConstants.PRESENTATION_CREATE);
        }
    }

    public static Result getPresentations () {
        try {
            Optional<List> optionalPresentations = XMLCommonMethods.getCollection(presentation);
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            return optionalPresentations.map(list -> new Result(Status.success, list)).orElseGet(() -> new Result(Status.success, new ArrayList()));
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATIONS_GET);
            return new Result(Status.error, ErrorConstants.PRESENTATIONS_GET);
        }
    }

    public static Result getPresentationById (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add("id");
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get("id"));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ErrorConstants.INSTANCE_NOT_FOUND);
            }

            Optional<Object> slideId = Optional.ofNullable(arguments.get("slideId"));
            boolean withSlides = Boolean.parseBoolean((String) arguments.getOrDefault("withSlides", "false"));
            boolean withComments = Boolean.parseBoolean((String) arguments.getOrDefault("withComments", "false"));
            boolean withMarks = Boolean.parseBoolean((String) arguments.getOrDefault("withMarks", "false"));
            boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault("withElements", "false"));
            log.info("Get presentation: with slide id: " + slideId.isPresent());
            log.info("Get presentation: withSlides: " + withSlides);
            log.info("Get presentation: withComments: " + withComments);
            log.info("Get presentation: withMarks: " + withMarks);
            log.info("Get presentation: withElements: " + withElements);

            Presentation presentation = optionalPresentation.get();


//            if (slideId.isPresent()) {
//                HashMap paramsGetSlide = new HashMap();
//                paramsGetSlide.put("presentationId", arguments.get("id"));
//                paramsGetSlide.put("id", slideId.get());
//                Result resultGetSlide = this.getSlideById(paramsGetSlide);
//                if (resultGetSlide.getStatus() == Status.success) {
//                    ArrayList slides = new ArrayList();
//                    slides.add(resultGetSlide.getReturnValue());
//                    presentation.setSlides(slides);
//
//                } else {
//                    return resultGetSlide;
//                }
//            } else if (withSlides) {
//                HashMap paramsGetSlides = new HashMap();
//                paramsGetSlides.put("presentationId", arguments.get("id"));
//                Result resultGetSlides = this.getPresentationSlides(paramsGetSlides);
//                log.debug("[getPresentationById] get presentation slides: " + paramsGetSlides.get("presentationId"));
//                if (resultGetSlides.getStatus() == Status.success) {
//                    presentation.setSlides((ArrayList) resultGetSlides.getReturnValue());
//                } else {
//                    return resultGetSlides;
//                }
//            }

//            if (withComments) {
//                HashMap paramsGetComments = new HashMap();
//                paramsGetComments.put("presentationId", arguments.get("id"));
//                Result resultGetComments = this.getPresentationComments(paramsGetComments);
//                log.debug("[getPresentationById] get presentation comments: " + paramsGetComments.get("presentationId"));
//                if (resultGetComments.getStatus() == Status.success) {
//                    presentation.setComments((ArrayList) resultGetComments.getReturnValue());
//                } else {
//                    return resultGetComments;
//                }
//            }

            return optionalPresentation.isPresent() ?
                    new Result(Status.success, presentation) :
                    new Result(Status.error, ErrorConstants.PRESENTATION_GET);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ErrorConstants.PRESENTATION_GET);
            return new Result(Status.error, ErrorConstants.PRESENTATION_GET);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        try {
            if (arguments.get("id") == null) {
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            } else {
                UUID id = UUID.fromString((String) arguments.get("id"));
                Status status = XMLCommonMethods.removeRecordById(presentation, Presentation.class, id);
                if (status == Status.success) {
                    return new Result(Status.success, SuccessConstants.PRESENTATION_REMOVE);
                } else {
                    return new Result(Status.error, ErrorConstants.PRESENTATION_REMOVE);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ErrorConstants.PRESENTATION_REMOVE);
        }
    }

    public static Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault("id", null));
            if (id == null) {
                log.error(ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
                return new Result(Status.error, ErrorConstants.ARGUMENT_IS_NOT_PROVIDED + "id");
            }
            boolean validId = XMLCommonMethods.getInstanceById(presentation, arguments).isPresent();
            if (validId) {
                List<Presentation> list = XMLCommonMethods.getCollection(presentation).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().peek(el -> {
                    if (el.getId().equals(id)) {
                        String fillColor = (String) arguments.getOrDefault("fillColor", el.getFillColor());
                        String fontFamily = (String) arguments.getOrDefault("fontFamily", el.getFontFamily());
                        String name = (String) arguments.getOrDefault("name", el.getName());
//                        boolean asTemplate = Boolean.parseBoolean((String) arguments.getOrDefault("asTemplate", "false"));
                        log.debug(ConstantsInfo.FIELD_EDIT + "fillColor " + fillColor);
                        el.setFillColor(fillColor); // TODO вынести названия полей в const
                        log.debug(ConstantsInfo.FIELD_EDIT + "fontFamily " + fontFamily);
                        el.setFontFamily(fontFamily);
                        log.debug(ConstantsInfo.FIELD_EDIT + "name " + name);
                        el.setName(name);
//                        if (asTemplate) {
//                            addPresentationInTemplate(el);
//                        }
                    }
                }).collect(Collectors.toList());
                Status result = XMLCommonMethods.writeCollection(updatedList, Presentation.class, presentation);
                if (result == Status.success) {
                    log.info(SuccessConstants.PRESENTATION_UPDATE + id);
                    return new Result(Status.success, SuccessConstants.PRESENTATION_UPDATE + id);
                }
                return new Result(Status.error, ErrorConstants.PRESENTATION_UPDATE + id);
            } else {
                log.info("[editPresentationOptions] Unable to find presentation: " + id);
                return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND + id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Unable to edit presentation options");
            return new Result(Status.error, ErrorConstants.PRESENTATION_NOT_FOUND);
        }
    }
}
