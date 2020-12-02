package ru.sfedu.course_project.api;

import ru.sfedu.course_project.bean.Feedback;
import ru.sfedu.course_project.bean.Font;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface DataProvider {
    public void createPresentation (HashMap arguments);
    public Presentation getPresentationById (long id) throws IOException;
    public String getName();

}
