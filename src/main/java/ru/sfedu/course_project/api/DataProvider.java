package ru.sfedu.course_project.api;

import ru.sfedu.course_project.bean.Presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface DataProvider {
    public UUID createPresentation (HashMap arguments);
    public Presentation getPresentationById (HashMap arguments) throws IOException;
    public List<Presentation> getAllPresentations ();
    public String getName();
}
