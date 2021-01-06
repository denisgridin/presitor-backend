package ru.sfedu.course_project.api;


import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.api.jdbc.JDBCPresentationMethods;
import ru.sfedu.course_project.api.jdbc.JDBCSlideMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.HashMap;

public class DataProviderJDBC implements DataProvider {
    private static final Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderJDBC() { }

    public static String getName() {
        return "JDBC";
    }

    @Override
    public Result createPresentation(HashMap arguments) {
        return JDBCPresentationMethods.createPresentation(arguments);
    }

    @Override
    public Result getPresentations() {
        return JDBCPresentationMethods.getPresentations();
    }

    @Override
    public Result getPresentationById(HashMap arguments) throws IOException {
        return JDBCPresentationMethods.getPresentationById(arguments);
    }

    @Override
    public Result removePresentationById(HashMap arguments) throws IOException {
        return JDBCPresentationMethods.removePresentationById(arguments);
    }

    @Override
    public Result editPresentationOptions(HashMap arguments) throws IOException {
        return JDBCPresentationMethods.editPresentationOptions(arguments);
    }

    @Override
    public Result getPresentationSlides(HashMap arguments) {
        return JDBCSlideMethods.getPresentationSlides(arguments);
    }

    @Override
    public Result createPresentationSlide(HashMap arguments) {
        return JDBCSlideMethods.createPresentationSlide(arguments);
    }

    @Override
    public Result removePresentationSlideById(HashMap arguments) {
        return null;
    }

    @Override
    public Result editPresentationSlideById(HashMap arguments) {
        return null;
    }

    @Override
    public Result getSlideById(HashMap arguments) {
        return JDBCSlideMethods.getSlideById(arguments);
    }

    @Override
    public Result getPresentationComments(HashMap arguments) {
        return null;
    }

    @Override
    public Result commentPresentation(HashMap arguments) {
        return null;
    }

    @Override
    public Result editPresentationComment(HashMap arguments) {
        return null;
    }

    @Override
    public Result removePresentationComment(HashMap arguments) {
        return null;
    }

    @Override
    public Result addElementInSlide(HashMap arguments) {
        return null;
    }

    @Override
    public Result removeSlideElement(HashMap arguments) {
        return null;
    }

    @Override
    public Result editSlideElement(HashMap arguments) {
        return null;
    }

    @Override
    public Result getSlideElementById(HashMap arguments) {
        return null;
    }

    @Override
    public Result getSlideElements(HashMap arguments) {
        return null;
    }

    @Override
    public Result rateByMark(HashMap arguments) {
        return null;
    }

    @Override
    public Result getPresentationMarks(HashMap arguments) {
        return null;
    }
}