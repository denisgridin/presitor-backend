package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderXML;
import ru.sfedu.course_project.api.jdbc.DataProviderJDBCTest;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.Creator;
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
    void createPresentationSuccess() throws IOException {
        log.debug("{TEST} createPresentationSuccess START");

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.NAME, "Тестовое имя");

        Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
        if (optionalPresentation.isPresent()) {
            Presentation presentation = optionalPresentation.get();

            Result result = provider.createPresentation(args);
            String id = result.getReturnValue().toString();

            HashMap params = new HashMap();
            params.put(ConstantsField.ID, id);

            Optional<Presentation> optionalItem = XMLCommonMethods.getInstanceById(CollectionType.presentation, params);

            if (optionalItem.isPresent()) {
                assertEquals(presentation.getId().toString(),
                        optionalItem.get().getId().toString());
            }
            assertEquals(result.getStatus(), Status.success);
        }
        log.debug("{TEST} createPresentationSuccess END");
    }

    @Test
    void createPresentationFail() {
        log.debug("{TEST} createPresentationFail START");


        String id = String.valueOf(UUID.randomUUID());

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, id);

        provider.createPresentation(args);
        Result result = provider.createPresentation(args);

        assertNotEquals(result.getStatus(), Status.success);
        assertEquals(result.getStatus(), Status.error);
        log.debug("{TEST} createPresentationFail END");
    }


    @Test
    void createPresentationFromTemplateSuccess () {
        log.debug("{TEST} createPresentationFromTemplateSuccess START");
        UUID templateId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID elementId = UUID.randomUUID();

        makeRectangleWithId(provider, elementId, slideId, templateId);

        HashMap params = new HashMap();
        params.put(ConstantsField.TEMPLATE_ID, String.valueOf(templateId));
        Result resultCreateFromTemplate = provider.createPresentation(params);
        assertEquals(Status.success, resultCreateFromTemplate.getStatus());

        log.debug("{TEST} createPresentationFromTemplateSuccess END");
    }

    @Test
    void createPresentationFromTemplateFail () {
        log.debug("{TEST} createPresentationFromTemplateFail START");
        UUID templateId = UUID.randomUUID();

//        makeRectangleWithId(provider, elementId, slideId, templateId); Create presentation with not existing templateId

        HashMap params = new HashMap();
        params.put(ConstantsField.TEMPLATE_ID, String.valueOf(templateId));
        Result resultCreateFromTemplate = provider.createPresentation(params);
        assertEquals(Status.error, resultCreateFromTemplate.getStatus());

        log.debug("{TEST} createPresentationFromTemplateFail END");
    }


    @Test
    void getPresentationWithOptionsSuccess () throws IOException {
        log.debug("{TEST} getPresentationWithOptionsSuccess START");
        UUID presentationId = UUID.randomUUID();

        makePresentationWithId(provider, presentationId);


        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(presentationId));
        args.put(ConstantsField.WITH_SLIDES, Constants.TRUE_VALUE);
        args.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);
        args.put(ConstantsField.WITH_COMMENTS, Constants.TRUE_VALUE);
        args.put(ConstantsField.WITH_MARKS, Constants.TRUE_VALUE);

        Result getPresResult = provider.getPresentationById(args);
        log.debug(ConstantsInfo.PRESENTATION + getPresResult);
        Optional optionalPresentation = (Optional) getPresResult.getReturnValue();
        if (optionalPresentation.isPresent()) {
            Presentation presentation = (Presentation) optionalPresentation.get();
            assertEquals(presentation.getId(), presentationId);
            assertTrue(Status.success == getPresResult.getStatus());
        }

        log.debug("{TEST} getPresentationWithOptionsSuccess START");
    }

    @Test
    void getPresentationWithOptionsFail () throws IOException {
        log.debug("{TEST} getPresentationWithOptionsFail START");
        HashMap args = new HashMap();
        Result result = provider.getPresentationById(args);

        assertTrue(Status.error == result.getStatus());

        log.debug("{TEST} getPresentationWithOptionsFail START");
    }

    @Test
    void editPresentationOptionsSuccess() throws IOException {
        log.debug("{TEST} editPresentationOptionsSuccess START");

        UUID id = UUID.randomUUID();
        Result createResult = makePresentationWithId(provider, id);

        if (createResult.getStatus() == Status.success) {
            HashMap arguments = new HashMap();
            arguments.put(ConstantsField.NAME, "My presentation");
            arguments.put(ConstantsField.FILL_COLOR, "#403add");
            arguments.put(ConstantsField.FONT_FAMILY, "Times New Roman");
            arguments.put(ConstantsField.ID, String.valueOf(id));

            assertEquals(provider.editPresentationOptions(arguments).getStatus(), Status.success);

            Optional<Presentation> optionalEditedPresentation = XMLCommonMethods.getInstanceById(CollectionType.presentation, arguments);

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
    void editPresentationOptionsFail() throws IOException {
        log.debug("{TEST} editPresentationOptionsFail START");

        UUID id = UUID.randomUUID();

        HashMap arguments = new HashMap();
        arguments.put(ConstantsField.NAME, "My presentation");
        arguments.put(ConstantsField.FILL_COLOR, "#403add");
        arguments.put(ConstantsField.FONT_FAMILY, "Times New Roman");
        arguments.put(ConstantsField.ID, String.valueOf(id));

        assertEquals(provider.editPresentationOptions(arguments).getStatus(), Status.error);
        log.debug("{TEST} editPresentationOptionsFail END");
    }

    @Test
    void removePresentationByIdSuccess() throws IOException {
        log.debug("{TEST} removePresentationByIdSuccess START");
        try {

            UUID id = UUID.randomUUID();

            Result createResult = makePresentationWithId(provider, id);

            if (createResult.getStatus() == Status.success) {
                HashMap args = new HashMap();
                args.put(ConstantsField.ID, String.valueOf(id));
                Result removeResult = provider.removePresentationById(args);
                Result resultGetPresentation = provider.getPresentationById(args);
                assertEquals(removeResult.getStatus(), Status.success);
                assertEquals(resultGetPresentation.getStatus(), Status.error);
            } else {
                fail();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        }
        log.debug("{TEST} removePresentationByIdSuccess END");
    }

    @Test
    void removePresentationByIdFail() {
        log.debug("{TEST} removePresentationByIdFail START");
        try {
            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
            Result removeResult = provider.removePresentationById(args);
            assertEquals(removeResult.getStatus(), Status.error);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error(e);
        }
        log.debug("{TEST} removePresentationByIdFail END");
    }


    @Test
    void getPresentationSlidesSuccess() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        UUID presId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID elementId = UUID.randomUUID();
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presId));
        args.put(ConstantsField.ID, String.valueOf(presId));
        args.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);
        makeRectangleWithId(provider, elementId, slideId, presId);

        Result getSlidesResult = provider.getPresentationSlides(args);
        assertEquals(getSlidesResult.getStatus(), Status.success);

        Optional optionalList = (Optional) getSlidesResult.getReturnValue();
        if (optionalList.isPresent()) {
            ArrayList list = (ArrayList) optionalList.get();

            Slide firstSlide = (Slide) list.get(0);
            ArrayList elements = firstSlide.getElements();

            assertTrue(list.size() > 0);
            assertTrue(elements.size() > 0);
        } else {
            fail();
        }
        log.debug("{TEST} getPresentationSlidesSuccess END");
    }

    @Test
    void getPresentationSlidesFail() {
        log.debug("{TEST} getPresentationSlidesFail START");
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
    void createPresentationSlideFail() {
        log.debug("{TEST} createPresentationSlideFail START");
        assertTrue(provider.createPresentationSlide(new HashMap()).getStatus() == Status.error);
        log.debug("{TEST} createPresentationSlideFail END");
    }

    @Test
    void removePresentationSlideByIdSuccess() {
        log.debug("{TEST} removePresentationSlideByIdSuccess START");


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
            assertEquals(provider.getSlideById(args).getStatus(), Status.error);
        }
        log.debug("{TEST} removePresentationSlideByIdSuccess END");
    }

    @Test
    void removePresentationSlideByIdFail() {
        log.debug("{TEST} removePresentationSlideByIdSuccess START");

        assertEquals(Status.error, provider.removePresentationSlideById(new HashMap()).getStatus());
        log.debug("{TEST} removePresentationSlideByIdSuccess END");
    }

    @Test
    void editPresentationSlideByIdSuccess() {
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        String name = "Test name";
        int index = 1;
        makePresentationWithId(provider, presentationId);
        Result resultCreateSlide = makeSlideWithId(provider, slideId, presentationId);
        if (resultCreateSlide.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.ID, String.valueOf(slideId));
            args.put(ConstantsField.NAME, name);
            args.put(ConstantsField.INDEX, String.valueOf(index));
            Result resultEditSlide = provider.editPresentationSlideById(args);
            assertEquals(resultEditSlide.getStatus(), Status.success);
            if (Status.success == resultEditSlide.getStatus()) {
                Result resultGetSlide = provider.getSlideById(args);
                if (Status.success == resultGetSlide.getStatus()) {
                    Optional optional = (Optional) resultGetSlide.getReturnValue();
                    if (optional.isPresent()) {
                        Slide slide = (Slide) optional.get();
                        assertEquals(slide.getName(),  name);
                        assertEquals(slide.getIndex(), index);
                    } else {
                        fail();
                    }
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
    void getSlideByIdSuccess () {
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID elementId = UUID.randomUUID();

        makeRectangleWithId(provider, elementId, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ID, String.valueOf(slideId));
        args.put(ConstantsField.WITH_ELEMENTS, Constants.TRUE_VALUE);

        assertTrue(provider.getSlideById(args).getStatus() == Status.success);
    }


    @Test
    void getSlideByIdError () {
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();

        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ID, String.valueOf(slideId));

        assertTrue(provider.getSlideById(args).getStatus() == Status.success);
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
                Optional optionalComments = (Optional) resultGetComments.getReturnValue();
                ArrayList comments = (ArrayList) optionalComments.get();
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


                Optional optionalList = (Optional) resultGetPresentationComments.getReturnValue();
                ArrayList comments = (ArrayList) optionalList.get();
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
        String text = "Тестовый измененный текст";
        String role = String.valueOf(Role.editor);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.TEXT, text);
            args.put(ConstantsField.ROLE, role);
            Result resultCommentPresentation = provider.commentPresentation(args);
            args.put(ConstantsField.ID, String.valueOf(resultCommentPresentation.getReturnValue()));
            Result resultEditComment = provider.editPresentationComment(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);

            assertTrue(resultCommentPresentation.getStatus() == Status.success);
            assertTrue(resultGetPresentationComments.getStatus() == Status.success);
            assertTrue(resultEditComment.getStatus() == Status.success);
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
            args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
            Result resultEditComment = provider.editPresentationComment(args);
            Result resultGetPresentationComments = provider.getPresentationComments(args);

            assertFalse(Status.success == resultGetPresentationComments.getStatus());
            assertFalse(Status.success == resultEditComment.getStatus());
        } else {
            fail();
        }
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
        Optional optional = (Optional) resultGet.getReturnValue();
        ArrayList list = (ArrayList) optional.get();
        assertTrue(!list.isEmpty());

        log.info("{ getSlideElementByIdShapeSuccess } END");
    }



    @Test
    void getSlideElementsFail () {
        log.info("{ getSlideElementsFail } START");

        Result resultGet = provider.getSlideElements(new HashMap());

        assertEquals(Status.error, resultGet.getStatus());

        log.info("{ getSlideElementsFail } END");
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

    @Test
    void addElementInSlideContentSuccess() {
        log.info("{ addElementInSlideContentSuccess } START");
        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        makeCustomContent(provider, id, slideId, presentationId);

        log.info("{ addElementInSlideContentSuccess } END");
    }

    @Test
    void getSlideElementsContentSuccess () {
        log.info("{ getSlideElementsContentSuccess } START");

        UUID presentationId = UUID.randomUUID();
        UUID slideId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        makeCustomContent(provider, id, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));

        Result resultGet = provider.getSlideElements(args);
        Optional optional = (Optional) resultGet.getReturnValue();

        assertEquals(Status.success, resultGet.getStatus());
        ArrayList list = (ArrayList) optional.orElse(new ArrayList());
        assertTrue(!list.isEmpty());

        log.info("{ getSlideElementsContentSuccess } END");
    }

    ////////////////

    @Test
    void addCustomRectangleInSlideFail() {
        log.info("{ addCustomRectangleInSlideFail } START");

        Result result = provider.addElementInSlide(new HashMap());

        assertTrue(Status.error == result.getStatus());

        log.info("{ addCustomRectangleInSlideFail } END");
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
            Optional optional = (Optional) addResult.getReturnValue();
            Shape testShape = (Shape) optional.get();

            HashMap testData = getUpdatedShape(testShape.getId(), slideId, presentationId);

            Shape updatedShape = (Shape) testData.get("shape");
            Result result = provider.editSlideElement((HashMap) testData.get("args"));

            Result resultGet = provider.getSlideElementById((HashMap) testData.get("args"));

            Optional optionalGet = (Optional) resultGet.getReturnValue();

            if (optionalGet.isPresent()) {
                Shape shape = (Shape) optionalGet.get();

                log.debug(ConstantsInfo.SHAPE + shape);
                assertEquals(shape.getFigure(), updatedShape.getFigure());
                assertEquals(shape.getStyle().toString(), updatedShape.getStyle().toString());
                assertEquals(shape.getText(), updatedShape.getText());
                assertEquals(shape.getName(), updatedShape.getName());
                assertEquals(shape.getLayout().toString(), updatedShape.getLayout().toString());
            } else {
                fail();
            }

            assertTrue(Status.success == result.getStatus());
        }


        log.info("{ editCustomRectangleInSlideSuccess } END");
    }

    @Test
    void editCustomRectangleInSlideFail() {
        log.info("{ editCustomRectangleInSlideFail } START");

        Result result = provider.editSlideElement(new HashMap());
        assertTrue(Status.error == result.getStatus());
        log.info("{ editCustomRectangleInSlideFail } END");
    }

    @Test
    void removeSlideElementByIdSuccess() {
        log.info("{ removeSlideElementByIdSuccess } START");
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
        assertTrue(resultRemove.getStatus() == Status.success);

        log.info("{ removeSlideElementByIdSuccess } END");
    }

    @Test
    void removeSlideElementShapeSuccess() {
        log.info("{ removeSlideElementShapeSuccess } START");


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

        log.info("{ removeSlideElementShapeSuccess } END");
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

    ////

    @Test
    void rateByMarkSuccess() {
        log.info("{ rateByMarkSuccess } START");

        UUID presentationId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.MARK, "bad");
        Result resultRate = provider.rateByMark(args);

        assertTrue(Status.success == resultRate.getStatus());

        log.info("{ rateByMarkSuccess } END");
    }

    @Test
    void rateByMarkFail() {
        log.info("{ rateByMarkFail } START");


        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        args.put(ConstantsField.MARK, 123);
        Result resultRate = provider.rateByMark(args);

        assertTrue(Status.error == resultRate.getStatus());

        log.info("{ rateByMarkFail } END");
    }


    @Test
    void getPresentationMarksSuccess() {
        log.info("{ getPresentationMarksSuccess } START");

        UUID presentationId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.MARK, "bad");
        Result resultRate = provider.rateByMark(args);

        Result result = provider.getPresentationMarks(args);

        assertTrue(Status.success == result.getStatus());
        log.info("{ getPresentationMarksSuccess } END");
    }

    @Test
    void getPresentationMarksFail() {
        log.info("{ getPresentationMarksSuccess } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        Result result = provider.getPresentationMarks(args);

        assertTrue(Status.error == result.getStatus());
        log.info("{ getPresentationMarksSuccess } END");
    }

    @Test
    void removePresentationMarkByIdSuccess() {

        UUID presentationId = UUID.randomUUID();
        UUID markId = UUID.randomUUID();

        makePresentationMark(provider, markId, presentationId, Mark.good);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ID, String.valueOf(markId));

        assertEquals(provider.removePresentationMarkById(args).getStatus(), Status.success);
    }

    @Test
    void removePresentationMarkByIdFail() {
        assertEquals(provider.removePresentationMarkById(new HashMap()).getStatus(), Status.error);
    }

    @Test
    void getMarkByIdSuccess() {
        UUID presentationId = UUID.randomUUID();
        UUID markId = UUID.randomUUID();

        makePresentationMark(provider, markId, presentationId, Mark.good);

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(markId));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        assertEquals(provider.getMarkById(args).getStatus(), Status.success);

        Optional returnValue = (Optional) provider.getMarkById(args).getReturnValue();
        assertTrue(returnValue.isPresent());
    }

    @Test
    void getMarkByIdFail() {
        assertEquals(provider.getMarkById(new HashMap()).getStatus(), Status.error);
    }

    @Test
    void editPresentationMarkSuccess() {

        UUID presentationId = UUID.randomUUID();
        UUID markId = UUID.randomUUID();

        makePresentationMark(provider, markId, presentationId, Mark.bad);


        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ID, String.valueOf(markId));
        args.put(ConstantsField.MARK, String.valueOf(Mark.excellent));
        assertEquals(provider.editPresentationMark(args).getStatus(), Status.success);

        Result resultGetMark = provider.getMarkById(args);

        assertEquals(resultGetMark.getStatus(), Status.success);

        Optional optional = (Optional) resultGetMark.getReturnValue();

        if (optional.isPresent()){
            Assessment assessment = (Assessment) optional.get();
            assertEquals(assessment.getMark(), Mark.excellent);

        } else {
            fail();
        }
    }

    @Test
    void editPresentationMarkFail() {

        UUID presentationId = UUID.randomUUID();
        UUID markId = UUID.randomUUID();

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.ID, String.valueOf(markId));
        args.put(ConstantsField.MARK, String.valueOf(Mark.excellent));

        assertEquals(provider.editPresentationMark(args).getStatus(), Status.error);
    }
}
