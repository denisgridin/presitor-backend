package ru.sfedu.course_project;

import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TestBase {
    public Result makeRandomPresentation (DataProvider provider) {
        String id = String.valueOf(UUID.randomUUID());
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, id);
        Result result = provider.createPresentation(args);
        return result;
    }
    public Result makePresentationWithId (DataProvider provider, UUID id) {
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        Result result = provider.createPresentation(args);
        return result;
    }

    public Result makeSlideWithId (DataProvider provider, UUID id, UUID presentationId) {
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        Result result = provider.createPresentationSlide(args);
        return result;
    }
}