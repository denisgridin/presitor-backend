package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListIdsConverter extends AbstractCsvConverter {
    public static Logger log = LogManager.getLogger(ListIdsConverter.class);

    @Override
    public String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        Pattern pattern = null;
        try {
            log.debug(ConfigurationUtil.getConfigurationEntry("uuid_regexp"));
            pattern = Pattern.compile(ConfigurationUtil.getConfigurationEntry("uuid_regexp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String idString = value.toString();
        Matcher matcher = pattern.matcher(idString);
        log.debug(idString);
        String id = null;
        while (matcher.find()) {
            log.debug("FIND");
            log.debug(matcher.group());
            id = matcher.group().replace("id", "");
            log.debug("Id = " + id);
        }
        return id;
    }

    @Override
    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return s;
    }

//    @Override
//    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
//        List instances = Arrays.asList(s.split("#"));
//        List <Feedback> feedbacks = new ArrayList();
//        for (int i = 0; i < 2; i++) {
//            log.debug(String.valueOf(instances.stream().count()));
//            log.debug(i);
//            String element = instances.get(i).toString();
//            log.debug("element: " + element);
//            String instance = element.substring(0, element.indexOf("{"));
//            List data = Arrays.asList(element.replace(instance, "").replace("{", "").replace("}", "").split(";"));
//            HashMap values = new HashMap();
//            data.forEach(el -> {
//                List <String> items = Arrays.asList(el.toString().split("="));
//                log.debug(items);
//                values.put(items.get(0), items.get(1));
//            });
//            Feedback feedback = null;
//            if (instance == "Comment") {
//                feedback = new Comment(1, Role.valueOf((String) values.get("role")), (String) values.get("text"));
//            } else if (instance == "Assessment") {
//                feedback = new Assessment(1, Role.valueOf((String) values.get("role")), (Mark) values.get("mark"));
//            }
//            log.debug(feedback.toString());
//            feedbacks.add(feedback);
//        }
//        log.debug(feedbacks);
//        return feedbacks;
//    }
}
