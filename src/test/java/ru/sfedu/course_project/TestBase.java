package ru.sfedu.course_project;

import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TestBase {
    public Result makeRandomPresentation (DataProvider provider) {
        String id = String.valueOf(UUID.randomUUID());
        HashMap args = new HashMap();
        args.put("id", id);
        Result result = provider.createPresentation(args);
        return result;
    }
    public Result makePresentationWithId (DataProvider provider, UUID id) {
        HashMap args = new HashMap();
        args.put("id", String.valueOf(id));
        Result result = provider.createPresentation(args);
        return result;
    }
}