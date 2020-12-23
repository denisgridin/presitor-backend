package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.bean.Layout;
import ru.sfedu.course_project.tools.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LayoutConverter extends AbstractBeanField  {

    private static Logger log = LogManager.getLogger(ru.sfedu.course_project.converters.LayoutConverter.class);

    @Override
    protected Object convert(String s) throws RuntimeException {
        try {

            String widthRegExp = (String) Constants.FIELD_REGEXP.get("width");
            String heightRegExp = (String) Constants.FIELD_REGEXP.get("height");
            String xRegExp = (String) Constants.FIELD_REGEXP.get("x");
            String yRegExp = (String) Constants.FIELD_REGEXP.get("y");
            String rotationRegExp = (String) Constants.FIELD_REGEXP.get("rotation");

            Pattern widthPattern = Pattern.compile(widthRegExp);
            Pattern heightPattern = Pattern.compile(heightRegExp);
            Pattern rotationPattern = Pattern.compile(rotationRegExp);
            Pattern xPattern = Pattern.compile(xRegExp);
            Pattern yPattern = Pattern.compile(yRegExp);

            String width = Helpers.getFieldFromMather(widthPattern, s, "width");
            String height = Helpers.getFieldFromMather(heightPattern, s, "height");
            String x = Helpers.getFieldFromMather(xPattern, s, "x");
            String y = Helpers.getFieldFromMather(yPattern, s, "y");
            String rotation = Helpers.getFieldFromMather(rotationPattern, s, "rotation");

            Layout layout = new Layout();

            layout.setX(Integer.valueOf(x));
            layout.setY(Integer.valueOf(y));
            layout.setRotation(Integer.valueOf(rotation));
            layout.setWidth(Integer.valueOf(width));
            layout.setHeight(Integer.valueOf(height));

            log.debug("[LayoutConverter] layout converted");

            return layout;
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
