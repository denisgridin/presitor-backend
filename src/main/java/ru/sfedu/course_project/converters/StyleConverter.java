package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.bean.BorderStyle;
import ru.sfedu.course_project.bean.Layout;
import ru.sfedu.course_project.bean.Style;
import ru.sfedu.course_project.tools.Helpers;

import javax.swing.border.Border;
import java.util.regex.Pattern;

public class StyleConverter extends AbstractBeanField  {

    private static Logger log = LogManager.getLogger(StyleConverter.class);

    @Override
    protected Object convert(String s) throws RuntimeException {
        try {

            String fillColorRegExp = (String) Constants.FIELD_REGEXP.get("fillColor");
            String boxShadowRegExp = (String) Constants.FIELD_REGEXP.get("boxShadow");
            String opacityRegExp = (String) Constants.FIELD_REGEXP.get("opacity");
            String borderColorRegExp = (String) Constants.FIELD_REGEXP.get("borderColor");
            String borderRadiusRegExp = (String) Constants.FIELD_REGEXP.get("borderRadius");
            String borderWidthRegExp = (String) Constants.FIELD_REGEXP.get("borderWidth");
            String borderStyleRegExp = (String) Constants.FIELD_REGEXP.get("borderStyle");

            Pattern fillColorPattern = Pattern.compile(fillColorRegExp);
            Pattern boxShadowPattern = Pattern.compile(boxShadowRegExp);
            Pattern opacityPattern = Pattern.compile(opacityRegExp);
            Pattern borderColorPattern = Pattern.compile(borderColorRegExp);
            Pattern borderRadiusPattern = Pattern.compile(borderRadiusRegExp);
            Pattern borderWidthPattern = Pattern.compile(borderWidthRegExp);
            Pattern borderStylePattern = Pattern.compile(borderStyleRegExp);

            String fillColor = Helpers.getFieldFromMather(fillColorPattern, s, "fillColor");
            String boxShadow = Helpers.getFieldFromMather(boxShadowPattern, s, "boxShadow");
            String opacity = Helpers.getFieldFromMather(opacityPattern, s, "opacity");
            String borderColor = Helpers.getFieldFromMather(borderColorPattern, s, "borderColor");
            String borderRadius = Helpers.getFieldFromMather(borderRadiusPattern, s, "borderRadius");
            String borderWidth = Helpers.getFieldFromMather(borderWidthPattern, s, "borderWidth");
            String borderStyle = Helpers.getFieldFromMather(borderStylePattern, s, "borderStyle");

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

            return style;
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
