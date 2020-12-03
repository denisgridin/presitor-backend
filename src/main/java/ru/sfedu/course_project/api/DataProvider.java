package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface DataProvider {
    public UUID createPresentation (HashMap arguments);
    public Presentation getPresentationById (HashMap arguments) throws IOException;
    public List<Presentation> getAllPresentations ();
    public Status removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException;
    public Status editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException;
    public String getName();
}
