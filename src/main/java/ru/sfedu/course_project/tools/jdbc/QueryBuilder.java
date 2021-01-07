package ru.sfedu.course_project.tools.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.jdbc.JDBCPresentationMethods;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.bean.FontCase;
import ru.sfedu.course_project.converters.LayoutConverter;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.awt.image.RescaleOp;
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
            log.info("For: " + queryMember);
            switch (queryMember) {
                case presentation: {
                    return buildEditPresentationQuery(instance, args);
                }
                case slide: {
                    return buildEditSlideQuery(instance, args);
                }
                case comment: {
                    return buildEditCommentQuery(instance, args);
                }
                case shape: {
                    return buildEditShapeQuery(instance, args);
                }
                case content: {
                    return buildEditContentQuery(instance, args);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static Layout updateLayout (Layout layout, HashMap args) {
        String x = (String) args.getOrDefault(ConstantsField.X, String.valueOf(layout.getX()));
        String y = (String) args.getOrDefault(ConstantsField.Y, String.valueOf(layout.getY()));
        String width = (String) args.getOrDefault(ConstantsField.WIDTH, String.valueOf(layout.getWidth()));
        String height = (String) args.getOrDefault(ConstantsField.HEIGHT, String.valueOf(layout.getHeight()));
        String rotation = (String) args.getOrDefault(ConstantsField.ROTATION, String.valueOf(layout.getRotation()));

        layout.setX(Integer.valueOf(x));
        layout.setY(Integer.valueOf(y));
        layout.setWidth(Integer.valueOf(width));
        layout.setHeight(Integer.valueOf(height));
        layout.setRotation(Integer.valueOf(rotation));

        log.debug("Updated layout: " + layout);

        return layout;
    }

    private static Font updateFont (Font font, HashMap args) {
        String fontFamily = (String) args.getOrDefault(args.get(ConstantsField.FONT_FAMILY), font.getFontFamily());
        String fontSize = (String) args.getOrDefault(args.get(ConstantsField.FONT_SIZE), font.getFontSize());
        String fontCase = (String) args.getOrDefault(args.get(ConstantsField.FONT_CASE), String.valueOf(font.getFontCase()));
        String letterSpacing = (String) args.getOrDefault(args.get(ConstantsField.LETTER_SPACING), font.getLetterSpacing());
        String lineSpacing = (String) args.getOrDefault(args.get(ConstantsField.LINE_SPACING), font.getLineSpacing());

        font.setFontFamily(fontFamily);
        font.setFontCase(FontCase.valueOf(fontCase));
        font.setFontSize(fontSize);
        font.setLetterSpacing(letterSpacing);
        font.setLineSpacing(lineSpacing);

        return font;
    }

    private static Style updateStyle (Style style, HashMap args) {
        String fillColor = (String) args.getOrDefault(ConstantsField.FILL_COLOR, style.getFillColor());
        String boxShadow = (String) args.getOrDefault(ConstantsField.BOX_SHADOW, style.getBoxShadow());
        String opacity = (String) args.getOrDefault(ConstantsField.OPACITY, style.getOpacity());
        String borderColor = (String) args.getOrDefault(ConstantsField.BORDER_COLOR, style.getBorderColor());
        String borderRadius = (String) args.getOrDefault(ConstantsField.BORDER_RADIUS, style.getBorderRadius());
        String borderWidth = (String) args.getOrDefault(ConstantsField.BORDER_WIDTH, style.getBorderWidth());
        String borderStyle = (String) args.getOrDefault(ConstantsField.BORDER_STYLE, style.getBorderStyle());

        style.setFillColor(fillColor);
        style.setBoxShadow(boxShadow);
        style.setOpacity(opacity);
        style.setBorderColor(borderColor);
        style.setBorderRadius(borderRadius);
        style.setBorderWidth(borderWidth);
        style.setBorderStyle(BorderStyle.valueOf(borderStyle));
        log.debug("Updated style: " + style);

        return style;
    }

    private static <T> String buildEditContentQuery (T instance, HashMap args) {
        try {
            Content content = (Content) instance;
            log.debug("Updating content: " + content);

            Layout layout = updateLayout(content.getLayout(), args);
            String layoutValue = layout.toString();

            Font font = updateFont(content.getFont(), args);
            String fontValue = font.toString().replace("'", "");;

            String name = content.getName().replace("'", "");;
            String presentationId = String.valueOf(content.getPresentationId());
            String slideId = String.valueOf(content.getSlideId());
            String id = String.valueOf(content.getId());
            String text = content.getText().replace("'", "");
            String elementType = String.valueOf(content.getElementType());

            String values = String.format(SQLQuery.CONTENT_VALUES_SET, elementType, layoutValue, name, presentationId, slideId, text, id, fontValue);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.content, values, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static <T> String buildEditShapeQuery (T instance, HashMap args) {
        try {
            Shape shape = (Shape) instance;
            log.debug("Updating shape: " + shape);

            Layout layout = updateLayout(shape.getLayout(), args);
            Style style = updateStyle(shape.getStyle(), args);

            String layoutValue = layout.toString();
            String styleValue = style.toString().replace("'", "");;
            String name = shape.getName().replace("'", "");;
            String presentationId = String.valueOf(shape.getPresentationId());
            String slideId = String.valueOf(shape.getSlideId());
            String id = String.valueOf(shape.getId());
            String text = shape.getText().replace("'", "");;
            String elementType = String.valueOf(shape.getElementType());
            String figure = String.valueOf(shape.getFigure());

            String values = String.format(SQLQuery.SHAPE_VALUES_SET, elementType, figure, id, layoutValue, name, presentationId, slideId, styleValue, text);
            String condition = String.format(SQLQuery.CONDITION_ITEM_ID, id);

            return String.format(SQLQuery.RECORD_UPDATE, CollectionType.shape, values, condition);
        } catch (RuntimeException e) {
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
            return buildRemoveInstanceQuery(queryMember, args);
//            switch (queryMember) {
//                case presentation: {
//                    return buildRemovePresentationQuery(queryMember, args);
//                }
//                case comment: {
//                    return buildRemoveCommentQuery(queryMember, args);
//                }
//                case shape: {
//                    return buildRemoveShapeQuery(queryMember, args);
//                }
//                default: return "";
//            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static String buildRemoveInstanceQuery (QueryMember queryMember, HashMap args) {
        try {
            String condition = String.format("id = '%s'", args.get(ConstantsField.ID));
            return String.format(SQLQuery.RECORD_REMOVE, queryMember, condition);
        } catch (RuntimeException e) {
            log.error(e);
            return "";
        }
    }

    private static String buildRemoveShapeQuery (QueryMember queryMember, HashMap args) {
        try {
            String condition = String.format("id = '%s'", args.get(ConstantsField.ID));
            return String.format(SQLQuery.RECORD_REMOVE, queryMember, condition);
        } catch (RuntimeException e) {
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
                case content: {
                    return buildCreateContentQuery(instance);
                }
                default: return "";
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return "";
        }
    }

    private static <T> String buildCreateContentQuery (T instance) {
        try {
            Content content = (Content) instance;

            String elementType = String.valueOf(content.getElementType());
            String layout = String.valueOf(content.getLayout());
            String name = content.getName();
            String presentationId = String.valueOf(content.getPresentationId());
            String slideId = String.valueOf(content.getSlideId());
            String text = String.valueOf(content.getText());
            String id = String.valueOf(content.getId());
            String font = String.valueOf(content.getFont()).replace("'", "");

            String fields = "(elementType, layout, name, presentationId, slideId, text, id, font)";
            String values = String.format("('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", elementType, layout, name, presentationId, slideId, text, id, font);
            String table = String.valueOf(QueryMember.content).toUpperCase();

            String queryBody = SQLQuery.RECORD_INSERT;
            log.debug("Query body: " + queryBody);
            return String.format(queryBody, table, fields, values);
        } catch (RuntimeException e) {
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
