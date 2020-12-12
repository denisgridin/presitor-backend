package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataProviderCSVTest extends TestBase {

    private static Logger log = LogManager.getLogger(DataProviderCSVTest.class);

    @Test
    void createPresentationSuccess() throws IOException {
        log.debug("{TEST} createPresentationSuccess START");

        DataProviderCSV provider = new DataProviderCSV();

        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));

        Presentation presentation = (Presentation) new Creator().create(Presentation.class, args).get();

        Result result = provider.createPresentation(args);
        String id = result.getReturnValue().toString();

        HashMap params = new HashMap();
        params.put("id", id);

        assertEquals(presentation.toString(),
                provider.getPresentationById(params).get().toString());
        assertEquals(result.getStatus(), Status.success);
        log.debug("{TEST} createPresentationSuccess END");
    }

    @Test
    void createPresentationFail() throws IOException {
        log.debug("{TEST} createPresentationFail START");
        DataProviderCSV provider = new DataProviderCSV();

        String id = String.valueOf(UUID.randomUUID());

        HashMap args = new HashMap();
        args.put("id", id);

        provider.createPresentation(args);
        Result result = provider.createPresentation(args);

        assertNotEquals(result.getStatus(), Status.success);
        assertEquals(result.getStatus(), Status.error);
        log.debug("{TEST} createPresentationFail END");
    }

    @Test
    void getPresentationByIdSuccess() { // TODO сделать общий метод получения элеменета по айди
    }

    @Test
    void getCollectionsListSuccess() {
        log.debug("getCollectionSuccess");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap args = new HashMap();
        provider.createPresentation(args);
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
        Presentation presentation = (Presentation) provider.getCollection(CollectionType.presentation, Presentation.class).get().get(0);
        UUID id = presentation.getId();
        args.put("presentationId", String.valueOf(id));
        provider.createPresentationSlide(args);
        assertNotEquals(provider.createPresentationSlide(args), Status.error);
//        assertTrue(provider.createPresentationSlide(args) instanceof UUID);
    }

    @Test
    void getCollectionSuccess() {
        DataProvider provider = new DataProviderCSV();
        assertNotEquals(provider.getCollection(CollectionType.presentation, Presentation.class), Status.error);
        assertNotEquals(provider.getCollection(CollectionType.slide, Slide.class), Status.error);
    }

    @Test
    void getCollectionError() {
        DataProvider provider = new DataProviderCSV();
        assertEquals(provider.getCollection(CollectionType.error, Presentation.class), Status.error);
        assertEquals(provider.getCollection(CollectionType.error, Slide.class), Status.error);
    }
}