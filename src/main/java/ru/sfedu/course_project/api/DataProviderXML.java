package ru.sfedu.course_project.api;

import ru.sfedu.course_project.api.xml.*;
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
        return XMLElementMethods.addElementInSlide(arguments);
    }

    @Override
    public Result removeSlideElement(HashMap arguments) {
        return XMLElementMethods.removeSlideElement(arguments);
    }

    @Override
    public Result editSlideElement(HashMap arguments) {
        return XMLElementMethods.editSlideElement(arguments);
    }

    @Override
    public Result getSlideElementById(HashMap arguments) {
        return XMLElementMethods.getSlideElementById(arguments);
    }

    @Override
    public Result getSlideElements(HashMap arguments) {
        return XMLElementMethods.getSlideElements(arguments);
    }

    @Override
    public Result rateByMark(HashMap arguments) {
        return XMLAssessmentMethods.rateByMark(arguments);
    }

    @Override
    public Result getPresentationMarks(HashMap arguments) {
        return XMLAssessmentMethods.getPresentationMarks(arguments);
    }
}
