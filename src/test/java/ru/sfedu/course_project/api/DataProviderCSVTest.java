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
                provider.getInstanceById(Presentation.class, params).get().toString());
        assertEquals(result.getStatus(), Status.success);
        log.debug("{TEST} createPresentationSuccess END");
    }

    @Test
    void createPresentationFail() {
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
    void getPresentationByIdSuccess() {
        log.debug("{TEST} getPresentationByIdSuccess START");
        DataProviderCSV provider = new DataProviderCSV();
        Result result = makeRandomPresentation(provider);
        if (result.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("id", String.valueOf(result.getReturnValue()));
            Result getPresResult = provider.getPresentationById(args);
            Presentation presentation = (Presentation) getPresResult.getReturnValue();
            assertEquals(presentation.getId(), result.getReturnValue());
            assertEquals(getPresResult.getStatus(), Status.success);
        } else {
            assertFalse(true);
        }
        log.debug("{TEST} getPresentationByIdSuccess END");
    }

    @Test
    void getPresentationByIdFail() {
        log.debug("{TEST} getPresentationByIdFail START");
        DataProviderCSV provider = new DataProviderCSV();
        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));

        Result getPresResult = provider.getPresentationById(args);

        assertEquals(getPresResult.getStatus(), Status.error);
        log.debug("{TEST} getPresentationByIdFail END");
    }


    @Test
    void removePresentationByIdSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        log.debug("{TEST} removePresentationByIdSuccess START");
        try {
            DataProvider provider = new DataProviderCSV();

            UUID id = UUID.randomUUID();
            Result createResult = makePresentationWithId(provider, id);

            if (createResult.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put("id", String.valueOf(id));
                Result removeResult = provider.removePresentationById(args);
                assertEquals(removeResult.getStatus(), Status.success);
            }

        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
            log.error(e);
        }
        log.debug("{TEST} removePresentationByIdSuccess END");
    }

    @Test
    void removePresentationByIdFail() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        log.debug("{TEST} removePresentationByIdFail START");
        try {
            DataProvider provider = new DataProviderCSV();

            UUID id = UUID.randomUUID();
            Result createResult = makePresentationWithId(provider, id);

            if (createResult.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put("id", String.valueOf(UUID.randomUUID()));
                Result removeResult = provider.removePresentationById(args);
                assertEquals(removeResult.getStatus(), Status.error);
            }

        } catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
            log.error(e);
        }
        log.debug("{TEST} removePresentationByIdFail END");
    }

    @Test
    void editPresentationOptionsSuccess() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        log.debug("{TEST} editPresentationOptionsSuccess START");
        DataProviderCSV provider = new DataProviderCSV();
        UUID id = UUID.randomUUID();
        Result createResult = makePresentationWithId(provider, id);

        if (createResult.getStatus() == Status.success) {
            HashMap arguments = new HashMap();
            arguments.put("name", "My presentation");
            arguments.put("fillColor", "#403add");
            arguments.put("fontFamily", "Times New Roman");
            arguments.put("id", String.valueOf(id));

            assertEquals(provider.editPresentationOptions(arguments).getStatus(), Status.success);

            Optional<Presentation> optionalEditedPresentation = provider.getInstanceById(Presentation.class, arguments);

            assertTrue(optionalEditedPresentation.isPresent());

            if (optionalEditedPresentation.isPresent()) {
                Presentation editedPresentation = optionalEditedPresentation.get();
                assertEquals(editedPresentation.getName(), "My presentation");
                assertEquals(editedPresentation.getFillColor(), "#403add");
                assertEquals(editedPresentation.getFontFamily(), "Times New Roman");
            }
        }
        log.debug("{TEST} editPresentationOptionsSuccess END");
    }

    @Test
    void editPresentationOptionsFail() throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        log.debug("{TEST} editPresentationOptionsFail START");
        DataProviderCSV provider = new DataProviderCSV();
        UUID id = UUID.randomUUID();

        HashMap arguments = new HashMap();
        arguments.put("name", "My presentation");
        arguments.put("fillColor", "#403add");
        arguments.put("fontFamily", "Times New Roman");
        arguments.put("id", String.valueOf(id));

        assertEquals(provider.editPresentationOptions(arguments).getStatus(), Status.error);
        log.debug("{TEST} editPresentationOptionsFail END");
    }


    @Test
    void getPresentationSlidesSuccess() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        DataProvider provider = new DataProviderCSV();
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put("presentationId", String.valueOf(presId));
        args.put("id", String.valueOf(presId));
        Result createResult = makePresentationWithId(provider, presId);

        if (createResult.getStatus() == Status.success) {
            Result getSlidesResult = provider.getPresentationSlides(args);
            assertEquals(getSlidesResult.getStatus(), Status.success);
        }
        log.debug("{TEST} getPresentationSlidesSuccess END");
    }

    @Test
    void getPresentationSlidesFail() {
        DataProvider provider = new DataProviderCSV();
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put("presentationId", String.valueOf(presId));
        Result getSlidesResult = provider.getPresentationSlides(args);
        assertEquals(getSlidesResult.getStatus(), Status.error);
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