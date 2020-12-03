package ru.sfedu.course_project.api;

import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.bean.Presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataProviderCSVTest extends TestBase {

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
                provider.getPresentationById(params).toString());
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
        assertNull(provider.getPresentationById(params));
    }

    @Test
    void getPresentationByIdSuccess() {
    }

    @Test
    void getAllPresentationsSuccess() {
        System.out.println("getAllPresentationsSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations();
        assertNotNull(presentationList);
    }

    @Test
    void getAllPresentationsFail() {
        System.out.println("getAllPresentationsFail");
    }

    @Test
    void isPresentationIdInUseSuccess() {
        System.out.println("isPresentationIdInUseSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations();
        if (presentationList.size() > 0) {
            System.out.println("[validatePresentationIdSuccess] presentation data source is NOT empty");
            Presentation presentation = presentationList.stream().findFirst().get();
            System.out.println(presentationList.toString());
            System.out.println(presentation.getId());
            assertTrue(provider.isPresentationIdInUse(String.valueOf(presentation.getId()), presentationList));
        } else {
            HashMap args = new HashMap();
            System.out.println("[validatePresentationIdSuccess] presentation data source is empty");
            UUID id = provider.createPresentation(args);
            List<Presentation> list = provider.getAllPresentations();
            System.out.println(list);
            System.out.println(id);
            assertTrue(provider.isPresentationIdInUse(String.valueOf(id), list));
        }
    }

    @Test
    void isPresentationIdInUseFail() {
        System.out.println("isPresentationIdInUseFail");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations();
        assertFalse(provider.isPresentationIdInUse(String.valueOf(UUID.randomUUID()), presentationList));
    }
}