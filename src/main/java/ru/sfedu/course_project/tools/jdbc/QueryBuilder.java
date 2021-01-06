package ru.sfedu.course_project.tools.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.jdbc.JDBCPresentationMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
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
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
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
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
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
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
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
