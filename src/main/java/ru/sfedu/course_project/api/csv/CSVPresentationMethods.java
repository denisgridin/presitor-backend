package ru.sfedu.course_project.api.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.ConstantsError;
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
import static ru.sfedu.course_project.enums.CollectionType.template;

public class CSVPresentationMethods {
    private static final Logger log = LogManager.getLogger(CSVPresentationMethods.class);

    public CSVPresentationMethods() { }

    public static Result createPresentation(HashMap args) {
        try {

            String templateId = (String) args.get(ConstantsField.TEMPLATE_ID);
            if (null != templateId) {
                return createPresentationFromTemplate(args);
            }


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


    public static Result createPresentationFromTemplate (HashMap args) {
        try {
            log.info("Searching template presentation");
            HashMap params = new HashMap();
            params.put(ConstantsField.ID, String.valueOf(args.get(ConstantsField.TEMPLATE_ID)));
            params.put(ConstantsField.WITH_SLIDES, "true");
            params.put(ConstantsField.WITH_ELEMENTS, "true");
            Result resultGetTemplate = getPresentationById(params);

            if (Status.error == resultGetTemplate.getStatus()) {
                return resultGetTemplate;
            }

            Presentation template = (Presentation) resultGetTemplate.getReturnValue();
            log.debug("Template found: " + template);

            return buildPresentationFromTemplate(template);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
        }
    }

    public static Result buildPresentationFromTemplate (Presentation template) {
        try {
            log.info("Create presentation from template: " + template);

            UUID id = UUID.randomUUID();
            String name = String.format(Constants.TEMPLATE_NAME, Constants.DEFAULT_PRESENTATION.get(ConstantsField.NAME), template.getName());
            String fillColor = template.getFillColor();
            String fontFamily = template.getFontFamily();

            log.info("Build presentation from template");

            log.debug("Add parameters for new presentation");
            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(id));
            log.info(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.ID, id));

            args.put(ConstantsField.NAME, name);
            log.info(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.NAME, name));

            args.put(ConstantsField.FILL_COLOR, fillColor);
            log.info(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.FILL_COLOR, fillColor));

            args.put(ConstantsField.FONT_FAMILY, fontFamily);
            log.info(String.format(ConstantsInfo.FIELD_FORMAT_SET, ConstantsField.FONT_FAMILY, fontFamily));

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

            ArrayList allSlides = (ArrayList) CSVCommonMethods.getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
            ArrayList allShapes = (ArrayList) CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            ArrayList allContents = (ArrayList) CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());

            allSlides.addAll(presentationSlides);
            allShapes.addAll(presentationShapes);
            allContents.addAll(presentationContents);

            Status statusWriteSlides = CSVCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
            log.info("Slides wrote");

            Status statusWriteShapes = CSVCommonMethods.writeCollection(allShapes, Shape.class, CollectionType.shape);
            log.info("Shapes wrote");

            Status statusWriteContents = CSVCommonMethods.writeCollection(allContents, Content.class, CollectionType.content);
            log.info("Contents wrote");

            return new Result(Status.success, id);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
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

            if (withSlides) {
                HashMap paramsGetSlides = new HashMap();
                paramsGetSlides.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                paramsGetSlides.put(ConstantsField.WITH_ELEMENTS, arguments.get(ConstantsField.WITH_ELEMENTS));
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

            if (withMarks) {
                HashMap paramsGetMarks = new HashMap();
                paramsGetMarks.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                log.debug("[getPresentationById] get presentation marks: " + paramsGetMarks.get(ConstantsField.ID));

                Result resultGetMarks = CSVAssessmentMethods.getPresentationMarks(paramsGetMarks);

                if (Status.success == resultGetMarks.getStatus()) {
                    presentation.setMarks((HashMap) resultGetMarks.getReturnValue());
                } else {
                    return resultGetMarks;
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
                log.debug("Presentation removing");
                UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
                Status status = CSVCommonMethods.removeRecordById(presentation, Presentation.class, id);
                if (Status.success == status) {
                    log.info(ConstantsSuccess.PRESENTATION_REMOVE);
                    log.info("Removing presentation children..");
                    return removePresentationChildren(id);
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

    public static Result removePresentationChildren (UUID presentationId){
        try {
            ArrayList allSlides = (ArrayList) CSVCommonMethods.getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
            log.debug("get allSlides");
            ArrayList allShapes = (ArrayList) CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            log.debug("get allShapes");
            ArrayList allContents = (ArrayList) CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());
            log.debug("get allContents");
            ArrayList allComments = (ArrayList) CSVCommonMethods.getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            log.debug("get allComments");
            ArrayList allMarks = (ArrayList) CSVCommonMethods.getCollection(CollectionType.assessment, Assessment.class).orElse(new ArrayList());
            log.debug("get allMarks");

            allSlides = (ArrayList) allSlides.stream().filter(el -> {
                Slide slide = (Slide) el;
                return !slide.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug("Filter allSlides");
            allShapes = (ArrayList) allShapes.stream().filter(el -> {
                Shape shape = (Shape) el;
                return !shape.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug("Filter allShapes");
            allContents = (ArrayList) allContents.stream().filter(el -> {
                Content content = (Content) el;
                return !content.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug("Filter allContents");
            allComments = (ArrayList) allComments.stream().filter(el -> {
                Comment comment = (Comment) el;
                return !comment.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug("Filter allComments");
            allMarks = (ArrayList) allMarks.stream().filter(el -> {
                Assessment assessment = (Assessment) el;
                return !assessment.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug("Filter allMarks");


            Status statusWriteAllSlides = CSVCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
            log.debug("statusWriteAllSlides: " + statusWriteAllSlides);
            Status statusWriteAllShapes = CSVCommonMethods.writeCollection(allShapes, Shape.class, CollectionType.shape);
            log.debug("statusWriteAllShapes: " + statusWriteAllShapes);
            Status statusWriteAllContents = CSVCommonMethods.writeCollection(allContents, Content.class, CollectionType.content);
            log.debug("statusWriteAllContents: " + statusWriteAllContents);
            Status statusWriteAllComments = CSVCommonMethods.writeCollection(allComments, Comment.class, CollectionType.comment);
            log.debug("statusWriteAllComments: " + statusWriteAllComments);
            Status statusWriteAllMarks = CSVCommonMethods.writeCollection(allMarks, Assessment.class, CollectionType.assessment);
            log.debug("statusWriteAllMarks: " + statusWriteAllMarks);

            return new Result(Status.success, ConstantsSuccess.PRESENTATION_REMOVE);
        } catch (RuntimeException e) {
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
        } return presentation;
    }

}