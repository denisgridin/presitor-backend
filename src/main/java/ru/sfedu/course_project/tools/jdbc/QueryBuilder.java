package ru.sfedu.course_project.tools.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.jdbc.JDBCPresentationMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static ru.sfedu.course_project.enums.Method.create;

public class QueryBuilder {
    public QueryBuilder() { }

    private static final Logger log = LogManager.getLogger(QueryBuilder.class);

    public static <T> String build (Method method, CollectionType collectionType, T instance, HashMap args) {
        try {
            switch (method) {
                case create: {
                    return buildCreateMethod(collectionType, instance);
                }
                case update:
                    return buildUpdateMethod(collectionType, instance, args);
                case remove:
                    return buildRemoveMethod(collectionType, args);
                case get:
                    return buildGetMethod(collectionType, instance);
                default:
                    return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildUpdateMethod(CollectionType collectionType, T instance, HashMap args) {
        try {
            log.info("Building update method");
            switch (collectionType) {
                case presentation: {
                    log.info("For: " + collectionType);
                    return buildEditPresentationQuery(instance, args);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildEditPresentationQuery(T instance, HashMap args) {
        try {
            Presentation presentation = (Presentation) instance;
            log.debug("Updating presentation: " + presentation);

            String id = String.valueOf(presentation.getId());
            String name = (String) args.getOrDefault("name", presentation.getName());
            String fillColor = (String) args.getOrDefault("fillColor", presentation.getFillColor());
            String fontFamily = (String) args.getOrDefault("fontFamily", presentation.getFontFamily());
            String values = String.format(SQLQuery.PRESENTATION_VALUES_SET, name, fillColor, fontFamily);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.presentation, values, condition);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return "";
        }
    }

    private static String buildRemoveMethod(CollectionType collectionType, HashMap args) {
        try {
            switch (collectionType) {
                case presentation: {
                    return buildRemovePresentationQuery(collectionType, args);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static String buildRemovePresentationQuery(CollectionType collectionType, HashMap args) {
        try {
            String condition = String.format("id = '%s'", args.get("id"));
            return String.format(SQLQuery.RECORD_REMOVE, collectionType, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildGetMethod(CollectionType collectionType, T instance) {
        try {
            switch (collectionType) {
                case presentation: {
                    return buildGetPresentationQuery(collectionType);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static String buildGetPresentationQuery(CollectionType collectionType) {
        try {
            return String.format(SQLQuery.RECORD_GET, collectionType);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateMethod(CollectionType collectionType, T instance) {
        try {
            switch (collectionType) {
                case presentation: {
                    return buildCreatePresentationQuery(instance);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreatePresentationQuery(T instance) {
        try {
            Presentation presentation = (Presentation) instance;
//            String id = String.valueOf(presentation.getId()).replace("-", "");
            String id = String.valueOf(presentation.getId());
            String name = presentation.getName();
            String fillColor = presentation.getFillColor();
            String fontFamily = presentation.getFontFamily();

            String fields = "(id, name, fillColor, fontFamily)";
            String values = String.format("('%s', '%s', '%s', '%s')", id, name, fillColor, fontFamily);
            String table = String.valueOf(CollectionType.presentation).toUpperCase();

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
