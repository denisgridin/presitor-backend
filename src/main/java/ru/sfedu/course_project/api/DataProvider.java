package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.HashMap;

public interface DataProvider {
//    public String getName();
//    public <T> Optional<List> getCollection (CollectionType collectionType, Class cl);
//    public <T> Status writeCollection(List list, Class cl, CollectionType collectionType);
//    public <T> Boolean isIdInUse (String id, List<T> list, CollectionType collectionType);
//    public Status removeRecordById (CollectionType collectionType, Class cl, UUID id);
//    public Optional getInstanceById (Class cl, CollectionType collectionType, HashMap arguments);

    public Result createPresentation (HashMap arguments);
    public Result getPresentations ();
    public Result getPresentationById (HashMap arguments) throws IOException;
    public Result removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException;
    public Result editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException;

    public Result getPresentationSlides (HashMap arguments);
    public Result createPresentationSlide (HashMap arguments);
    public Result removePresentationSlideById (HashMap arguments);
    public Result editPresentationSlideById (HashMap arguments);
    public Result getSlideById (HashMap arguments);

    public Result getPresentationComments (HashMap arguments);
    public Result commentPresentation (HashMap arguments);
    public Result editPresentationComment (HashMap arguments);
    public Result removePresentationComment (HashMap arguments);

    public Result addElementInSlide (HashMap arguments);
    public Result removeSlideElement (HashMap arguments);
    public Result editSlideElement (HashMap arguments);
    public Result getSlideElementById (HashMap arguments);
    public Result getSlideElements (HashMap arguments);

    public Result rateByMark (HashMap arguments);
    public Result getPresentationMarks (HashMap arguments);
//    public Result editPresentationSlideOptionsById (HashMap arguments);
//    public <T> Status addCollectionRecord (T record, UUID id);
}
