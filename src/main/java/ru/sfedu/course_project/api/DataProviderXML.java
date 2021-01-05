package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.api.xml.XMLCommentMethods;
import ru.sfedu.course_project.api.xml.XMLPresentationMethods;
import ru.sfedu.course_project.api.xml.XMLSlideMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.HashMap;

public class DataProviderXML implements DataProvider {

    @Override
    public Result createPresentation (HashMap args) {
        return XMLPresentationMethods.createPresentation(args);
    }

    public Result addPresentationInTemplate(Presentation presentation) {
        return null;
    }

    @Override
    public Result getPresentations() {
        return XMLPresentationMethods.getPresentations();
    }

    @Override
    public Result getPresentationById(HashMap arguments) throws IOException {
        return XMLPresentationMethods.getPresentationById(arguments);
    }

    @Override
    public Result removePresentationById(HashMap arguments) throws IOException {
        return XMLPresentationMethods.removePresentationById(arguments);
    }

    @Override
    public Result editPresentationOptions(HashMap arguments) throws IOException {
        return XMLPresentationMethods.editPresentationOptions(arguments);
    }

    @Override
    public Result getPresentationSlides(HashMap arguments) {
        return XMLSlideMethods.getPresentationSlides(arguments);
    }

    @Override
    public Result createPresentationSlide(HashMap arguments) {
        return XMLSlideMethods.createPresentationSlide(arguments);
    }

    @Override
    public Result removePresentationSlideById(HashMap arguments) {
        return XMLSlideMethods.removePresentationSlideById(arguments);
    }

    @Override
    public Result editPresentationSlideById(HashMap arguments) {
        return XMLSlideMethods.editPresentationSlideById(arguments);
    }

    @Override
    public Result getSlideById(HashMap arguments) {
        return XMLSlideMethods.getSlideById(arguments);
    }

    @Override
    public Result getPresentationComments(HashMap arguments) {
        return XMLCommentMethods.getPresentationComments(arguments);
    }

    @Override
    public Result commentPresentation(HashMap arguments) {
        return XMLCommentMethods.commentPresentation(arguments);
    }

    @Override
    public Result editPresentationComment(HashMap arguments) {
        return XMLCommentMethods.editPresentationComment(arguments);
    }

    @Override
    public Result removePresentationComment(HashMap arguments) {
        return XMLCommentMethods.removePresentationComment(arguments);
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
