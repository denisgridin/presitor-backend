package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.api.xml.XMLPresentationMethods;
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
    public Result removePresentationById(HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        return XMLPresentationMethods.removePresentationById(arguments);
    }

    @Override
    public Result editPresentationOptions(HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        return XMLPresentationMethods.editPresentationOptions(arguments);
    }

    @Override
    public Result getPresentationSlides(HashMap arguments) {
        return null;
    }

    @Override
    public Result createPresentationSlide(HashMap arguments) {
        return null;
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
        return null;
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
}
