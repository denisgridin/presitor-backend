package ru.sfedu.course_project.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.api.csv.*;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.*;

import java.io.IOException;
import java.util.*;

public class DataProviderCSV implements DataProvider {
    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

    public String getName () {
        return "CSV";
    }


    ///                      Presentations section                          \\\

    @Override
    public Result createPresentation(HashMap arguments) {
        return CSVPresentationMethods.createPresentation(arguments);
    }

    @Override
    public Result getPresentations () {
        return CSVPresentationMethods.getPresentations();
    }

    @Override
    public Result getPresentationById (HashMap arguments) {
        return CSVPresentationMethods.getPresentationById(arguments);
    }

    @Override
    public Result removePresentationById (HashMap arguments) {
        return CSVPresentationMethods.removePresentationById(arguments);
    }

    @Override
    public Result editPresentationOptions (HashMap arguments) throws IOException {
        return CSVPresentationMethods.editPresentationOptions(arguments);
    }


    ///                         Slides section                          \\\

    @Override
    public Result createPresentationSlide (HashMap arguments) {
        return CSVSlideMethods.createPresentationSlide(arguments);
    }

    @Override
    public Result getPresentationSlides (HashMap arguments) {
        return CSVSlideMethods.getPresentationSlides(arguments);
    }

    @Override
    public Result getSlideById (HashMap args) {
        return CSVSlideMethods.getSlideById(args);
    }

    @Override
    public Result editPresentationSlideById (HashMap args) {
        return CSVSlideMethods.editPresentationSlideById(args);
    }

    @Override
    public Result removePresentationSlideById (HashMap arguments) {
        return CSVSlideMethods.removePresentationSlideById(arguments);
    }


    @Override
    public Result commentPresentation (HashMap arguments) {
        return CSVCommentMethods.commentPresentation(arguments);
    }

    @Override
    public Result getPresentationComments (HashMap arguments) {
        return CSVCommentMethods.getPresentationComments(arguments);
    }

    public static Status writeCommentsCollection (Comment comment) {
        try {
            ArrayList comments = (ArrayList) CSVCommonMethods.getCollection(CollectionType.comment, Comment.class).orElse(new ArrayList());
            comments.add(comment);
            Status status = CSVCommonMethods.writeCollection(comments, Comment.class, CollectionType.comment);
            return status;
        } catch (RuntimeException e) {
            log.error(e);
            return Status.error;
        }
    }

    @Override
    public Result editPresentationComment (HashMap arguments) {
        return CSVCommentMethods.editPresentationComment(arguments);
    }

    @Override
    public Result removePresentationComment (HashMap arguments) {
        return CSVCommentMethods.removePresentationComment(arguments);
    }


    @Override
    public Result addElementInSlide (HashMap args) {
        return CSVElementMethods.addElementInSlide(args);
    }

    @Override
    public Result removeSlideElement (HashMap args) {
        return CSVElementMethods.removeSlideElement(args);
    }

    @Override
    public Result editSlideElement (HashMap args) {
        return CSVElementMethods.editSlideElement(args);
    }

    @Override
    public Result getSlideElementById (HashMap arguments) {
        return CSVElementMethods.getSlideElementById(arguments);
    }

    @Override
    public Result getSlideElements (HashMap arguments) {
        return CSVElementMethods.getSlideElements(arguments);
    }

    @Override
    public Result rateByMark(HashMap arguments) {
        return CSVAssessmentMethods.rateByMark(arguments);
    }

    @Override
    public Result getPresentationMarks (HashMap args) {
        return CSVAssessmentMethods.getPresentationMarks(args);
    }
}
