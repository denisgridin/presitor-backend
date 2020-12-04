package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.Status;

import java.io.IOException;
import java.util.*;

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
                provider.getPresentationById(params).get().toString());
    }

    @Test
    void createPresentationFail() throws IOException {
        System.out.println("createPresentationFail");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap params = new HashMap();
        Presentation firstPres = (Presentation) provider.getAllPresentations().get().get(0);
        params.put("id", String.valueOf(firstPres.getId()));
        provider.createPresentation(params);
        assertNull(provider.createPresentation(params));
    }

    @Test
    void getPresentationByIdSuccess() {
    }

    @Test
    void getAllPresentationsSuccess() {
        System.out.println("getAllPresentationsSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        Optional<List> optionalPresentationList = provider.getAllPresentations();
        assertNotNull(optionalPresentationList.get());
    }

    @Test
    void getAllPresentationsFail() {
        System.out.println("getAllPresentationsFail");
        DataProviderCSV provider = new DataProviderCSV();
        Optional<List> optionalPresentationList = provider.getAllPresentations();
        assertNotNull(optionalPresentationList.get());
    }

    @Test
    void isPresentationIdInUseSuccess() {
        System.out.println("isPresentationIdInUseSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations().orElse(new ArrayList());
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
            List<Presentation> list = provider.getAllPresentations().orElse(new ArrayList());
            System.out.println(list);
            System.out.println(id);
            assertTrue(provider.isPresentationIdInUse(String.valueOf(id), list));
        }
    }

    @Test
    void isPresentationIdInUseFail() {
        System.out.println("isPresentationIdInUseFail");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations().orElse(new ArrayList());
        assertFalse(provider.isPresentationIdInUse(String.valueOf(UUID.randomUUID()), presentationList));
    }

    @Test
    void removePresentationByIdSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("removePresentationByIdSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getAllPresentations().orElse(new ArrayList());
        Presentation removingPresentation = presentationList.get(0);

        String id = String.valueOf(removingPresentation.getId());
        HashMap args = new HashMap();
        args.put("id", id);
        assertEquals(provider.removePresentationById(args), Status.success);
    }

    @Test
    void removePresentationByIdFail() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("removePresentationByIdFail");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap args = new HashMap();
        String id = String.valueOf(UUID.randomUUID());
        args.put("id", id);
//        assertNotEquals(provider.removePresentationById(args), Status.success);
        assertEquals(provider.removePresentationById(args), Status.error);
    }

    @Test
    void editPresentationOptionsSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("editPresentationOptionsSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        Presentation presentation = (Presentation) provider.getAllPresentations().orElse(new ArrayList()).get(0);
        HashMap arguments = new HashMap();
        arguments.put("name", "My presentation");
        arguments.put("fillColor", "#403add");
        arguments.put("fontFamily", "Times New Roman");
        arguments.put("id", String.valueOf(presentation.getId()));
        assertEquals(provider.editPresentationOptions(arguments), Status.success);
    }

    @Test
    void editPresentationOptionsFail() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("editPresentationOptionsSuccess");
        HashMap arguments = new HashMap();
        arguments.put("name", "My presentation");
        arguments.put("fillColor", "#403add");
        arguments.put("fontFamily", "Times New Roman");
        arguments.put("id", String.valueOf(UUID.randomUUID()));
        DataProviderCSV provider = new DataProviderCSV();
        assertEquals(provider.editPresentationOptions(arguments), Status.error);
    }
}