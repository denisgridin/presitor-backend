package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.csv.CSVCommonMethods;
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

        Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
        Presentation presentation = optionalPresentation.get();

        Result result = provider.createPresentation(args);
        String id = result.getReturnValue().toString();

        HashMap params = new HashMap();
        params.put("id", id);

        assertEquals(presentation.toString(),
                CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, params).get().toString());
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

            Optional<Presentation> optionalEditedPresentation = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, arguments);

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
        log.debug("{TEST} getPresentationSlidesFail START");
        DataProvider provider = new DataProviderCSV();
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put("presentationId", String.valueOf(presId));
        Result getSlidesResult = provider.getPresentationSlides(args);
        assertEquals(getSlidesResult.getStatus(), Status.error);
        log.debug("{TEST} getPresentationSlidesFail END");
    }

    @Test
    void createPresentationSlideSuccess() {
        log.debug("{TEST} createPresentationSlideSuccess START");
        DataProvider provider = new DataProviderCSV();
        HashMap args = new HashMap();
        String presId = String.valueOf(UUID.randomUUID());
        String slideId = String.valueOf(UUID.randomUUID());
        args.put("id", presId);

        Result createPresResult = makePresentationWithId(provider, UUID.fromString(presId));
        if (createPresResult.getStatus() == Status.success) {
            args.put("id", slideId);
            args.put("presentationId", presId);
            Result createSlideResult = provider.createPresentationSlide(args);
            assertEquals(createSlideResult.getStatus(), Status.success);
            Optional<Slide> optionalSlide = CSVCommonMethods.getInstanceById(Slide.class, CollectionType.slide, args);
            assertTrue(optionalSlide.isPresent());
        }
        log.debug("{TEST} createPresentationSlideSuccess END");
    }

    @Test
    void removePresentationSlideByIdSuccess() {
        log.debug("{TEST} removePresentationSlideByIdSuccess START");

        DataProvider provider = new DataProviderCSV();

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();

        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);

        assertEquals(resultCreatePresentation.getStatus(), Status.success);
        assertEquals(resultCreateSlide.getStatus(), Status.success);
        if (resultCreatePresentation.getStatus() == Status.success && resultCreateSlide.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("presentationId", String.valueOf(presentationId));
            args.put("id", String.valueOf(slideId));
            Result resultGetSlide = provider.getSlideById(args);
            assertEquals(resultGetSlide.getStatus(), Status.success);

            Result resultRemoveSlide = provider.removePresentationSlideById(args);
            assertEquals(resultRemoveSlide.getStatus(), Status.success);
        }
        log.debug("{TEST} removePresentationSlideByIdSuccess END");
    }

    @Test
    void editPresentationSlideByIdSuccess() {
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        String name = "Test name";
        DataProvider provider = new DataProviderCSV();
        makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);
        if (resultCreateSlide.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("presentationId", String.valueOf(presentationId));
            args.put("id", String.valueOf(slideId));
            args.put("name", name);
            Result resultEditSlide = provider.editPresentationSlideById(args);
            assertEquals(resultEditSlide.getStatus(), Status.success);
            if (resultEditSlide.getStatus() == Status.success) {
                Result resultGetSlide = provider.getSlideById(args);
                if (resultGetSlide.getStatus() == Status.success) {
                    Slide slide = (Slide) resultGetSlide.getReturnValue();
                    assertEquals(slide.getName(),  name);
                }
            }
        }
    }

    @Test
    void editPresentationSlideByIdFail() {
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        String name = "Test name";
        DataProvider provider = new DataProviderCSV();
        makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);
        if (resultCreateSlide.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("presentationId", String.valueOf(UUID.randomUUID())); // Set random id
            args.put("id", String.valueOf(UUID.randomUUID())); // Set random id
            args.put("name", name);
            Result resultEditSlide = provider.editPresentationSlideById(args);
            assertNotEquals(resultEditSlide.getStatus(), Status.success);
        }
    }

    @Test
    void commentPresentationSuccess() {
        try {
            UUID presentationId = UUID.randomUUID();

            DataProvider provider = new DataProviderCSV();
            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {

                HashMap args = new HashMap();
                args.put("presentationId", String.valueOf(presentationId));
                args.put("text", "Тестовый текст");
                args.put("role", "editor");
                Result resultCommentPresentation = provider.commentPresentation(args);

                assertTrue(resultCommentPresentation.getStatus() == Status.success);

                HashMap params = new HashMap();
                params.put("id", String.valueOf(presentationId));

                String commentId = String.valueOf(resultCommentPresentation.getReturnValue());

                Optional resultGetPresentation = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, params);
                assertTrue(resultGetPresentation.isPresent());
                Presentation presentation = (Presentation) resultGetPresentation.get();
                log.debug(presentation);
                log.debug(presentation.getComments());
                ArrayList comments = presentation.getComments();

                log.debug("comments: " + comments);

                Optional presentationCommentId = comments.stream().filter(el -> {
                    log.debug(el.getClass().getName());
                    log.debug("comment id: " + commentId);
                    log.debug(el.equals(commentId));
                    return el.equals(commentId);
                }).limit(1).findFirst();
                assertTrue(presentationCommentId.isPresent());
            } else {
                assertTrue(false);
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void commentPresentationFail() {
        try {
            UUID presentationId = UUID.randomUUID();

            DataProvider provider = new DataProviderCSV();
            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {

                HashMap args = new HashMap();
                args.put("presentationId", String.valueOf(UUID.randomUUID()));
                args.put("text", "Тестовый текст");
                args.put("role", "editor");
                Result resultCommentPresentation = provider.commentPresentation(args);

                assertTrue(resultCommentPresentation.getStatus() == Status.error);
            } else {
                assertTrue(false);
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void getPresentationCommentsSuccess () {
        try {
            UUID presentationId = UUID.randomUUID();

            DataProvider provider = new DataProviderCSV();
            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put("presentationId", String.valueOf(presentationId));
                args.put("text", "Тестовый текст");
                args.put("role", "editor");
                Result resultCommentPresentation = provider.commentPresentation(args);
                Result resultGetPresentationComments = provider.getPresentationComments(args);
                assertTrue(resultGetPresentationComments.getStatus() == Status.success);
                assertTrue(resultCommentPresentation.getStatus() == Status.success);

                ArrayList comments = (ArrayList) resultGetPresentationComments.getReturnValue();
                assertTrue(comments.size() > 0);
            } else {
                assertTrue(false);
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void getPresentationCommentsFail () {
        try {
            UUID presentationId = UUID.randomUUID();

            DataProvider provider = new DataProviderCSV();
            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put("presentationId", String.valueOf(UUID.randomUUID()));
                args.put("text", null);
                args.put("role", "editor");
                Result resultCommentPresentation = provider.commentPresentation(args);
                Result resultGetPresentationComments = provider.getPresentationComments(args);

                assertTrue(resultGetPresentationComments.getStatus() == Status.error);
                assertTrue(resultCommentPresentation.getStatus() == Status.error);

                if (resultGetPresentationComments.getStatus() == Status.success) {
                    ArrayList comments = (ArrayList) resultGetPresentationComments.getReturnValue();
                    assertTrue(comments.size() == 0);
                }
            } else {
                assertTrue(false);
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    void editPresentationCommentSuccess() {
        UUID presentationId = UUID.randomUUID();

        DataProvider provider = new DataProviderCSV();
        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("presentationId", String.valueOf(presentationId));
            args.put("text", "Тестовый измененный текст");
            args.put("role", "editor");
            Result resultCommentPresentation = provider.commentPresentation(args);
            args.put("id", String.valueOf(resultCommentPresentation.getReturnValue()));
            Result resultEditComment = provider.editPresentationComment(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);

            assertTrue(resultCommentPresentation.getStatus() == Status.success);
            assertTrue(resultGetPresentationComments.getStatus() == Status.success);
            assertTrue(resultEditComment.getStatus() == Status.success);
        } else {
            assertTrue(false);
        }
    }

    @Test
    void editPresentationCommentFail() {
        UUID presentationId = UUID.randomUUID();

        DataProvider provider = new DataProviderCSV();
        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put("presentationId", String.valueOf(UUID.randomUUID()));
            args.put("text", null);
            args.put("role", "guest");
            Result resultCommentPresentation = provider.commentPresentation(args);
            args.put("id", String.valueOf(UUID.randomUUID()));
            Result resultEditComment = provider.editPresentationComment(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);

            assertFalse(resultCommentPresentation.getStatus() == Status.success);
            assertFalse(resultGetPresentationComments.getStatus() == Status.success);
            assertFalse(resultEditComment.getStatus() == Status.success);
        } else {
            assertTrue(false);
        }
    }

//    @Test
//    void getCollectionsListSuccess() {
//        log.debug("getCollectionSuccess");
//        DataProviderCSV provider = new DataProviderCSV();
//        HashMap args = new HashMap();
//        provider.createPresentation(args);
//        Optional<List> optionalPresentationList = provider.getCollection(CollectionType.presentation, Presentation.class);
//        assertNotNull(optionalPresentationList.get());
//    }
//
//    @Test
//    void getCollectionsListFail() {
//        System.out.println("getCollection(CollectionType.presentation)Fail");
//        DataProviderCSV provider = new DataProviderCSV();
//        Optional<List> optionalPresentationList = provider.getCollection(CollectionType.error, Presentation.class);
//        assertFalse(optionalPresentationList.isPresent());
//    }

//
//
//
//    @Test
//    void getCollectionSuccess() {
//        DataProvider provider = new DataProviderCSV();
//        assertNotEquals(provider.getCollection(CollectionType.presentation, Presentation.class), Status.error);
//        assertNotEquals(provider.getCollection(CollectionType.slide, Slide.class), Status.error);
//    }
//
//    @Test
//    void getCollectionError() {
//        DataProvider provider = new DataProviderCSV();
//        assertEquals(provider.getCollection(CollectionType.error, Presentation.class), Status.error);
//        assertEquals(provider.getCollection(CollectionType.error, Slide.class), Status.error);
//    }
}