package ru.sfedu.course_project.api.jdbc;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderJDBC;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Role;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataProviderJDBCTest extends TestBase {
    private static Logger log = LogManager.getLogger(DataProviderJDBCTest.class);

    DataProvider provider = new DataProviderJDBC();

    @BeforeAll
    static void setTestFilePath () throws IOException {
        try {
            System.setProperty("dataBasePath", ConfigurationUtil.getConfigurationEntry("database_path_test"));
            System.setProperty("dataPath", ConfigurationUtil.getConfigurationEntry("testDataPath"));
        } catch (IOException e) {
            log.debug(e);
        }
    }

    @Test
    void createPresentationSuccess () throws IOException {
        log.info("{ createPresentationSuccess }");
        Result result = makeRandomPresentation(provider);

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(result.getReturnValue()));

        Result getPresentationResult = provider.getPresentationById(args);

        assertSame(getPresentationResult.getStatus(), Status.success);
        assertSame(result.getStatus(), Status.success);
        assertTrue(result.getReturnValue() instanceof UUID);
    }

//    @Test
//    void createPresentationFail () throws IOException {
//        HashMap args = new HashMap();
//        Result result = provider.createPresentation(new HashMap());
//        assertSame(result.getStatus(), Status.error);
//    }


    @Test
    void getPresentationsSuccess () {
        Result result = provider.getPresentations();
        assertSame(result.getStatus(), Status.success);
    }

    @Test
    void getPresentationByIdSuccess () throws IOException {
        Result result = makeRandomPresentation(provider);
        HashMap args = new HashMap();
        UUID presentationId = (UUID) result.getReturnValue();
        args.put(ConstantsField.ID, String.valueOf(presentationId));
        Result resultGetPresentation = provider.getPresentationById(args);
        log.debug("Find presentation: " + presentationId);
        assertSame(resultGetPresentation.getStatus(), Status.success);

        Presentation foundPresentation = (Presentation) resultGetPresentation.getReturnValue();
        log.debug("Found: presentation: " + foundPresentation.getId());
        assertEquals(presentationId, foundPresentation.getId());
    }

    @Test
    void getPresentationByIdFail () throws IOException {
        Result result = provider.getPresentationById(new HashMap());

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
        Result result2 = provider.getPresentationById(args);

        assertSame(result.getStatus(), Status.error);
        assertSame(result2.getStatus(), Status.error);
    }

    @Test
    void removePresentationByIdSuccess () throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        Result result = makeRandomPresentation(provider);

        if (result.getStatus() == Status.success) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            args.put("id", String.valueOf(presentationId));
            Result resultRemovePresentation = provider.removePresentationById(args);

            assertSame(resultRemovePresentation.getStatus(), Status.success);
        } else {
            fail();
        }
    }

    @Test
    void removePresentationByIdFail () throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));
        Result result = provider.removePresentationById(args);
        assertSame(result.getStatus(), Status.error);
    }

    @Test
    void editPresentationOptionsSuccess () throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        Result result = makeRandomPresentation(provider);

        if (Status.success == result.getStatus()) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            String name = "Text name";
            String fillColor = "black";
            String fontFamily = "Comic sans";
            args.put(ConstantsField.ID, String.valueOf(presentationId));
            args.put(ConstantsField.NAME, name);
            args.put(ConstantsField.FILL_COLOR, fillColor);
            args.put(ConstantsField.FONT_FAMILY, fontFamily);

            Result resultGetPresentation = provider.getPresentationById(args);
            Result resultRemovePresentation = provider.editPresentationOptions(args);

            if (Status.success == resultGetPresentation.getStatus()) {
                assertSame(resultRemovePresentation.getStatus(), Status.success);
                assertSame(resultGetPresentation.getStatus(), Status.success);

                Presentation presentation = (Presentation) provider.getPresentationById(args).getReturnValue();


                assertEquals(presentation.getName(), name);
                assertEquals(presentation.getFillColor(), fillColor);
                assertEquals(presentation.getFontFamily(), fontFamily);
            }
        } else {
            fail();
        }
    }

    @Test
    void editPresentationOptionsFail () {
        try {
            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));

            Result resultRemovePresentation = provider.editPresentationOptions(args);

            assertEquals(resultRemovePresentation.getStatus(), Status.error);
        } catch (RuntimeException | IOException  e) {
            log.error(e);
        }
    }

    @Test
    void getPresentationSlidesSuccess() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presId));
        args.put(ConstantsField.ID, String.valueOf(presId));
        Result createResult = makePresentationWithId(provider, presId);

        args.remove(ConstantsField.ID);
        provider.createPresentationSlide(args);

        if (Status.success == createResult.getStatus()) {
            Result getSlidesResult = provider.getPresentationSlides(args);
            assertEquals(getSlidesResult.getStatus(), Status.success);
        }
        log.debug("{TEST} getPresentationSlidesSuccess END");
    }

    @Test
    void getPresentationSlidesSuccess2() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        UUID presId = UUID.fromString("7e8bba60-7c0b-4f8e-b77c-79d0c7ee97a0");
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presId));
        args.put(ConstantsField.ID, String.valueOf(presId));
