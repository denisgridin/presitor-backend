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

import static ru.sfedu.course_project.enums.CollectionType.comment;
import static ru.sfedu.course_project.enums.CollectionType.presentation;

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
            log.info(ConstantsInfo.TEMPLATE_SEARCH);
            HashMap params = new HashMap();
            params.put(ConstantsField.ID, String.valueOf(args.get(ConstantsField.TEMPLATE_ID)));
            params.put(ConstantsField.WITH_SLIDES, Constants.TRUE_VALUE);
            params.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);
            Result resultGetTemplate = getPresentationById(params);

            if (Status.error == resultGetTemplate.getStatus()) {
                return resultGetTemplate;
            }

            Optional <Presentation> optionalTemplate = (Optional<Presentation>) resultGetTemplate.getReturnValue();
            Presentation template = optionalTemplate.get();
            log.debug(ConstantsInfo.TEMPLATE_FOUND + template);

            return buildPresentationFromTemplate(template);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
        }
    }

    public static Result buildPresentationFromTemplate (Presentation template) {
        try {
            log.info(ConstantsInfo.PRESENTATION_CREATE_FROM_TEMPLATE + template);

            UUID id = UUID.randomUUID();
            String name = String.format(Constants.TEMPLATE_NAME, Constants.DEFAULT_PRESENTATION.get(ConstantsField.NAME), template.getName());
            String fillColor = template.getFillColor();
            String fontFamily = template.getFontFamily();

            log.info(ConstantsInfo.PRESENTATION_BUILD_FROM_TEMPLATE);

            log.debug(ConstantsInfo.ARGUMENTS_ADD);
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

            log.info(ConstantsSuccess.PRESENTATION_CREATED_FROM_TEMPLATE);

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
                    log.info(ConstantsInfo.ELEMENTS_WRITE_FROM_SLIDE + slide);
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

            log.debug(ConstantsInfo.SLIDES + presentationSlides);
            log.debug(ConstantsInfo.SHAPES + presentationShapes);
            log.debug(ConstantsInfo.CONTENTS + presentationContents);

            ArrayList allSlides = (ArrayList) CSVCommonMethods.getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
            ArrayList allShapes = (ArrayList) CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            ArrayList allContents = (ArrayList) CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());

            allSlides.addAll(presentationSlides);
            allShapes.addAll(presentationShapes);
            allContents.addAll(presentationContents);

            CSVCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
            CSVCommonMethods.writeCollection(allShapes, Shape.class, CollectionType.shape);
            CSVCommonMethods.writeCollection(allContents, Content.class, CollectionType.content);

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

            boolean withSlides = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_SLIDES, String.valueOf(false)));
            boolean withComments = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_COMMENTS, String.valueOf(false)));
            boolean withMarks = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_MARKS, String.valueOf(false)));
            boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_ELEMENTS, String.valueOf(false)));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, ConstantsField.WITH_SLIDES, withSlides));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, ConstantsField.WITH_COMMENTS, withComments));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, ConstantsField.WITH_MARKS, withMarks));
            log.info(String.format(ConstantsInfo.FIELD_VALUE, ConstantsField.WITH_ELEMENTS, withElements));

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(CollectionType.presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND);
            }

            Presentation presentation = optionalPresentation.get();

            if (withSlides) {
                HashMap paramsGetSlides = new HashMap();
                paramsGetSlides.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                paramsGetSlides.put(ConstantsField.WITH_ELEMENTS, arguments.get(ConstantsField.WITH_ELEMENTS));
                Result resultGetSlides = CSVSlideMethods.getPresentationSlides(paramsGetSlides);
                log.debug(ConstantsInfo.SLIDES_GET + paramsGetSlides.get(ConstantsField.PRESENTATION_ID));
                if (Status.success == resultGetSlides.getStatus()) {
                    Optional optional = (Optional) resultGetSlides.getReturnValue();
                    if (optional.isPresent()) {
                        ArrayList slides = (ArrayList) optional.get();
                        presentation.setSlides(slides);
                    }
                } else {
                    return resultGetSlides;
                }
            }

            if (withComments) {
                HashMap paramsGetComments = new HashMap();
                paramsGetComments.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                Result resultGetComments = CSVCommentMethods.getPresentationComments(paramsGetComments);
                log.debug(ConstantsInfo.COMMENTS_GET + paramsGetComments.get(ConstantsField.PRESENTATION_ID));
                if (Status.success == resultGetComments.getStatus()) {
                    Optional optional = (Optional) resultGetComments.getReturnValue();
                    if (optional.isPresent()) {
                        ArrayList comments = (ArrayList) optional.get();
                        presentation.setComments(comments);
                    }

                } else {
                    return resultGetComments;
                }
            }

            if (withMarks) {
                HashMap paramsGetMarks = new HashMap();
                paramsGetMarks.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                log.debug(ConstantsInfo.ASSESSMENTS_GET + paramsGetMarks.get(ConstantsField.ID));

                Result resultGetMarks = CSVAssessmentMethods.getPresentationMarks(paramsGetMarks);

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
            if (null == arguments.get(ConstantsField.ID)) {
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            } else {
                log.debug(ConstantsInfo.PRESENTATION_REMOVE);
                UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
                Status status = CSVCommonMethods.removeRecordById(presentation, Presentation.class, id);
                if (Status.success == status) {
                    log.info(ConstantsSuccess.PRESENTATION_REMOVE);
                    log.info(ConstantsInfo.PRESENTATION_CHILDREN_REMOVE);
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

    public static Status removePresentationSlides (UUID presentationId) {
        try {
            ArrayList allSlides = (ArrayList) CSVCommonMethods.getCollection(CollectionType.slide, Slide.class).orElse(new ArrayList());
            log.debug(ConstantsInfo.MEMBERS_GET + ConstantsInfo.SLIDES);

            allSlides = (ArrayList) allSlides.stream().filter(el -> {
                Slide slide = (Slide) el;
                return !slide.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            Status statusWriteAllSlides = CSVCommonMethods.writeCollection(allSlides, Slide.class, CollectionType.slide);
            log.debug(String.format(ConstantsInfo.MEMBERS_WROTE, ConstantsInfo.SLIDES, statusWriteAllSlides));
            log.debug(ConstantsInfo.MEMBERS_FILTER + ConstantsInfo.SLIDES);
            return statusWriteAllSlides;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SLIDES_REMOVE);
            return Status.error;
        }
    }

    public static Status removePresentationElements (UUID presentationId) {
        try {
            ArrayList allShapes = (ArrayList) CSVCommonMethods.getCollection(CollectionType.shape, Shape.class).orElse(new ArrayList());
            log.debug(ConstantsInfo.MEMBERS_GET + ConstantsInfo.SHAPES);
            ArrayList allContents = (ArrayList) CSVCommonMethods.getCollection(CollectionType.content, Content.class).orElse(new ArrayList());
            log.debug(ConstantsInfo.MEMBERS_GET + ConstantsInfo.CONTENTS);

            allShapes = (ArrayList) allShapes.stream().filter(el -> {
                Shape shape = (Shape) el;
                return !shape.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.MEMBERS_FILTER + ConstantsInfo.SHAPES);
            allContents = (ArrayList) allContents.stream().filter(el -> {
                Content content = (Content) el;
                return !content.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.MEMBERS_FILTER + ConstantsInfo.CONTENTS);

            Status statusWriteAllShapes = CSVCommonMethods.writeCollection(allShapes, Shape.class, CollectionType.shape);
            log.debug(String.format(ConstantsInfo.MEMBERS_WROTE, ConstantsInfo.SHAPES, statusWriteAllShapes));
            Status statusWriteAllContents = CSVCommonMethods.writeCollection(allContents, Content.class, CollectionType.content);
            log.debug(String.format(ConstantsInfo.MEMBERS_WROTE, ConstantsInfo.CONTENTS, statusWriteAllContents));

            if (statusWriteAllShapes == Status.success && statusWriteAllContents == Status.success) {
                return Status.success;
            } else {
                return Status.error;
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_REMOVE);
            return Status.error;
        }
    }

    public static Status removePresentationComments (UUID presentationId) {
        try {
            ArrayList allComments = (ArrayList) CSVCommonMethods.getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            log.debug(ConstantsInfo.MEMBERS_GET + ConstantsInfo.COMMENTS);

            allComments = (ArrayList) allComments.stream().filter(el -> {
                Comment comment = (Comment) el;
                return !comment.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.MEMBERS_FILTER + ConstantsInfo.COMMENTS);


            Status statusWriteAllComments = CSVCommonMethods.writeCollection(allComments, Comment.class, CollectionType.comment);
            log.debug(String.format(ConstantsInfo.MEMBERS_WROTE, ConstantsInfo.COMMENTS, statusWriteAllComments));

            return statusWriteAllComments;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.COMMENTS_REMOVE);
            return Status.error;
        }
    }

    public static Status removePresentationMarks (UUID presentationId) {
        try {
            ArrayList allMarks = (ArrayList) CSVCommonMethods.getCollection(CollectionType.assessment, Assessment.class).orElse(new ArrayList());
            log.debug(ConstantsInfo.MEMBERS_GET + ConstantsInfo.MARKS);


            allMarks = (ArrayList) allMarks.stream().filter(el -> {
                Assessment assessment = (Assessment) el;
                return !assessment.getPresentationId().equals(presentationId);
            }).collect(Collectors.toList());
            log.debug(ConstantsInfo.MEMBERS_FILTER + ConstantsInfo.MARKS);
            Status statusWriteAllMarks = CSVCommonMethods.writeCollection(allMarks, Assessment.class, CollectionType.assessment);
            log.debug(String.format(ConstantsInfo.MEMBERS_WROTE, ConstantsInfo.MARKS, statusWriteAllMarks));
            return statusWriteAllMarks;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENTS_REMOVE);
            return Status.error;
        }
    }

    public static Result removePresentationChildren (UUID presentationId){
        try {

            Status statusRemoveSlides = removePresentationSlides(presentationId);
            Status statusRemoveElements = removePresentationElements(presentationId);
            Status statusRemoveAssessments = removePresentationMarks(presentationId);

            if (statusRemoveAssessments == Status.success && statusRemoveElements == Status.success && statusRemoveSlides == Status.success) {
                return new Result(Status.success, ConstantsSuccess.PRESENTATION_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
            }
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }

    public static Result editPresentationOptions (HashMap arguments) throws IOException {
        try {
            UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
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

            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.FILL_COLOR + fillColor);
            presentation.setFillColor(fillColor);
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.FONT_FAMILY + fontFamily);
            presentation.setFontFamily(fontFamily);
            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.NAME + name);
            presentation.setName(name);
        } return presentation;
    }

}