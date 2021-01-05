package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderXML;
import ru.sfedu.course_project.api.jdbc.DataProviderJDBCTest;
import ru.sfedu.course_project.bean.Comment;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Role;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataProviderXMLTest extends TestBase {
    private static Logger log = LogManager.getLogger(DataProviderJDBCTest.class);

    DataProvider provider = new DataProviderXML();

    @BeforeAll
    static void setTestFilePath () throws IOException {
        try {
            System.setProperty("dataPath", ConfigurationUtil.getConfigurationEntry("testDataPath"));
        } catch (IOException e) {
            log.debug(e);
        }
    }

    @Test
    void createPresentationSuccess () throws IOException {

        Result result = makeRandomPresentation(provider);

        HashMap args = new HashMap();
        args.put("id", String.valueOf(result.getReturnValue()));

        Result getPresentationResult = provider.getPresentationById(args);

        assertTrue(getPresentationResult.getStatus() == Status.success);
        assertTrue(result.getStatus() == Status.success);
        assertTrue(result.getReturnValue() instanceof UUID);
    }

    @Test
    void createPresentationFail () throws IOException {
        Result result = provider.getPresentations();
        if (result.getStatus() == Status.success) {
            ArrayList presentations = (ArrayList) result.getReturnValue();
            Presentation presentation = (Presentation) presentations.get(0);
            HashMap args = new HashMap();
            UUID id = presentation.getId();

            Result resultCreatePresentation = makePresentationWithId(provider, id);
            assertTrue(resultCreatePresentation.getStatus() == Status.error);
        } else {
            assertTrue(false);
        }
    }

    @Test
    void getPresentationByIdSuccess () throws IOException {
        Result result = makeRandomPresentation(provider);

        if (result.getStatus() == Status.success) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            args.put("id", String.valueOf(presentationId));
            Result resultGetPresentation = provider.getPresentationById(args);

            assertTrue(resultGetPresentation.getStatus() == Status.success);

            Presentation foundPresentation = (Presentation) resultGetPresentation.getReturnValue();
            assertTrue(foundPresentation.getId().equals(presentationId));
        } else {
            assertTrue(false);
        }
    }

    @Test
    void getPresentationByIdFail () throws IOException {
        Result result = provider.getPresentationById(new HashMap());

        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));
        Result result2 = provider.getPresentationById(args);

        assertTrue(result.getStatus() == Status.error);
        assertTrue(result2.getStatus() == Status.error);
    }

    @Test
    void removePresentationByIdSuccess () throws IOException {
        Result result = makeRandomPresentation(provider);

        if (result.getStatus() == Status.success) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            args.put("id", String.valueOf(presentationId));
            Result resultRemovePresentation = provider.removePresentationById(args);

            assertTrue(resultRemovePresentation.getStatus() == Status.success);
        } else {
            assertTrue(false);
        }
    }

    @Test
    void removePresentationByIdFail () throws IOException {
        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));
        Result result = provider.removePresentationById(args);
        assertTrue(result.getStatus() == Status.error);
    }

    @Test
    void editPresentationOptionsSuccess () throws IOException {
        Result result = makeRandomPresentation(provider);

        if (result.getStatus() == Status.success) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            String name = "Text name";
            String fillColor = "black";
            String fontFamily = "Comic sans";
            args.put("id", String.valueOf(presentationId));
            args.put("name", name);
            args.put("fillColor", fillColor);
            args.put("fontFamily", fontFamily);

            Result resultGetPresentation = provider.getPresentationById(args);
            Result resultRemovePresentation = provider.editPresentationOptions(args);

            if (resultGetPresentation.getStatus() == Status.success) {
                assertTrue(resultRemovePresentation.getStatus() == Status.success);
                assertTrue(resultGetPresentation.getStatus() == Status.success);

                Presentation presentation = (Presentation) provider.getPresentationById(args).getReturnValue();


                assertEquals(presentation.getName(), name);
                assertEquals(presentation.getFillColor(), fillColor);
                assertEquals(presentation.getFontFamily(), fontFamily);
            }
        } else {
            assertTrue(false);
        }
    }

    @Test
    void editPresentationOptionsFail () throws IOException {
        try {
            HashMap args = new HashMap();
            args.put("id", String.valueOf(UUID.randomUUID()));

            Result resultRemovePresentation = provider.editPresentationOptions(args);

            assertEquals(Status.error, resultRemovePresentation.getStatus());
        } catch (RuntimeException | IOException e) {
            log.error(e);
        }
    }

    @Test
    void getPresentationSlidesSuccess() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        DataProvider provider = new DataProviderXML();
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presId));
        args.put(ConstantsField.ID, String.valueOf(presId));
        Result createResult = makePresentationWithId(provider, presId);

        if (Status.success == createResult.getStatus()) {
            Result getSlidesResult = provider.getPresentationSlides(args);
            assertEquals(getSlidesResult.getStatus(), Status.success);
        }
        log.debug("{TEST} getPresentationSlidesSuccess END");
    }

    @Test
    void getPresentationSlidesFail() {
        log.debug("{TEST} getPresentationSlidesFail START");
        DataProvider provider = new DataProviderXML();
        UUID presId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presId));
        Result getSlidesResult = provider.getPresentationSlides(args);
        assertEquals(getSlidesResult.getStatus(), Status.error);
        log.debug("{TEST} getPresentationSlidesFail END");
    }

    @Test
    void createPresentationSlideSuccess() {
        log.debug("{TEST} createPresentationSlideSuccess START");
        DataProvider provider = new DataProviderXML();
        HashMap args = new HashMap();
        String presId = String.valueOf(UUID.randomUUID());
        String slideId = String.valueOf(UUID.randomUUID());
        args.put(ConstantsField.ID, presId);

        Result createPresResult = makePresentationWithId(provider, UUID.fromString(presId));
        if (Status.success == createPresResult.getStatus()) {
            args.put(ConstantsField.ID, slideId);
            args.put(ConstantsField.PRESENTATION_ID, presId);
            Result createSlideResult = provider.createPresentationSlide(args);
            assertEquals(createSlideResult.getStatus(), Status.success);
            Optional<Slide> optionalSlide = XMLCommonMethods.getInstanceById(CollectionType.slide, args);
            assertTrue(optionalSlide.isPresent());
        }
        log.debug("{TEST} createPresentationSlideSuccess END");
    }

    @Test
    void removePresentationSlideByIdSuccess() {
        log.debug("{TEST} removePresentationSlideByIdSuccess START");

        DataProvider provider = new DataProviderXML();

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();

        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);

        assertEquals(resultCreatePresentation.getStatus(), Status.success);
        assertEquals(resultCreateSlide.getStatus(), Status.success);
        if (resultCreatePresentation.getStatus() == Status.success && Status.success == resultCreateSlide.getStatus()) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.ID, String.valueOf(slideId));
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
        DataProvider provider = new DataProviderXML();
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
        DataProvider provider = new DataProviderXML();
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

                HashMap params = new HashMap();
                params.put(ConstantsField.ID, String.valueOf(presentationId));

                UUID commentId = (UUID) resultCommentPresentation.getReturnValue();

                Optional resultGetPresentation = XMLCommonMethods.getInstanceById(CollectionType.presentation, params);
                assertTrue(resultGetPresentation.isPresent());
                Result resultGetComments = provider.getPresentationComments(args);

                log.debug("comments: " + resultGetComments.getReturnValue());
                ArrayList comments = (ArrayList) resultGetComments.getReturnValue();
                Optional presentationCommentId = comments.stream().filter(el -> {
                    Comment comment = (Comment) el;
                    log.debug("comment id: " + commentId);
                    log.debug("current item id: " + comment.getId());
                    log.debug(comment.getId().equals(commentId));
                    return comment.getId().equals(commentId);
                }).limit(1).findFirst();
                assertTrue(presentationCommentId.isPresent());
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
    void getPresentationCommentsFail () {
        try {
            UUID presentationId = UUID.randomUUID();

            Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

            if (resultCreatePresentation.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
                args.put(ConstantsField.TEXT, null);
                args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
                Result resultCommentPresentation = provider.commentPresentation(args);
                Result resultGetPresentationComments = provider.getPresentationComments(args);

                assertTrue(resultGetPresentationComments.getStatus() == Status.error);
                assertTrue(resultCommentPresentation.getStatus() == Status.error);

                if (resultGetPresentationComments.getStatus() == Status.success) {
                    ArrayList comments = (ArrayList) resultGetPresentationComments.getReturnValue();
                    assertTrue(comments.size() == 0);
                }
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

        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.TEXT, "Тестовый измененный текст");
            args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
            Result resultCommentPresentation = provider.commentPresentation(args);

            if (Status.success == resultCommentPresentation.getStatus()) {
                args.put(ConstantsField.ID, String.valueOf(resultCommentPresentation.getReturnValue()));
                Result resultEditComment = provider.editPresentationComment(args);
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
}
