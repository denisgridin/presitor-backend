package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
