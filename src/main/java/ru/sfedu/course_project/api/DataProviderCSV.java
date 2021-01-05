package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.api.csv.*;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import ru.sfedu.course_project.utils.ConstantsField;

import static ru.sfedu.course_project.enums.CollectionType.*;

public class DataProviderCSV implements DataProvider {
    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

    public String getName () {
        return "CSV";
    }


//    @Override
//    public <T> Status addCollectionRecord (T record, UUID id) {
//        try {
//            String collectionType = record.getClass().getName().toLowerCase();
//            List itemsList = getCollection(CollectionType.valueOf(collectionType), record.getClass()).orElse(new ArrayList());
//            itemsList.add(record);
//            Status result = writeCollection(itemsList, record.getClass());
//            if (result == Status.success) {
//                log.info(String.format("[addCollectionRecord] Item was successfully added: %s %s", collectionType, id));
//                return Status.success;
//            } else {
//                log.error("[addCollectionRecord] Unable to create presentation");
//                return Status.error;
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            log.error(e);
//            log.error("[addCollectionRecord] Unable to add collection record ");
//            return Status.error;
//        }
//    }


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
