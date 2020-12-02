package ru.sfedu.course_project.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public interface DataProvider {
    public UUID createPresentation (HashMap arguments);
    public Optional getPresentationById (HashMap arguments) throws IOException;
    public String getName();

}
