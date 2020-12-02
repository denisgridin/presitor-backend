package ru.sfedu.course_project;

import ru.sfedu.course_project.bean.Presentation;

import java.util.HashMap;
import java.util.UUID;

public class TestBase {
    public Presentation makePresentation () {
        HashMap arguments = new HashMap();
        Presentation presentation = new Presentation(arguments);
        return presentation;
    }
}