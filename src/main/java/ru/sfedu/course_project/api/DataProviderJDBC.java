package ru.sfedu.course_project.api;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.api.jdbc.*;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.sql.SQLException;
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
        return JDBCSlideMethods.removePresentationSlideById(arguments);
    }

    @Override
    public Result editPresentationSlideById(HashMap arguments) {
        return JDBCSlideMethods.editPresentationSlideById(arguments);
    }

    @Override
    public Result getSlideById(HashMap arguments) {
        return JDBCSlideMethods.getSlideById(arguments);
    }

    @Override
    public Result getPresentationComments(HashMap arguments) {
        return JDBCCommentMethods.getPresentationComments(arguments);
    }

    @Override
    public Result commentPresentation(HashMap arguments) {
        return JDBCCommentMethods.commentPresentation(arguments);
    }

    @Override
    public Result editPresentationComment(HashMap arguments) {
        return JDBCCommentMethods.editPresentationComment(arguments);
    }

    @Override
    public Result removePresentationCommentById(HashMap arguments) {
        return JDBCCommentMethods.removePresentationCommentById(arguments);
    }

    @Override
    public Result rateByMark(HashMap arguments) {
        return JDBCAssessmentMethod.rateByMark(arguments);
    }

    @Override
    public Result getPresentationMarks(HashMap arguments) {
        return JDBCAssessmentMethod.getPresentationMarks(arguments);
    }

    @Override
    public Result removePresentationMarkById(HashMap arguments) {
        return null;
    }

    @Override
    public Result getMarkById(HashMap arguments) {
        return null;
    }

    @Override
    public Result editPresentationMark(HashMap arguments) {
        return null;
    }


    @Override
    public Result addElementInSlide(HashMap arguments) {
        return JDBCElementMethods.addElementInSlide(arguments);
    }

    @Override
    public Result removeSlideElement(HashMap arguments) {
        return JDBCElementMethods.removeSlideElement(arguments);
    }

    @Override
    public Result editSlideElement(HashMap arguments) {
        return JDBCElementMethods.editSlideElement(arguments);
    }

    @Override
    public Result getSlideElementById(HashMap arguments) {
        return JDBCElementMethods.getSlideElementById(arguments);
    }

    @Override
    public Result getSlideElements(HashMap arguments) {
        return JDBCElementMethods.getSlideElements(arguments);
    }
}