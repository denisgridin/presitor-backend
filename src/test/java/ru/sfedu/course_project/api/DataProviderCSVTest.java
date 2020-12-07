package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;

import javax.xml.crypto.Data;
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
        Optional<List> presentationList = (Optional) provider.getCollection(CollectionType.presentation, Presentation.class);
        if (presentationList.isPresent()) {
            Presentation firstPres = (Presentation) presentationList.get().get(0);
            params.put("id", String.valueOf(firstPres.getId()));
            provider.createPresentation(params);
            assertNull(provider.createPresentation(params));
        }
    }

    @Test
    void getPresentationByIdSuccess() { // TODO сделать общий метод получения элеменета по айди
    }

    @Test
    void getCollectionsListSuccess() {
        System.out.println("getCollection(CollectionType.presentation)Success");
        DataProviderCSV provider = new DataProviderCSV();
        Optional<List> optionalPresentationList = provider.getCollection(CollectionType.presentation, Presentation.class);
        assertNotNull(optionalPresentationList.get());
    }

    @Test
    void getCollectionsListFail() {
        System.out.println("getCollection(CollectionType.presentation)Fail");
        DataProviderCSV provider = new DataProviderCSV();
        Optional<List> optionalPresentationList = provider.getCollection(CollectionType.error, Presentation.class);
        assertFalse(optionalPresentationList.isPresent());
    }

    @Test
    void isPresentationIdInUseSuccess() {
        System.out.println("isPresentationIdInUseSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
        if (presentationList.size() > 0) {
            System.out.println("[validatePresentationIdSuccess] presentation data source is NOT empty");
            Presentation presentation = presentationList.stream().findFirst().get();
            System.out.println(presentationList.toString());
            System.out.println(presentation.getId());
            assertTrue(provider.isIdInUse(String.valueOf(presentation.getId()), presentationList));
        } else {
            HashMap args = new HashMap();
            System.out.println("[validatePresentationIdSuccess] presentation data source is empty");
            UUID id = provider.createPresentation(args);
            List<Presentation> list = provider.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
            System.out.println(list);
            System.out.println(id);
            assertTrue(provider.isIdInUse(String.valueOf(id), list));
        }
    }

    @Test
    void isPresentationIdInUseFail() {
        System.out.println("isPresentationIdInUseFail");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
        assertFalse(provider.isIdInUse(String.valueOf(UUID.randomUUID()), presentationList));
    }

    @Test
    void removePresentationByIdSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("removePresentationByIdSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        List<Presentation> presentationList = provider.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
        Presentation removingPresentation = presentationList.get(0);

        String id = String.valueOf(removingPresentation.getId());
        HashMap args = new HashMap();
        args.put("id", id);
        assertEquals(provider.removePresentationById(args), Status.success);
        // TODO Общий метод удаления записей
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
        // TODO Общий метод удаления записей
    }

    @Test
    void editPresentationOptionsSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        System.out.println("editPresentationOptionsSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        Presentation presentation = (Presentation) provider.getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList()).get(0);
        HashMap arguments = new HashMap();
        arguments.put("name", "My presentation");
        arguments.put("fillColor", "#403add");
        arguments.put("fontFamily", "Times New Roman");
        arguments.put("id", String.valueOf(presentation.getId()));
        assertEquals(provider.editPresentationOptions(arguments), Status.success);
        Optional<Presentation> optionalEditedPresentation = provider.getPresentationById(arguments);
        assertTrue(optionalEditedPresentation.isPresent());
        if (optionalEditedPresentation.isPresent()) {
            Presentation editedPresentation = optionalEditedPresentation.get();
            assertEquals(editedPresentation.getName(), "My presentation");
            assertEquals(editedPresentation.getFillColor(), "#403add");
            assertEquals(editedPresentation.getFontFamily(), "Times New Roman");
        }
        // Todo проверить поля в датасорсе
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
        // Todo проверить поля в датасорсе
        //
    }

    @Test
    void getPresentationSlidesSuccess() {
        DataProvider provider = new DataProviderCSV();
        Optional<List> presentations = provider.getCollection(CollectionType.presentation, Presentation.class);
        if (presentations.isPresent()) {
            HashMap args = new HashMap();
            Presentation presentation = (Presentation) presentations.get().get(0);
            args.put("presentationId", String.valueOf(presentation.getId()));
            Optional<List> list = provider.getPresentationSlides(args);
            assertTrue(list.isPresent());
        }
    }

    @Test
    void getPresentationSlidesFail() {
        DataProvider provider = new DataProviderCSV();
        HashMap args = new HashMap();
        args.put("presentationId", String.valueOf(UUID.randomUUID()));
        Optional<List> list = provider.getPresentationSlides(args);
        assertFalse(list.isPresent());
    }

    @Test
    void createPresentationSlideSuccess() {
        DataProvider provider = new DataProviderCSV();
        HashMap args = new HashMap();
        args.put("presentationId", String.valueOf(UUID.randomUUID()));
    }
}