//        Result createResult = makePresentationWithId(provider, presId);

        args.remove(ConstantsField.ID);
        provider.createPresentationSlide(args);
        Result getSlidesResult = provider.getPresentationSlides(args);
        assertEquals(getSlidesResult.getStatus(), Status.success);
        log.debug("{TEST} getPresentationSlidesSuccess END");
    }

    @Test
    void getPresentationSlidesFail() {
        log.debug("{TEST} getPresentationSlidesFail START");
        Result getSlidesResult = provider.getPresentationSlides(new HashMap());
        assertEquals(getSlidesResult.getStatus(), Status.error);
        log.debug("{TEST} getPresentationSlidesFail END");
    }

    @Test
    void createPresentationSlideSuccess() {
        log.debug("{TEST} createPresentationSlideSuccess START");
        HashMap args = new HashMap();
        String presId = String.valueOf(UUID.randomUUID());
        String slideId = String.valueOf(UUID.randomUUID());
        args.put(ConstantsField.ID, presId);

        Result createPresResult = makePresentationWithId(provider, UUID.fromString(presId));
        assertSame(createPresResult.getStatus(), Status.success);
        if (Status.success == createPresResult.getStatus()) {
            log.info("{ Slide creation stage }");
            args.put(ConstantsField.ID, slideId);
            args.put(ConstantsField.PRESENTATION_ID, presId);
            Result createSlideResult = provider.createPresentationSlide(args);
            assertEquals(createSlideResult.getStatus(), Status.success);
        } else {
            fail();
        }
        log.debug("{TEST} createPresentationSlideSuccess END");
    }

    @Test
    void createPresentationSlideSuccess2() {
        log.debug("{TEST} createPresentationSlideSuccess START");
        HashMap args = new HashMap();
        String presId = "7e8bba60-7c0b-4f8e-b77c-79d0c7ee97a0";
        String slideId = String.valueOf(UUID.randomUUID());
        args.put(ConstantsField.ID, presId);

        log.info("{ Slide creation stage }");
        args.put(ConstantsField.ID, slideId);
        args.put(ConstantsField.PRESENTATION_ID, presId);
        Result createSlideResult = provider.createPresentationSlide(args);
        assertEquals(createSlideResult.getStatus(), Status.success);
        log.debug("{TEST} createPresentationSlideSuccess END");
    }

    @Test
    void removePresentationSlideByIdSuccess() {
        log.debug("{TEST} removePresentationSlideByIdSuccess START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.fromString("8603fe2e-8c2c-481f-badf-137103e8d8cf");

        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);

        assertEquals(resultCreatePresentation.getStatus(), Status.success);
        assertEquals(resultCreateSlide.getStatus(), Status.success);
        if (resultCreatePresentation.getStatus() == Status.success && Status.success == resultCreateSlide.getStatus()) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.ID, String.valueOf(slideId));
            log.debug("{ Get created slide }");
            Result resultGetSlide = provider.getSlideById(args);
            assertEquals(resultGetSlide.getStatus(), Status.success);

            log.debug("{ Remove created slide }");
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
        makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);
        if (resultCreateSlide.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.ID, String.valueOf(slideId));
            args.put(ConstantsField.NAME, name);
            Result resultEditSlide = provider.editPresentationSlideById(args);
            assertEquals(resultEditSlide.getStatus(), Status.success);
            if (Status.success == resultEditSlide.getStatus()) {
                Result resultGetSlide = provider.getSlideById(args);
                if (Status.success == resultGetSlide.getStatus()) {
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
        makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);
        if (Status.success == resultCreateSlide.getStatus()) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID())); // Set random id
            args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID())); // Set random id
            args.put(ConstantsField.NAME, name);
            Result resultEditSlide = provider.editPresentationSlideById(args);
            assertNotEquals(resultEditSlide.getStatus(), Status.success);
        }
    }

    @Test
    void getPresentationCommentsSuccess () {
        try {
            UUID presentationId = UUID.randomUUID();

            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
                args.put(ConstantsField.TEXT, "Тестовый текст");
                args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
                Result resultCommentPresentation = provider.commentPresentation(args);
                Result resultGetPresentationComments = provider.getPresentationComments(args);
                assertTrue(resultGetPresentationComments.getStatus() == Status.success);
                assertTrue(resultCommentPresentation.getStatus() == Status.success);

                ArrayList comments = (ArrayList) resultGetPresentationComments.getReturnValue();
                assertTrue(comments.size() > 0);
            } else {
                fail();
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getPresentationCommentsSuccess2 () {
        try {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, "73b4b915-7e3f-4185-967b-c6a06b4cfe32");
            args.put(ConstantsField.TEXT, "Тестовый текст");
            args.put(ConstantsField.ROLE, String.valueOf(Role.guest));
            UUID presentationId = UUID.fromString("73b4b915-7e3f-4185-967b-c6a06b4cfe32");

            Result resultCommentPresentation = provider.commentPresentation(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);
            assertTrue(resultGetPresentationComments.getStatus() == Status.success);
            assertTrue(resultCommentPresentation.getStatus() == Status.success);

            ArrayList comments = (ArrayList) resultGetPresentationComments.getReturnValue();
            assertTrue(comments.size() > 0);
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void commentPresentationSuccess() {
        try {
            UUID presentationId = UUID.randomUUID();

            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {

                HashMap args = new HashMap();
                args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
                args.put(ConstantsField.TEXT, "Тестовый текст");
                args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
                Result resultCommentPresentation = provider.commentPresentation(args);

                assertTrue(Status.success == resultCommentPresentation.getStatus());
            } else {
                fail();
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void commentPresentationSuccess2() {
        try {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, "73b4b915-7e3f-4185-967b-c6a06b4cfe32");
            args.put(ConstantsField.TEXT, "Тестовый текст test");
            args.put(ConstantsField.ROLE, String.valueOf(Role.guest));
            Result resultCommentPresentation = provider.commentPresentation(args);

            assertTrue(Status.success == resultCommentPresentation.getStatus());
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void commentPresentationFail() {
        try {
            UUID presentationId = UUID.randomUUID();

            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {

                HashMap args = new HashMap();
                args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
                args.put(ConstantsField.TEXT, "Тестовый текст");
                args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
                Result resultCommentPresentation = provider.commentPresentation(args);

                assertTrue(resultCommentPresentation.getStatus() == Status.error);
            } else {
                fail();
            }
        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void editPresentationCommentSuccess() {
        UUID presentationId = UUID.randomUUID();

        log.info("{ Create presentation }");
        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.TEXT, "Тестовый текст");
            args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
            log.info("{ comment presentation }");
            Result resultCommentPresentation = provider.commentPresentation(args);

            if (Status.success == resultCommentPresentation.getStatus()) {

                log.info("{ Edit presentation comment }");

                HashMap params = new HashMap();
                params.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
                params.put(ConstantsField.ID, String.valueOf(resultCommentPresentation.getReturnValue()));
                params.put(ConstantsField.TEXT, "Test text");
                params.put(ConstantsField.ROLE, Role.guest);
                Result resultEditComment = provider.editPresentationComment(params);

                log.info("{ Get comments }");
                Result resultGetPresentationComments = provider.getPresentationComments(args);

                assertTrue(resultCommentPresentation.getStatus() == Status.success);
                assertTrue(resultGetPresentationComments.getStatus() == Status.success);
                assertTrue(resultEditComment.getStatus() == Status.success);
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    @Test
    void editPresentationCommentFail() {
        UUID presentationId = UUID.randomUUID();

        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
            args.put(ConstantsField.TEXT, null);
            args.put(ConstantsField.ROLE, "guest");
            Result resultCommentPresentation = provider.commentPresentation(args);
            args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
            Result resultEditComment = provider.editPresentationComment(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);

            assertFalse(Status.success == resultCommentPresentation.getStatus());
            assertFalse(Status.success == resultGetPresentationComments.getStatus());
            assertFalse(Status.success == resultEditComment.getStatus());
        } else {
            fail();
        }
    }

    @Test
    void removePresentationCommentSuccess () {
        log.info("{ removePresentationCommentSuccess } START");

        UUID presentationId = UUID.randomUUID();

        log.info("{ Create presentation }");
        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.TEXT, "Тестовый текст");
            args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
            log.info("{ comment presentation }");
            Result resultCommentPresentation = provider.commentPresentation(args);

            if (Status.success == resultCommentPresentation.getStatus()) {

                log.info("{ Remove presentation comment }");

                args.put(ConstantsField.ID, String.valueOf(resultCommentPresentation.getReturnValue()));
                Result resultRemoveComment = provider.removePresentationComment(args);

                log.info("{ Get comments }");
                Result resultGetPresentationComments = provider.getPresentationComments(args);

                assertTrue(resultCommentPresentation.getStatus() == Status.success);
                assertTrue(resultGetPresentationComments.getStatus() == Status.success);
                assertTrue(resultRemoveComment.getStatus() == Status.success);
            } else {
                fail();
            }
        } else {
            fail();
        }

        log.info("{ removePresentationCommentSuccess } END");
    }

    @Test
    void rateByMarkSuccess() {
        log.info("{ rateByMarkSuccess } START");

        UUID presentationId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
        args.put(ConstantsField.MARK, "bed");
        Result resultRate = provider.rateByMark(args);

        assertTrue(Status.success == resultRate.getStatus());

        log.info("{ rateByMarkSuccess } END");
    }

    @Test
    void getPresentationMarks () {
        log.info("{ getPresentationMarks } START");

        UUID presentationId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
        args.put(ConstantsField.MARK, "bed");
        Result resultRate = provider.rateByMark(args);

        assertTrue(Status.success == resultRate.getStatus());

        if (resultRate.getStatus() == Status.success) {
            Result resultGetMarks = provider.getPresentationMarks(args);

            assertSame(resultGetMarks.getStatus(), Status.success);
        }


        log.info("{ getPresentationMarks } END");
    }

    @Test
    void getSlideElementsSuccess () {
        log.info("{ getSlideElementsSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        makeRectangleWithId(provider, id, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));

        Result resultGet = provider.getSlideElements(args);

        assertEquals(Status.success, resultGet.getStatus());
        ArrayList list = (ArrayList) resultGet.getReturnValue();
        assertTrue(!list.isEmpty());

        log.info("{ getSlideElementsSuccess } END");
    }

}