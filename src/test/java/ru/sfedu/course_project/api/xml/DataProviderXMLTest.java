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
import ru.sfedu.course_project.bean.Shape;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.*;
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


    @Test
    void addElementInSlideFail() {
        log.info("{ addElementInSlideFail } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.ELEMENT_TYPE, 123);
        args.put(ConstantsField.FIGURE, 123);

        Result result = provider.addElementInSlide(args);

        assertTrue(Status.error == result.getStatus());

        log.info("{ addElementInSlideFail } END");
    }

    @Test
    void addRectangleInSlideSuccess() {
        log.info("{ addElementInSlideSuccess } START");
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        makeRectangleWithId(provider, id, slideId, presentationId);

        log.info("{ addElementInSlideSuccess } END");
    }

    @Test
    void getSlideElementByIdShapeSuccess () {
        log.info("{ getSlideElementByIdShapeSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        makeRectangleWithId(provider, id, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        args.put(ConstantsField.FIGURE, String.valueOf(Figure.rectangle));
        Result resultGet = provider.getSlideElementById(args);

        assertTrue(Status.success == resultGet.getStatus());
        log.info("{ getSlideElementByIdShapeSuccess } END");
    }

    @Test
    void getSlideElementsSuccess () {
        log.info("{ getSlideElementByIdShapeSuccess } START");

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

        log.info("{ getSlideElementByIdShapeSuccess } END");
    }

    @Test
    void addCustomRectangleInSlideSuccess() {
        log.info("{ addCustomRectangleInSlideSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        HashMap testData = getUpdatedShape(UUID.randomUUID(), slideId, presentationId);

        Shape shape = (Shape) testData.get("shape");
        Result result = provider.addElementInSlide((HashMap) testData.get("args"));

        assertTrue(Status.success == result.getStatus());

        log.info("{ addCustomRectangleInSlideSuccess } END");
    }

    @Test
    void editCustomRectangleInSlideSuccess() {
        log.info("{ editCustomRectangleInSlideSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        UUID id = UUID.randomUUID();

        HashMap createParams = new HashMap();
        createParams.put(ConstantsField.ID, String.valueOf(id));
        createParams.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        createParams.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        createParams.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        createParams.put(ConstantsField.FIGURE, String.valueOf(Figure.rectangle));

        Result addResult = provider.addElementInSlide(createParams);

        if (Status.success == addResult.getStatus()) {
            Shape testShape = (Shape) addResult.getReturnValue();

            HashMap testData = getUpdatedShape(testShape.getId(), slideId, presentationId);

            Shape updatedShape = (Shape) testData.get("shape");
            Result result = provider.editSlideElement((HashMap) testData.get("args"));

            assertTrue(Status.success == result.getStatus());
        }


        log.info("{ editCustomRectangleInSlideSuccess } END");
    }


    @Test
    void removeSlideElementSuccess() {
        log.info("{ removeSlideElementSuccess } START");


        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        UUID id = UUID.randomUUID();

        HashMap params = new HashMap();
        params.put(ConstantsField.ID, String.valueOf(id));
        params.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        params.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        params.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        params.put(ConstantsField.FIGURE, String.valueOf(Figure.rectangle));

        Result addResult = provider.addElementInSlide(params);

        if (Status.success == addResult.getStatus()) {

            Result resultRemove = provider.removeSlideElement(params);
            assertTrue(Status.success == resultRemove.getStatus());
        }

        log.info("{ removeSlideElementSuccess } END");
    }

    ////////////////

    @Test
    void getSlideElementByIdShapeFail () {
        log.info("{ getSlideElementByIdShapeFail } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(123));
        args.put(ConstantsField.FIGURE, String.valueOf(123));
        Result resultGet = provider.getSlideElementById(args);

        assertFalse(Status.success == resultGet.getStatus());
        log.info("{ getSlideElementByIdShapeFail } END");
    }

    @Test
    void getSlideElementsFail () {
        log.info("{ getSlideElementsFail } START");

        Result resultGet = provider.getSlideElements(new HashMap());

        assertEquals(Status.error, resultGet.getStatus());

        log.info("{ getSlideElementsFail } END");
    }

    @Test
    void addCustomRectangleInSlideFail() {
        log.info("{ addCustomRectangleInSlideFail } START");

        Result result = provider.addElementInSlide(new HashMap());

        assertTrue(Status.error == result.getStatus());

        log.info("{ addCustomRectangleInSlideFail } END");
    }

    @Test
    void editCustomRectangleInSlideFail() {
        log.info("{ editCustomRectangleInSlideFail } START");

        Result result = provider.editSlideElement(new HashMap());
        assertTrue(Status.error == result.getStatus());
        log.info("{ editCustomRectangleInSlideFail } END");
    }


    @Test
    void removeSlideElementFail() {
        log.info("{ removeSlideElementFail } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(123));
        args.put(ConstantsField.FIGURE, String.valueOf(123));
        Result resultRemove = provider.removeSlideElement(args);
        assertTrue(Status.error == resultRemove.getStatus());

        log.info("{ removeSlideElementSuccess } END");
    }


    @Test
    void addContentInSlideSuccess() {
        log.info("{ addContentInSlideSuccess } START");
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.content));
        args.put(ConstantsField.ID, String.valueOf(id));

        makeContentWithId(provider, id, slideId, presentationId, args);

        Result resultRemove = provider.removeSlideElement(args);

        log.info("{ addContentInSlideSuccess } END");
    }

    @Test
    void addContentInSlideContentSuccess() {
        log.info("{ addContentInSlideContentSuccess } START");
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        makeCustomContent(provider, id, slideId, presentationId);

        log.info("{ addContentInSlideContentSuccess } END");
    }

    @Test
    void removeSlideElementContentSuccess() {
        log.info("{ removeSlideElementContentSuccess } START");


        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        UUID id = UUID.randomUUID();

        HashMap params = new HashMap();
        params.put(ConstantsField.ID, String.valueOf(id));
        params.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        params.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        params.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        params.put(ConstantsField.FIGURE, String.valueOf(Figure.rectangle));

        Result addResult = provider.addElementInSlide(params);

        if (Status.success == addResult.getStatus()) {

            Result resultRemove = provider.removeSlideElement(params);
            assertTrue(Status.success == resultRemove.getStatus());
        }

        log.info("{ removeSlideElementContentSuccess } END");
    }

    @Test
    void getSlideElementsContentSuccess () {
        log.info("{ getSlideElementByIdShapeSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        makeCustomContent(provider, id, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));

        Result resultGet = provider.getSlideElements(args);

        assertEquals(Status.success, resultGet.getStatus());
        ArrayList list = (ArrayList) resultGet.getReturnValue();
        assertTrue(!list.isEmpty());

        log.info("{ getSlideElementByIdShapeSuccess } END");
    }
}
