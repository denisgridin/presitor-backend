package ru.sfedu.course_project.api.jdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.*;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class JDBCPresentationMethods {
    private static final Logger log = LogManager.getLogger(JDBCPresentationMethods.class);

    public static Result createPresentationFromTemplate (HashMap args) {
        try {
            log.info("Searching template presentation");
            HashMap params = new HashMap();
            params.put(ConstantsField.ID, String.valueOf(args.get(ConstantsField.TEMPLATE_ID)));
            params.put(ConstantsField.WITH_SLIDES, Constants.TRUE_VALUE);
            params.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);
            Result resultGetTemplate = getPresentationById(params);

            if (Status.error == resultGetTemplate.getStatus()) {
                return resultGetTemplate;
            }

            Optional optional = (Optional) resultGetTemplate.getReturnValue();
            Presentation template = (Presentation) optional.get();
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
            UUID id = UUID.randomUUID();
            String name = String.format(Constants.TEMPLATE_NAME, Constants.DEFAULT_PRESENTATION.get(ConstantsField.NAME), template.getName());
            String fillColor = template.getFillColor();
            String fontFamily = template.getFontFamily();

            log.info(ConstantsInfo.PRESENTATION_BUILD_FROM_TEMPLATE);

            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(id));
            args.put(ConstantsField.NAME, name);
            args.put(ConstantsField.FILL_COLOR, fillColor);
            args.put(ConstantsField.FONT_FAMILY, fontFamily);

            Result resultCreatePresentation = createPresentation(args);

            if (Status.error == resultCreatePresentation.getStatus()) {
                return resultCreatePresentation;
            }

            log.info(ConstantsInfo.PRESENTATION_CREATE_FROM_TEMPLATE);

            UUID presentationId = id;

            ArrayList templateSlides = template.getSlides();

            if (templateSlides != null) {
                templateSlides.stream().forEach(el -> {
                    Slide slide = (Slide) el;
                    UUID slideId = UUID.randomUUID();
                    slide.setId(slideId);
                    slide.setPresentationId(presentationId);

                    writeSlide(slide);

                    if (slide.getElements() != null) {
                        log.info(ConstantsInfo.ARGUMENTS_ADD + slide);
                        ArrayList elements = slide.getElements();

                        log.debug(ConstantsInfo.ELEMENTS + elements);

                        elements.stream().forEach(item -> {
                            Element element = (Element) item;
                            element.setPresentationId(presentationId);
                            element.setSlideId(slideId);
                            ElementType elementType = element.getElementType();
                            switch (elementType) {
                                case shape: {
                                    writeShape((Shape) item);
                                    break;
                                }
                                case content: {
                                    writeContent((Content) item);
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        });
                    }
                });
            }

            return new Result(Status.success, id);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE_FROM_TEMPLATE);
        }
    }

    private static Result writeSlide (Slide slide) {
        try {
            String query = QueryBuilder.build(Method.create, QueryMember.slide, slide, null);

            Statement statement = JDBCCommonMethods.setConnection();

            int rows = statement.executeUpdate(query);

            if (rows > 0) {
                return new Result(Status.success, ConstantsSuccess.SLIDE_CREATE);
            } else {
                return new Result(Status.error, ConstantsError.SLIDE_CREATE);
            }

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.SLIDE_CREATE);
        }
    }

    private static Result writeShape (Shape shape) {
        try {
            String query = QueryBuilder.build(Method.create, QueryMember.shape, shape, null);

            Statement statement = JDBCCommonMethods.setConnection();

            int rows = statement.executeUpdate(query);

            if (rows > 0) {
                return new Result(Status.success, ConstantsSuccess.SHAPE_CREATE);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_CREATE);
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.SHAPE_CREATE);
        }
    }
    private static Result writeContent (Content content) {
        try {
            String query = QueryBuilder.build(Method.create, QueryMember.content, content, null);

            Statement statement = JDBCCommonMethods.setConnection();

            int rows = statement.executeUpdate(query);

            if (rows > 0) {
                return new Result(Status.success, ConstantsSuccess.CONTENT_CREATE);
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_CREATE);
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.CONTENT_CREATE);
        }
    }

    public static Result createPresentation(HashMap args) {

        try {
            String templateId = (String) args.get(ConstantsField.TEMPLATE_ID);
            if (null != templateId) {
                return createPresentationFromTemplate(args);
            }

            log.debug(ConstantsInfo.PRESENTATIONS_CREATE);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();

            if (!optionalPresentation.isPresent()) {
                log.error(ConstantsError.PRESENTATION_CREATE);
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.presentation, optionalPresentation.get(), null);
            log.debug("Query string: " + query);

            if (query.isEmpty()) {
                log.error("Query string is empty");
                return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
            }
            log.debug("Create table if not exist");
            statement.execute(SQLQuery.CREATE_PRESENTATION_TABLE);
//            statement.execute("DROP TABLE PRESENTATION");
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.PRESENTATION_CREATE);
            return new Result(Status.success, optionalPresentation.get().getId());
        } catch (IndexOutOfBoundsException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_CREATE);
            return new Result(Status.error, ConstantsError.PRESENTATION_CREATE);
        }
    }

    public static Result getPresentations () {
        try {
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }
            Result result = JDBCCommonMethods.getCollection(QueryMember.presentation);
            JDBCCommonMethods.closeConnection();

            if (result.getStatus() == Status.success) {
                return new Result(Status.success, Optional.of(result.getReturnValue()));
            } else {
                return result;
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result getPresentationById(HashMap arguments) {
        try {
            if (null == arguments.get(ConstantsField.ID)) {
                log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
                return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            }
            log.debug(ConstantsInfo.PRESENTATIONS_GET);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            log.debug(ConstantsInfo.SQL_BUILD);
            String query = QueryBuilder.build(Method.get, QueryMember.presentation, null, arguments);

            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));

            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            ResultSet resultSet = statement.executeQuery(query);
            log.info(ConstantsInfo.EXECUTE_RESULT + resultSet);

            Result resultParseSet = JDBCCommonMethods.getInstanceFromResultSet(resultSet, QueryMember.presentation);
            JDBCCommonMethods.closeConnection();

            if (Status.error == resultParseSet.getStatus()) {
                return resultParseSet;
            }

            Presentation presentation = (Presentation) resultParseSet.getReturnValue();
            log.debug(ConstantsInfo.PRESENTATION + presentation);
            boolean withSlides = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_SLIDES, "false"));
            boolean withComments = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_COMMENTS, "false"));
            boolean withMarks = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_MARKS, "false"));
            boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_ELEMENTS, "false"));
            log.info(ConstantsField.WITH_SLIDES + withSlides);
            log.info(ConstantsField.WITH_COMMENTS + withComments);
            log.info(ConstantsField.WITH_MARKS + withMarks);
            log.info(ConstantsField.WITH_ELEMENTS + withElements);



            if (withSlides) {
                HashMap paramsGetSlides = new HashMap();
                paramsGetSlides.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                paramsGetSlides.put(ConstantsField.WITH_ELEMENTS, arguments.get(ConstantsField.WITH_ELEMENTS));
                Result resultGetSlides = JDBCSlideMethods.getPresentationSlides(paramsGetSlides);
                log.debug(ConstantsInfo.SLIDES_GET + paramsGetSlides.get(ConstantsField.PRESENTATION_ID));
                if (Status.success == resultGetSlides.getStatus()) {
                    Optional optional = (Optional) resultGetSlides.getReturnValue();
                    presentation.setSlides((ArrayList) optional.get());
                } else {
                    return resultGetSlides;
                }
            }

            if (withComments) {
                HashMap paramsGetComments = new HashMap();
                paramsGetComments.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                Result resultGetComments = JDBCCommentMethods.getPresentationComments(paramsGetComments);
                log.debug(ConstantsInfo.COMMENTS_GET + paramsGetComments.get(ConstantsField.ID));
                if (Status.success == resultGetComments.getStatus()) {
                    Optional optional = (Optional) resultGetComments.getReturnValue();
                    presentation.setComments((ArrayList) optional.get());
                } else {
                    return resultGetComments;
                }
            }

            if (withMarks) {
                HashMap paramsGetMarks = new HashMap();
                paramsGetMarks.put(ConstantsField.PRESENTATION_ID, arguments.get(ConstantsField.ID));
                log.debug(ConstantsInfo.MARKS_GET + paramsGetMarks.get(ConstantsField.ID));

                Result resultGetMarks = JDBCAssessmentMethod.getPresentationMarks(paramsGetMarks);

                if (Status.success == resultGetMarks.getStatus()) {
                    Optional optional = (Optional) resultGetMarks.getReturnValue();
                    presentation.setMarks((HashMap) optional.get());
                } else {
                    return resultGetMarks;
                }
            }


            return new Result(Status.success, Optional.of(presentation));
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.PRESENTATION_NOT_FOUND);
            return new Result(Status.error, ConstantsError.PRESENTATION_NOT_FOUND);
        }
    }

    public static Result removePresentationById (HashMap arguments) {
        if (null == arguments.get(ConstantsField.ID)) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            String query = QueryBuilder.build(Method.remove, QueryMember.presentation, null, arguments);

            log.debug(ConstantsInfo.QUERY + query);
            int rows = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();

            if (rows == 0) {
                return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
            }
            return removePresentationMembers(arguments);
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }

    public static Result removePresentationMembers (HashMap args) {
        try {
            String condition = String.format("presentationId = '%s'", args.get(ConstantsField.PRESENTATION_ID));
            String querySlides = String.format(SQLQuery.RECORD_REMOVE, QueryMember.slide, condition);;
            String queryComment = String.format(SQLQuery.RECORD_REMOVE, QueryMember.comment, condition);;
            String queryShape = String.format(SQLQuery.RECORD_REMOVE, QueryMember.shape, condition);;
            String queryContent = String.format(SQLQuery.RECORD_REMOVE, QueryMember.content, condition);;
            String queryAssessments = String.format(SQLQuery.RECORD_REMOVE, QueryMember.assessment, condition);;

            Statement statement = JDBCCommonMethods.setConnection();

            log.debug("Remove slides: " + querySlides);
            statement.execute(querySlides);

            log.debug("Remove comments: " + queryComment);
            statement.execute(queryComment);

            log.debug("Remove shapes: " + queryShape);
            statement.execute(queryShape);

            log.debug("Remove contents: " + queryContent);
            statement.execute(queryContent);

            log.debug("Remove assessments: " + queryAssessments);
            statement.execute(queryAssessments);

            return new Result(Status.success, ConstantsSuccess.PRESENTATION_REMOVE);

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_MEMBERS_REMOVE);
        }
    }

    public static Result editPresentationOptions (HashMap arguments) {
        if (null == arguments.get(ConstantsField.ID)) {
            log.error(ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
            return new Result(Status.error, ConstantsError.ARGUMENT_IS_NOT_PROVIDED + ConstantsField.ID);
        }
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            Result resultGetPresentation = getPresentationById(arguments);

            if (Status.error == resultGetPresentation.getStatus()){
                return resultGetPresentation;
            }

            Optional optional = (Optional) resultGetPresentation.getReturnValue();
            Presentation presentation = (Presentation) optional.get();

            String query = QueryBuilder.build(Method.update, QueryMember.presentation, presentation, arguments);
            log.debug(ConstantsInfo.QUERY + query);
            int resultRows = statement.executeUpdate(query);
            log.debug(ConstantsInfo.UPDATED_ROWS + resultRows);
            JDBCCommonMethods.closeConnection();
            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.PRESENTATION_UPDATE);
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
            }
        } catch (RuntimeException | SQLException | IOException e){
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
        }
    }
}