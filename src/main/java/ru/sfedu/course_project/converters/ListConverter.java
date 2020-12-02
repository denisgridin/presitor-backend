package ru.sfedu.course_project.converters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Main;
import ru.sfedu.course_project.bean.Comment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ListConverter extends AbstractBeanField {
    public static Logger log = LogManager.getLogger(ListConverter.class);

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Comment> list = Arrays.asList(objectMapper.readValue(s, Comment[].class));
            log.debug(list);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
