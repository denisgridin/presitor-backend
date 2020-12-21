package ru.sfedu.course_project.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.sfedu.course_project.enums.Role;

import java.util.UUID;

public class RoleConverter extends AbstractBeanField {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return Role.valueOf(s);
    }
}
