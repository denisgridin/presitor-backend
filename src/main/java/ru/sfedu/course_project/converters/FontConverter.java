package ru.sfedu.course_project.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.bean.Font;
import ru.sfedu.course_project.bean.FontCase;
import ru.sfedu.course_project.tools.Helpers;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.regex.Pattern;

public class FontConverter extends AbstractBeanField {
    private static Logger log = LogManager.getLogger(ru.sfedu.course_project.converters.FontConverter.class);

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            String familyRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.FONT_FAMILY);
            String sizeRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.FONT_SIZE);
            String letterSpacingRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.LETTER_SPACING);
            String lineSpacingRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.LINE_SPACING);
            String caseRegExp = (String) Constants.FIELD_REGEXP.get(ConstantsField.FONT_CASE);

            Pattern familyPattern = Pattern.compile(familyRegExp);
            Pattern sizePattern = Pattern.compile(sizeRegExp);
            Pattern letterSpacingPattern = Pattern.compile(letterSpacingRegExp);
            Pattern lineSpacingPattern = Pattern.compile(lineSpacingRegExp);
            Pattern casePattern = Pattern.compile(caseRegExp);

            String family = Helpers.getFieldFromMather(familyPattern, s, ConstantsField.FONT_FAMILY);
            String size = Helpers.getFieldFromMather(sizePattern, s, ConstantsField.FONT_SIZE);
            String letterSpacing = Helpers.getFieldFromMather(letterSpacingPattern, s, ConstantsField.LETTER_SPACING);
            String lineSpacing = Helpers.getFieldFromMather(lineSpacingPattern, s, ConstantsField.LINE_SPACING);
            FontCase fontCase = FontCase.valueOf(Helpers.getFieldFromMather(casePattern, s, ConstantsField.FONT_CASE));

            Font font = new Font();

            font.setFontSize(size);
            font.setLineSpacing(lineSpacing);
            font.setLetterSpacing(letterSpacing);
            font.setFontCase(fontCase);
            font.setFontFamily(family);

            log.debug("[FontConverter] font converted");

            return font;
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
