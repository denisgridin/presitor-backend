package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataProvider {
    public String getName();
    public UUID createPresentation (HashMap arguments);
    public Optional<Presentation> getPresentationById (HashMap arguments) throws IOException;
    public Optional<List> getAllPresentations ();
    public Status removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException;
    public Status editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException;

//    public UUID createSlide (HashMap arguments) throws IOException;
    public Optional<List> getPresentationSlides (HashMap arguments);
    public Object createPresentationSlide (HashMap arguments);
}
