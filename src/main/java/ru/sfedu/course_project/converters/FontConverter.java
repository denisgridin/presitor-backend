package ru.sfedu.course_project.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.sfedu.course_project.bean.Font;

import java.io.IOException;
import java.util.List;

public class FontConverter extends AbstractBeanField {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, Font.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
