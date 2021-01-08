package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.bean.Layout;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Helpers;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.regex.Pattern;

public class LayoutConverter extends AbstractBeanField  {

    private static Logger log = LogManager.getLogger(ru.sfedu.course_project.converters.LayoutConverter.class);

    @Override
    protected Object convert(String s) throws RuntimeException {

        Result result = convertLayout(s);
        if (Status.success == result.getStatus()) {
            return result.getReturnValue();
        } else {
            return null;
        }
    }

    public static Result convertLayout (String s) {
        try {
            String widthRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.WIDTH);
            String heightRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.HEIGHT);
            String xRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.X);
            String yRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.Y);
            String rotationRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.ROTATION);

            Pattern widthPattern = Pattern.compile(widthRegExp);
            Pattern heightPattern = Pattern.compile(heightRegExp);
            Pattern rotationPattern = Pattern.compile(rotationRegExp);
            Pattern xPattern = Pattern.compile(xRegExp);
            Pattern yPattern = Pattern.compile(yRegExp);

            String width = Helpers.getFieldFromMather(widthPattern, s, ConstantsField.WIDTH);
            String height = Helpers.getFieldFromMather(heightPattern, s, ConstantsField.HEIGHT);
            String x = Helpers.getFieldFromMather(xPattern, s, ConstantsField.X);
            String y = Helpers.getFieldFromMather(yPattern, s, ConstantsField.Y);
            String rotation = Helpers.getFieldFromMather(rotationPattern, s, ConstantsField.ROTATION);

            Layout layout = new Layout();

            layout.setX(Integer.valueOf(x));
            layout.setY(Integer.valueOf(y));
            layout.setRotation(Integer.valueOf(rotation));
            layout.setWidth(Integer.valueOf(width));
            layout.setHeight(Integer.valueOf(height));

            log.debug("[LayoutConverter] layout converted");

            return new Result(Status.success, layout);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.PARSE_LAYOUT);
        }
    }
}
