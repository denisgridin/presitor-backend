package ru.sfedu.course_project.tools.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.jdbc.JDBCPresentationMethods;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.QueryMember;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static ru.sfedu.course_project.enums.Method.create;

public class QueryBuilder {
    public QueryBuilder() { }

    private static final Logger log = LogManager.getLogger(QueryBuilder.class);

    public static <T> String build (Method method, QueryMember queryMember, T instance, HashMap args) {
        try {
            switch (method) {
                case create: {
                    return buildCreateMethod(queryMember, instance);
                }
                case update:
                    return buildUpdateMethod(queryMember, instance, args);
                case remove:
                    return buildRemoveMethod(queryMember, args);
                case get:
                    return buildGetMethod(queryMember, instance, args);
                default:
                    return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildUpdateMethod(QueryMember queryMember, T instance, HashMap args) {
        try {
            log.info("Building update method");
            switch (queryMember) {
                case presentation: {
                    log.info("For: " + queryMember);
                    return buildEditPresentationQuery(instance, args);
                }
                case slide: {
                    log.info("For: " + queryMember);
                    return buildEditSlideQuery(instance, args);
                }
                case comment: {
                    log.info("For: " + queryMember);
                    return buildEditCommentQuery(instance, args);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildEditCommentQuery (T instance, HashMap args) {
        try {
            Comment comment = (Comment) instance;
            log.debug("Updating comment: " + comment);

            String text = (String) args.getOrDefault(ConstantsField.TEXT, comment.getText());
            String presentationId = String.valueOf(comment.getPresentationId());
            String id = String.valueOf(comment.getId());
            String role = String.valueOf(args.getOrDefault(ConstantsField.ROLE, comment.getRole()));
            String datetime = String.valueOf(args.getOrDefault(ConstantsField.DATETIME, comment.getDatetime()));

            String values = String.format(SQLQuery.COMMENT_VALUES_SET, id, role, datetime, presentationId, text);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.comment, values, condition);

        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildEditSlideQuery (T instance, HashMap args) {
        try {
            Slide slide = (Slide) instance;
            log.debug("Updating slide: " + slide);

            String id = String.valueOf(slide.getId());
            String name = (String) args.getOrDefault(ConstantsField.NAME, slide.getName());
            int index = Integer.valueOf((String) args.getOrDefault(ConstantsField.INDEX, String.valueOf(slide.getIndex())));

            String values = String.format(SQLQuery.SLIDE_VALUES_SET, name, index);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.slide, values, condition);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return "";
        }
    }

    private static <T> String buildEditPresentationQuery(T instance, HashMap args) {
        try {
            Presentation presentation = (Presentation) instance;
            log.debug("Updating presentation: " + presentation);

            String id = String.valueOf(presentation.getId());
            String name = (String) args.getOrDefault(ConstantsField.NAME, presentation.getName());
            String fillColor = (String) args.getOrDefault(ConstantsField.FILL_COLOR, presentation.getFillColor());
            String fontFamily = (String) args.getOrDefault(ConstantsField.FONT_FAMILY, presentation.getFontFamily());
            String values = String.format(SQLQuery.PRESENTATION_VALUES_SET, name, fillColor, fontFamily);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.presentation, values, condition);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return "";
        }
    }

    private static String buildRemoveMethod(QueryMember queryMember, HashMap args) {
        try {
            switch (queryMember) {
                case presentation: {
                    return buildRemovePresentationQuery(queryMember, args);
                }
                case comment: {
                    return buildRemoveCommentQuery(queryMember, args);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static String buildRemoveCommentQuery (QueryMember queryMember, HashMap args) {
        try {
            String condition = String.format("id = '%s'", args.get(ConstantsField.ID));
            return String.format(SQLQuery.RECORD_REMOVE, queryMember, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static String buildRemovePresentationQuery(QueryMember queryMember, HashMap args) {
        try {
            String condition = String.format("id = '%s'", args.get(ConstantsField.ID));
            return String.format(SQLQuery.RECORD_REMOVE, queryMember, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildGetMethod(QueryMember queryMember, T instance, HashMap args) {
        try {
            if (null == args) {
                return buildGetInstancesQuery(queryMember);
            } else {
                return buildGetSingleInstanceQuery(queryMember, args);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static String buildGetSingleInstanceQuery (QueryMember queryMember, HashMap args) {
        try {
            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));
            log.info(String.format(ConstantsInfo.INSTANCE_GET, queryMember, id));
            String condition = String.format("id = '%s'", id);
            return String.format(SQLQuery.RECORD_GET_WITH_CONDITION, queryMember, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static String buildGetInstancesQuery(QueryMember queryMember) {
        try {
            return String.format(SQLQuery.RECORD_GET, queryMember);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateMethod(QueryMember queryMember, T instance) {
        try {
            switch (queryMember) {
                case presentation: {
                    return buildCreatePresentationQuery(instance);
                }
                case slide: {
                    return buildCreateSlideQuery(instance);
                }
                case comment: {
                    return buildCreateCommentQuery(instance);
                }
                case assessment: {
                    return buildCreateAssessmentQuery(instance);
                }
                case shape: {
                    return buildCreateShapeQuery(instance);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateShapeQuery (T instance) {
        try {
            Shape shape = (Shape) instance;

            String elementType = String.valueOf(shape.getElementType());
            String figure = String.valueOf(shape.getFigure());
            String layout = String.valueOf(shape.getLayout());
            String name = shape.getName();
            String presentationId = String.valueOf(shape.getPresentationId());
            String slideId = String.valueOf(shape.getSlideId());
            String style = String.valueOf(shape.getStyle()).replace("'", "");
            String text = String.valueOf(shape.getText());
            String id = String.valueOf(shape.getId());

            String fields = "(elementType, figure, id, layout, name, presentationId, slideId, style, text)";
            String values = String.format("('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", elementType, figure, id, layout, name, presentationId, slideId, style, text);
            String table = String.valueOf(QueryMember.shape).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateAssessmentQuery (T instance) {
        try {
            Assessment assessment = (Assessment) instance;

            String role = String.valueOf(assessment.getRole());
            String id = String.valueOf(assessment.getId());
            String presentationId = String.valueOf(assessment.getPresentationId());
            String mark = String.valueOf(assessment.getMark());

            String fields = "(id, role, presentationId, mark)";
            String values = String.format("('%s', '%s', '%s', '%s')", id, role, presentationId, mark);
            String table = String.valueOf(QueryMember.assessment).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateCommentQuery (T instance) {
        try {
            Comment comment = (Comment) instance;

            String id = String.valueOf(comment.getId());
            String presentationId = String.valueOf(comment.getPresentationId());
            String role = String.valueOf(comment.getRole());
            String text = comment.getText();
            String datetime = comment.getDatetime();

            String fields = "(id, role, datetime, presentationId, text)";
            String values = String.format("('%s', '%s', '%s', '%s', '%s')", id, role, datetime, presentationId, text);
            String table = String.valueOf(QueryMember.comment).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateSlideQuery(T instance) {
        try {
            Slide slide = (Slide) instance;
//            String id = String.valueOf(presentation.getId()).replace("-", "");
            String id = String.valueOf(slide.getId());
            String name = slide.getName();
            int index = slide.getIndex();
            String presentationId = String.valueOf(slide.getPresentationId());

            String fields = "(id, name, index, presentationId)";
            String values = String.format("('%s', '%s', '%s', '%s')", id, name, index, presentationId);
            String table = String.valueOf(QueryMember.slide).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreatePresentationQuery(T instance) {
        try {
            Presentation presentation = (Presentation) instance;
            String id = String.valueOf(presentation.getId());
            String name = presentation.getName();
            String fillColor = presentation.getFillColor();
            String fontFamily = presentation.getFontFamily();

            String fields = "(id, name, fillColor, fontFamily)";
            String values = String.format("('%s', '%s', '%s', '%s')", id, name, fillColor, fontFamily);
            String table = String.valueOf(QueryMember.presentation).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }
}
