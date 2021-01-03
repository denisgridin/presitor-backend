package ru.sfedu.course_project.api.csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class CSVPresentationMethods {
    private static final Logger log = LogManager.getLogger(CSVPresentationMethods.class);

    public CSVPresentationMethods() { }

    public static Result createPresentation(HashMap args) {
        try {
            List<Presentation> listPresentations = CSVCommonMethods.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList<Presentation>());
            if (null != args.get(ConstantsField.ID)) {
                String id = (String) args.get(ConstantsField.ID);
                boolean isIdInUse = CSVCommonMethods.isIdInUse(id, listPresentations, CollectionType.presentation);
                if (isIdInUse) {
                    return new Result(Status.error, ConstantsError.ID_IN_USE);
                }
            }

            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
            if (!optionalPresentation.isPresent()) {
                log.error(ConstantsError.ARGUMENTS_ERROR);
                return new Result(Status.error, ConstantsError.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);

            Status result = CSVCommonMethods.writeCollection(listPresentations, Presentation.class, CollectionType.presentation);

            boolean asTemplate = Boolean.parseBoolean((String) args.getOrDefault(ConstantsField.AS_TEMPLATE, String.valueOf(false)));
            if (asTemplate) {
                addPresentationInTemplate(presentation);
            }

            if (Status.success == result) {
                log.info(ConstantsSuccess.PRESENTATION_CREATE + presentation.getId());
                return new Result(Status.success, presentation.getId());
            } else {
                log.error(ConstantsError.PRESENTATION_CREATE);
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
        }
    }

    public static Result addPresentationInTemplate (Presentation presentation) {
        try {
            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(presentation.getId()));
            Optional<Presentation> optionalTemplate = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.template, args);
            if (optionalTemplate.isPresent()) {
                log.error(ConstantsError.TEMPLATE_EXISTS + presentation.getId());
                return new Result(Status.error, ConstantsError.TEMPLATE_EXISTS + presentation.getId());
            }
            ArrayList templates = (ArrayList) CSVCommonMethods.getCollection(CollectionType.template, Presentation.class)
                    .orElse(new ArrayList());
            templates.add(presentation);
            Status statusWrite = CSVCommonMethods.writeCollection(templates, Presentation.class, CollectionType.template);
            if (Status.success == statusWrite) {
                log.info(ConstantsSuccess.TEMPLATE_ADD + presentation.getId());
                return new Result(Status.success, ConstantsSuccess.TEMPLATE_ADD + presentation.getId());
            } else {
                log.error(ConstantsError.TEMPLATE_ADD + presentation.getId());
                return new Result(Status.error, ConstantsError.TEMPLATE_ADD + presentation.getId());
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.TEMPLATE_ADD + presentation.getId());
            return new Result(Status.error, ConstantsError.TEMPLATE_ADD + presentation.getId());
        }
    }

    public static Result getPresentations() {
        try {
            Optional<List> optionalPresentations = CSVCommonMethods.getCollection(CollectionType.presentation, Presentation.class);
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            if (optionalPresentations.isPresent()) {
                return new Result(Status.success, optionalPresentations.get());
            } else {
                return new Result(Status.success, new ArrayList());
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATIONS_GET);
            return new Result(Status.error, ConstantsError.PRESENTATIONS_GET);
        }
    }

    public static Result getPresentationById (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(CollectionType.presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Optional<Object> slideId = Optional.ofNullable(arguments.get(ConstantsField.SLIDE_ID));
            boolean withSlides = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_SLIDES, String.valueOf(false)));
            boolean withComments = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_COMMENTS, String.valueOf(false)));
            boolean withMarks = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_MARKS, String.valueOf(false)));
            boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_ELEMENTS, String.valueOf(false)));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, "with slide id: ", slideId.isPresent()));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, "withSlides: ", withSlides));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, "withComments: ", withComments));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, "withMarks: ", withMarks));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, "withElements: ", withElements));

            Presentation presentation = optionalPresentation.get();

            if (slideId.isPresent()) {
                HashMap paramsGetSlide = new HashMap();
                paramsGetSlide.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                paramsGetSlide.put(ConstantsField.ID, slideId.get());
                Result resultGetSlide = CSVSlideMethods.getSlideById(paramsGetSlide);
                if (Status.success == resultGetSlide.getStatus()) {
                    ArrayList slides = new ArrayList();
                    slides.add(resultGetSlide.getReturnValue());
                    presentation.setSlides(slides);

                } else {
                    return resultGetSlide;
                }
            } else if (withSlides) {
                HashMap paramsGetSlides = new HashMap();
                paramsGetSlides.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                Result resultGetSlides = CSVSlideMethods.getPresentationSlides(paramsGetSlides);
                log.debug("[getPresentationById] get presentation slides: " + paramsGetSlides.get(ConstantsField.PRESENTATION_ID));
                if (Status.success == resultGetSlides.getStatus()) {
                    presentation.setSlides((ArrayList) resultGetSlides.getReturnValue());
                } else {
                    return resultGetSlides;
                }
            }

            if (withComments) {
                HashMap paramsGetComments = new HashMap();
                paramsGetComments.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                Result resultGetComments = CSVCommentMethods.getPresentationComments(paramsGetComments);
                log.debug("[getPresentationById] get presentation comments: " + paramsGetComments.get(ConstantsField.PRESENTATION_ID));
                if (Status.success == resultGetComments.getStatus()) {
                    presentation.setComments((ArrayList) resultGetComments.getReturnValue());
                } else {
                    return resultGetComments;
                }
            }

            return optionalPresentation.isPresent() ?
                    new Result(Status.success, presentation) :
                    new Result(Status.error, ConstantsError.PRESENTATION_GET);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_GET);
            return new Result(Status.error, ConstantsError.PRESENTATION_GET);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + "id");
            } else {
                UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
                Status status = CSVCommonMethods.removeRecordById(presentation, Presentation.class, id);
                if (Status.success == status) {
                    return new Result(Status.success, ConstantsSuccess.PRESENTATION_REMOVE);
                } else {
                    return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }

    public static Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault(ConstantsField.ID, null));
            if (null == id) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            }
            boolean validId = CSVCommonMethods.getInstanceById(Presentation.class, presentation, arguments).isPresent();
            if (validId) {
                List<Presentation> list = CSVCommonMethods.getCollection(presentation, Presentation.class).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().map(el -> updatePresentationRecord(el, arguments, id)).collect(Collectors.toList());
                Status result = CSVCommonMethods.writeCollection(updatedList, Presentation.class, presentation);
                if (Status.success == result) {
                    log.info(ConstantsSuccess.PRESENTATION_UPDATE + id);
                    return new Result(Status.success, ConstantsSuccess.PRESENTATION_UPDATE + id);
                }
                return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE + id);
            } else {
                log.info(ConstantsError.PRESENTATION_NOT_FOUND + id);
                return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND + id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(ConstantsError.PRESENTATION_NOT_FOUND);
            return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
        }
    }

    public static Presentation updatePresentationRecord (Presentation presentation, HashMap arguments, UUID id) {
        if (presentation.getId().equals(id)) {
            String fillColor = (String) arguments.getOrDefault(ConstantsField.FILL_COLOR, presentation.getFillColor());
            String fontFamily = (String) arguments.getOrDefault(ConstantsField.FONT_FAMILY, presentation.getFontFamily());
            String name = (String) arguments.getOrDefault(ConstantsField.NAME, presentation.getName());
            boolean asTemplate = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.AS_TEMPLATE, String.valueOf(false)));
            ArrayList slides = (ArrayList) arguments.getOrDefault(ConstantsField.SLIDES, presentation.getSlides());
            ArrayList comments = (ArrayList) arguments.getOrDefault(ConstantsField.COMMENTS, presentation.getComments());


            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.FILL_COLOR + fillColor);
            presentation.setFillColor(fillColor);
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.FONT_FAMILY + fontFamily);
            presentation.setFontFamily(fontFamily);
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.NAME + name);
            presentation.setName(name);

//                        if (asTemplate) {
//                            addPresentationInTemplate(el);
//                        } TODO метод добавления флага шаблона
        } return presentation;
    }

}