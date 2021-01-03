package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {

    private static Logger log = LogManager.getLogger(Helpers.class);

    public static String getFieldFromMather(Pattern pattern, String value, String field) {
        Matcher matcher = pattern.matcher(value);
        String item = "";
        while (matcher.find()) {
            log.debug("FIND");
            log.debug(matcher.group());
            item = matcher.group().replace(String.format("%s=", field), "");
            log.debug(String.format("%s: %s", field, item));
        }
        return item;
    }

    public static Result editContentBean(HashMap args, Content content) {
        try {
            Font font = content.getFont();

            FontCase fontCase = FontCase.normal;
            try {
                fontCase = FontCase.valueOf((String) args.getOrDefault(ConstantsField.CASE, String.valueOf(font.getFontCase())));
            } catch (RuntimeException e) {
                fontCase = FontCase.normal;
                log.error(e);
            }
            log.debug(ConstantsInfo.FIELD_EDIT + " fontCase " + fontCase);
            font.setFontCase(fontCase);

            String family = Optional.of((String) args.getOrDefault(ConstantsField.FONT_FAMILY, font.getFamily())).orElse("");
            log.debug(ConstantsInfo.FIELD_EDIT + " family " + family);
            font.setFamily(family);

            String letterSpacing = Optional.of((String) args.getOrDefault(ConstantsField.LETTER_SPACING, font.getLetterSpacing())).orElse("");
            log.debug(ConstantsInfo.FIELD_EDIT + " letterSpacing " + letterSpacing);
            font.setLetterSpacing(letterSpacing);

            String lineSpacing = Optional.of((String) args.getOrDefault(ConstantsField.LINE_SPACING, font.getLineSpacing())).orElse("");
            log.debug(ConstantsInfo.FIELD_EDIT + " lineSpacing " + lineSpacing);
            font.setLineSpacing(lineSpacing);

            String size = Optional.of((String) args.getOrDefault(ConstantsField.FONT_SIZE, font.getSize())).orElse("");
            log.debug(ConstantsInfo.FIELD_EDIT + " size " + size);
            font.setSize(size);
            log.debug("Updated content Font: " + font);

            Layout layout = content.getLayout();

            Integer rotation = Optional.of(Integer.valueOf(String.valueOf(args.getOrDefault(ConstantsField.ROTATION, String.valueOf(layout.getRotation()))))).orElse(0);
            log.debug(ConstantsInfo.FIELD_EDIT + " rotation " + rotation);
            Integer width = Optional.of(Integer.valueOf((String) args.getOrDefault(ConstantsField.WIDTH, String.valueOf(layout.getWidth())))).orElse(0);
            log.debug(ConstantsInfo.FIELD_EDIT + " width " + width);
            Integer height = Optional.of(Integer.valueOf((String) args.getOrDefault(ConstantsField.HEIGHT, String.valueOf(layout.getHeight())))).orElse( 0);
            log.debug(ConstantsInfo.FIELD_EDIT + " height" + height);

            Integer x = Optional.of(Integer.valueOf((String) args.getOrDefault(ConstantsField.X, String.valueOf(layout.getX())))).orElse(0);
            log.debug(ConstantsInfo.FIELD_EDIT + " x " + x);
            Integer y = Optional.of(Integer.valueOf((String) args.getOrDefault("y", String.valueOf(layout.getY())))).orElse( 0);
            log.debug(ConstantsInfo.FIELD_EDIT + " y" + y);

            layout.setRotation(rotation);
            layout.setX(x);
            layout.setY(y);
            layout.setWidth(width);
            layout.setHeight(height);

            log.debug("Content Layout updated");

            String name = (String) args.getOrDefault("name", content.getName());
            log.debug(ConstantsInfo.FIELD_EDIT + " name ");
            String text = (String) args.getOrDefault(ConstantsField.TEXT, content.getText( ));
            log.debug(ConstantsInfo.FIELD_EDIT + " text");

            content.setFont(font);
            content.setText(text);
            content.setLayout(layout);
            content.setName(name);

            log.debug("Content updated: " + content);

            return new Result(Status.success, content);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.CONTENT_EDIT);
            return new Result(Status.error, ConstantsError.CONTENT_EDIT);
        }
    }

    public static Result editShapeBean(HashMap args, Shape shape) {
        try {

            Layout layout = shape.getLayout();

            Integer rotation = Integer.valueOf(String.valueOf(args.getOrDefault(ConstantsField.ROTATION, String.valueOf(layout.getRotation()))));
            Integer width = Integer.valueOf((String) args.getOrDefault(ConstantsField.WIDTH, String.valueOf(layout.getWidth())));
            Integer height = Integer.valueOf((String) args.getOrDefault(ConstantsField.HEIGHT, String.valueOf(layout.getHeight())));

            Integer x = Integer.valueOf((String) args.getOrDefault(ConstantsField.X, String.valueOf(layout.getX())));
            Integer y = Integer.valueOf((String) args.getOrDefault(ConstantsField.Y, String.valueOf(layout.getY())));

            layout.setRotation(rotation);
            layout.setX(x);
            layout.setY(y);
            layout.setWidth(width);
            layout.setHeight(height);

            log.debug("Shape Layout updated");

            Style style = shape.getStyle();

            String boxShadow = (String) args.getOrDefault(ConstantsField.BOX_SHADOW, style.getBoxShadow());
            String fillColor = (String) args.getOrDefault(ConstantsField.FILL_COLOR, style.getFillColor());
            BorderStyle borderStyle = BorderStyle.valueOf((String) args.getOrDefault(ConstantsField.BORDER_STYLE, String.valueOf(style.getBorderStyle())));
            String borderWidth = (String) args.getOrDefault(ConstantsField.BORDER_WIDTH, style.getBorderWidth());
            String borderRadius = (String) args.getOrDefault(ConstantsField.BORDER_RADIUS, style.getBorderRadius());
            String borderColor = (String) args.getOrDefault(ConstantsField.BORDER_COLOR, style.getBorderColor());
            String opacity = (String) args.getOrDefault(ConstantsField.OPACITY, style.getOpacity());

            style.setBoxShadow(boxShadow);
            style.setFillColor(fillColor);
            style.setBorderStyle(borderStyle);
            style.setBorderWidth(borderWidth);
            style.setBorderRadius(borderRadius);
            style.setBorderColor(borderColor);
            style.setOpacity(opacity);

            log.debug("Shape Style updated");

            String text = (String) args.getOrDefault(ConstantsField.TEXT, shape.getText());
            String name = (String) args.getOrDefault(ConstantsField.NAME, shape.getName());

            shape.setText(text);
            shape.setLayout(layout);
            shape.setStyle(style);
            shape.setName(name);

            log.debug("Shape Data updated");
            log.info("Shape: " + shape);
            return new Result(Status.success, shape);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_EDIT);
            return new Result(Status.error, ConstantsError.SHAPE_EDIT);
        }
    }
}
