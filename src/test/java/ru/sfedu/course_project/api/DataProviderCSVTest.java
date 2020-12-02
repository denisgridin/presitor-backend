package ru.sfedu.course_project.api;

import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.bean.Presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataProviderCSVTest extends TestBase {

    @Test
    void createPresentationSuccess() throws IOException {
        System.out.println("createPresentationSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap args = new HashMap();
        Presentation presentation = makePresentation();
        provider.createPresentation(args);
        String presId = String.valueOf(presentation.getId());
        HashMap params = new HashMap();
        params.put("id", presId);
        assertEquals(presentation.toString(),
                provider.getPresentationById(params).get().toString());
    }

    @Test
    void createPresentationFail() throws IOException {
        System.out.println("createPresentationFail");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap args = new HashMap();
        provider.createPresentation(args);
        String presId = String.valueOf(UUID.randomUUID()); // set random id for fail check
        HashMap params = new HashMap();
        params.put("id", presId);
        assertNull(provider.getPresentationById(params).orElse(null));
    }

    @Test
    void getPresentationById() {
    }
}