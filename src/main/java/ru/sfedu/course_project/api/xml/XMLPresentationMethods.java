package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.ElementType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class XMLPresentationMethods {
    private static final Logger log = LogManager.getLogger(XMLPresentationMethods.class);

    public static Result buildPresentationFromTemplate (Presentation template) {
        try {
            UUID id = UUID.randomUUID();
            String name = String.format(Constants.TEMPLATE_NAME, Constants.DEFAULT_PRESENTATION.get(ConstantsField.NAME), template.getName());
            String fillColor = template.getFillColor();
            String fontFamily = template.getFontFamily();

            log.info("Build presentation from template");

            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(id));
            args.put(ConstantsField.NAME, name);
            args.put(ConstantsField.FILL_COLOR, fillColor);
            args.put(ConstantsField.FONT_FAMILY, fontFamily);

            Result resultCreatePresentation = createPresentation(args);

            if (Status.error == resultCreatePresentation.getStatus()) {
                return resultCreatePresentation;
            }

            log.info("Presentation created from template");

            UUID presentationId = id;

            ArrayList presentationShapes = new ArrayList();
            ArrayList presentationContents = new ArrayList();
            ArrayList presentationSlides = new ArrayList();

            ArrayList templateSlides = template.getSlides();
            templateSlides.stream().forEach(el -> {
                Slide slide = (Slide) el;
                UUID slideId = UUID.randomUUID();
                slide.setId(slideId);
                slide.setPresentationId(presentationId);
                presentationSlides.add(slide);


                if (slide.getElements() != null) {
                    log.info("Write new presentation elements from slide: " + slide);
                    ArrayList elements = (ArrayList) slide.getElements().stream().peek(item -> item.setId(UUID.randomUUID())).collect(Collectors.toList());

                    elements.stream().forEach(item -> {
                        Element element = (Element) item;
                        element.setId(UUID.randomUUID());
                        element.setPresentationId(presentationId);
                        element.setSlideId(slideId);
                        ElementType elementType = element.getElementType();
                        switch (elementType) {
                            case shape: {
                                presentationShapes.add(element);
                                break;
                            }
                            case content: {
                                presentationContents.add(element);
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    });
                }
            });

            log.debug("Presentation slides: " + presentationSlides);
            log.debug("Presentation shapes: " + presentationShapes);
            log.debug("Presentation contents: " + presentationContents);

            ArrayList allSlides = (ArrayList) XMLCommonMethods.getCollection(CollectionType.slide).orElse(new ArrayList());
            ArrayList allShapes = (ArrayList) XMLCommonMethods.getCollection(CollectionType.shape).orElse(new ArrayList());
            ArrayList allContents = (ArrayList) XMLCommonMethods.getCollection(CollectionType.content).orElse(new ArrayList());

            allSlides.addAll(presentationSlides);
            allShapes.addAll(presentationShapes);
            allContents.addAll(presentationContents);

            Status statusWriteSlides = XMLCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
            log.info("Slides wrote");

            Status statusWriteShapes = XMLCommonMethods.writeCollection(allShapes, Shape.class, CollectionType.shape);
            log.info("Shapes wrote");

            Status statusWriteContents = XMLCommonMethods.writeCollection(allContents, Content.class, CollectionType.content);
            log.info("Contents wrote");

            return new Result(Status.success, id);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
        }
    }

    public static Result createPresentationFromTemplate (HashMap args) {
        try {
            log.info(ConstantsInfo.TEMPLATE_SEARCH);
            HashMap params = new HashMap();
            params.put(ConstantsField.ID, String.valueOf(args.get(ConstantsField.TEMPLATE_ID)));
            params.put(ConstantsField.WITH_SLIDES, Constants.TRUE_VALUE);
            params.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);
            Result resultGetTemplate = getPresentationById(params);

            if (Status.error == resultGetTemplate.getStatus()) {
                return resultGetTemplate;
            }

            Optional optional = (Optional) resultGetTemplate.getReturnValue();
            Presentation template = (Presentation)  optional.get();
            log.debug(ConstantsInfo.TEMPLATE_FOUND + template);

            return buildPresentationFromTemplate(template);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
        }
    }

    public static Result createPresentation(HashMap args) {
        try {

            String templateId = (String) args.get(ConstantsField.TEMPLATE_ID);
            if (null != templateId) {
                return createPresentationFromTemplate(args);
            }


            List<Presentation> listPresentations = XMLCommonMethods.getCollection(presentation).orElse(new ArrayList<Presentation>());
            if (args.get(ConstantsField.ID) != null) {
                String id = (String) args.get(ConstantsField.ID);
                if (XMLCommonMethods.isIdInUse(id, listPresentations, presentation)) {
                    return new Result(Status.error, ConstantsError.ID_IN_USE);
                };
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
            if (!optionalPresentation.isPresent()) {
                log.error(ConstantsError.ARGUMENTS_ERROR);
                return new Result(Status.error, ConstantsError.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);
            Status result = XMLCommonMethods.writeCollection(listPresentations, Presentation.class, CollectionType.presentation);


            if (result == Status.success) {
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

    public static Result getPresentations () {
        try {
            Optional<List> optionalPresentations = XMLCommonMethods.getCollection(presentation);
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            return new Result(Status.success, optionalPresentations);
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
            if (isArgsValid.getStatus() == Status.error) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = XMLCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, "id", (String) arguments.get("id"));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            boolean withSlides = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_SLIDES, "false"));
            boolean withComments = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_COMMENTS, "false"));
            boolean withMarks = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_MARKS, "false"));
            boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_ELEMENTS, "false"));
            log.info(ConstantsField.WITH_SLIDES + withSlides);
            log.info(ConstantsField.WITH_COMMENTS + withComments);
            log.info(ConstantsField.WITH_MARKS + withMarks);
            log.info(ConstantsField.WITH_ELEMENTS + withElements);

            Presentation presentation = optionalPresentation.get();


            if (withSlides) {
                HashMap paramsGetSlides = new HashMap();
                paramsGetSlides.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                paramsGetSlides.put(ConstantsField.WITH_ELEMENTS, arguments.get(ConstantsField.WITH_ELEMENTS));
                Result resultGetSlides = XMLSlideMethods.getPresentationSlides(paramsGetSlides);
                log.debug(ConstantsInfo.SLIDES_GET + paramsGetSlides.get(ConstantsField.PRESENTATION_ID));
                if (resultGetSlides.getStatus() == Status.success) {
                    Optional optional = (Optional) resultGetSlides.getReturnValue();
                    presentation.setSlides((ArrayList) optional.get());
                } else {
                    return resultGetSlides;
                }
            }

            if (withComments) {
                HashMap paramsGetComments = new HashMap();
                paramsGetComments.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                Result resultGetComments = XMLCommentMethods.getPresentationComments(paramsGetComments);
                log.debug(ConstantsField.COMMENTS + paramsGetComments.get(ConstantsField.PRESENTATION_ID));
                if (resultGetComments.getStatus() == Status.success) {
                    Optional optional = (Optional) resultGetComments.getReturnValue();
                    presentation.setComments((ArrayList) optional.get());
                } else {
                    return resultGetComments;
                }
            }
            if (withMarks) {
                HashMap paramsGetMarks = new HashMap();
                paramsGetMarks.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                log.debug(ConstantsInfo.ASSESSMENTS_GET + paramsGetMarks.get(ConstantsField.ID));

                Result resultGetMarks = XMLAssessmentMethods.getPresentationMarks(paramsGetMarks);

                if (Status.success == resultGetMarks.getStatus()) {
                    Optional optional = (Optional) resultGetMarks.getReturnValue();
                    if (optional.isPresent()) {
                        HashMap marks = (HashMap) optional.get();
                        presentation.setMarks(marks);
                    }
                } else {
                    return resultGetMarks;
                }
            }

            return optionalPresentation.isPresent() ?
                    new Result(Status.success, Optional.of(presentation)) :
                    new Result(Status.error, ConstantsError.PRESENTATION_GET);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_GET);
            return new Result(Status.error, ConstantsError.PRESENTATION_GET);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        try {
            if (arguments.get(ConstantsField.ID) == null) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            } else {
                UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
                Status status = XMLCommonMethods.removeRecordById(presentation, Presentation.class, id);
                if (status == Status.success) {
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

    public static Result editPresentationOptions (HashMap arguments) throws IOException {
        try {

            UUID id = UUID.fromString((String) arguments.getOrDefault(ConstantsField.ID, null));
            if (null == id) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            }


            boolean validId = XMLCommonMethods.getInstanceById(presentation, arguments).isPresent();
            if (validId) {
                List<Presentation> list = XMLCommonMethods.getCollection(presentation).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().peek(el -> {
                    if (el.getId().equals(id)) {
                        log.debug(ConstantsInfo.ASSESSMENTS_GET + arguments);
                        String fillColor = (String) arguments.getOrDefault(ConstantsField.FILL_COLOR, el.getFillColor());
                        String fontFamily = (String) arguments.getOrDefault(ConstantsField.FONT_FAMILY, el.getFontFamily());
                        String name = (String) arguments.getOrDefault(ConstantsField.NAME, el.getName());
                        log.debug(ConstantsInfo.FIELD_EDIT + "fillColor " + fillColor);
                        el.setFillColor(fillColor);
                        log.debug(ConstantsInfo.FIELD_EDIT + "fontFamily " + fontFamily);
                        el.setFontFamily(fontFamily);
                        log.debug(ConstantsInfo.FIELD_EDIT + "name " + name);
                        el.setName(name);
                    }
                }).collect(Collectors.toList());
                Status result = XMLCommonMethods.writeCollection(updatedList, Presentation.class, presentation);
                if (result == Status.success) {
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
            log.error(ConstantsError.PRESENTATION_UPDATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
        }
    }
}
