package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.enums.BorderStyle;
import ru.sfedu.course_project.bean.Style;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Helpers;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.regex.Pattern;

public class StyleConverter extends AbstractBeanField  {

    private static Logger log = LogManager.getLogger(StyleConverter.class);

    @Override
    protected Object convert(String s) throws RuntimeException {
        Result result = convertStyle(s);
        return result.getReturnValue();
    }

    public static Result convertStyle (String s) {
        try {

            String fillColorRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.FILL_COLOR);
            String boxShadowRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.BOX_SHADOW);
            String opacityRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.OPACITY);
            String borderColorRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.BORDER_COLOR);
            String borderRadiusRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.BORDER_RADIUS);
            String borderWidthRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.BORDER_WIDTH);
            String borderStyleRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.BORDER_STYLE);

            Pattern fillColorPattern = Pattern.compile(fillColorRegExp);
            Pattern boxShadowPattern = Pattern.compile(boxShadowRegExp);
            Pattern opacityPattern = Pattern.compile(opacityRegExp);
            Pattern borderColorPattern = Pattern.compile(borderColorRegExp);
            Pattern borderRadiusPattern = Pattern.compile(borderRadiusRegExp);
            Pattern borderWidthPattern = Pattern.compile(borderWidthRegExp);
            Pattern borderStylePattern = Pattern.compile(borderStyleRegExp);

            String fillColor = Helpers.getFieldFromMather(fillColorPattern, s, ConstantsField.FILL_COLOR);
            String boxShadow = Helpers.getFieldFromMather(boxShadowPattern, s, ConstantsField.BOX_SHADOW);
            String opacity = Helpers.getFieldFromMather(opacityPattern, s, ConstantsField.OPACITY);
            String borderColor = Helpers.getFieldFromMather(borderColorPattern, s, ConstantsField.BORDER_COLOR);
            String borderRadius = Helpers.getFieldFromMather(borderRadiusPattern, s, ConstantsField.BORDER_RADIUS);
            String borderWidth = Helpers.getFieldFromMather(borderWidthPattern, s, ConstantsField.BORDER_WIDTH);
            String borderStyle = Helpers.getFieldFromMather(borderStylePattern, s, ConstantsField.BORDER_STYLE);

            Style style = new Style();

            style.setFillColor(fillColor);
            style.setBoxShadow(boxShadow);
            style.setOpacity(opacity);
            style.setBorderColor(borderColor);
            style.setBorderRadius(borderRadius);
            style.setBorderWidth(borderWidth);

            BorderStyle bs = borderStyle.isEmpty() ?  BorderStyle.none : BorderStyle.valueOf(borderStyle);
            style.setBorderStyle(bs);

            log.debug("[StyleConverter] style converted");

            return new Result(Status.success, style);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            log.error(ConstantsError.PARSE_STYLE);
            return new Result(Status.error, ConstantsError.PARSE_STYLE);
        }
    }
}
