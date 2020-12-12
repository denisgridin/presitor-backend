package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.BaseClass;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataProvider {
    public String getName();
    public <T> Optional<List> getCollection (CollectionType collectionType, Class cl);
    public <T> Status writeCollection (List collection, Class cl);
    public <T extends BaseClass> Boolean isIdInUse (String id, List<T> list);
    public <T extends BaseClass> Status removeRecordById (CollectionType collectionType, Class cl, UUID id);

    public Result createPresentation (HashMap arguments);
    public Result getPresentationById (HashMap arguments) throws IOException;
    public Result removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException;
    public Status editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException;

    public Optional<List> getPresentationSlides (HashMap arguments);
    public Object createPresentationSlide (HashMap arguments);
//    public <T> Status addCollectionRecord (T record, UUID id);
}
