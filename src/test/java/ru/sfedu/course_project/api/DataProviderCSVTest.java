package ru.sfedu.course_project.api;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.csv.CSVCommonMethods;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataProviderCSVTest extends TestBase {

    private static Logger log = LogManager.getLogger(DataProviderCSVTest.class);

    DataProviderCSV provider = new DataProviderCSV();

    @BeforeAll
    static void setTestFilePath () throws IOException {
        try {
            System.setProperty("dataPath", ConfigurationUtil.getConfigurationEntry("testDataPath"));
        } catch (IOException e) {
            log.debug(e);
        }
    }

    @Test
    void createPresentationFromTemplateSuccess () {
        HashMap args = new HashMap();
        args.put(ConstantsField.TEMPLATE_ID, "353038aa-9497-4f1a-ad1e-9e539fe2ecfd");

        Result result = provider.createPresentation(args);

        assertTrue(Status.success == result.getStatus());
    }

    @Test
    void getPresentationWithOptionsSuccess () {
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, "353038aa-9497-4f1a-ad1e-9e539fe2ecfd");
        args.put(ConstantsField.WITH_SLIDES, "true");
        args.put(ConstantsField.WITH_ELEMENTS, "true");

        Result result = provider.getPresentationById(args);

        assertTrue(Status.success == result.getStatus());
    }


    @Test
    void createPresentationSuccess() throws IOException {
        log.debug("{TEST} createPresentationSuccess START");

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));

        Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args).getReturnValue();
        if (optionalPresentation.isPresent()) {
            Presentation presentation = optionalPresentation.get();

            Result result = provider.createPresentation(args);
            String id = result.getReturnValue().toString();

            HashMap params = new HashMap();
            params.put(ConstantsField.ID, id);

            Optional<Presentation> optionalItem = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, params);

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
    void getPresentationByIdSuccess() {
        log.debug("{TEST} getPresentationByIdSuccess START");

        Result result = makeRandomPresentation(provider);
        if (Status.success == result.getStatus()) {
            HashMap args = new HashMap();
            args.put(ConstantsField.ID, String.valueOf(result.getReturnValue()));
            Result getPresResult = provider.getPresentationById(args);
            Presentation presentation = (Presentation) getPresResult.getReturnValue();
            assertEquals(presentation.getId(), result.getReturnValue());
            assertEquals(getPresResult.getStatus(), Status.success);
        } else {
            fail();
        }
        log.debug("{TEST} getPresentationByIdSuccess END");
    }

    @Test
    void getPresentationByIdFail() {
        log.debug("{TEST} getPresentationByIdFail START");

        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));

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
                args.put(ConstantsField.ID, String.valueOf(id));
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
                args.put(ConstantsField.ID, String.valueOf(UUID.randomUUID()));
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

        UUID id = UUID.randomUUID();
        Result createResult = makePresentationWithId(provider, id);

        if (createResult.getStatus() == Status.success) {
            HashMap arguments = new HashMap();
            arguments.put(ConstantsField.NAME, "My presentation");
            arguments.put(ConstantsField.FILL_COLOR, "#403add");
            arguments.put(ConstantsField.FONT_FAMILY, "Times New Roman");
            arguments.put(ConstantsField.ID, String.valueOf(id));

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
    void getPresentationSlidesSuccess() {
        log.debug("{TEST} getPresentationSlidesSuccess START");
        DataProvider provider = new DataProviderCSV();
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
        DataProvider provider = new DataProviderCSV();
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
        DataProvider provider = new DataProviderCSV();
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
        DataProvider provider = new DataProviderCSV();
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
        DataProvider provider = new DataProviderCSV();
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

            DataProvider provider = new DataProviderCSV();
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

                Optional resultGetPresentation = CSVCommonMethods.getInstanceById(Presentation.class, CollectionType.presentation, params);
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

            DataProvider provider = new DataProviderCSV();
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

            DataProvider provider = new DataProviderCSV();
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

            DataProvider provider = new DataProviderCSV();
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

        DataProvider provider = new DataProviderCSV();
        Result resultCreatePresentation = makePresentationWithId(provider, presentationId);

        if (resultCreatePresentation.getStatus() == Status.success) {
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
            args.put(ConstantsField.TEXT, "Тестовый измененный текст");
            args.put(ConstantsField.ROLE, String.valueOf(Role.editor));
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

        DataProvider provider = new DataProviderCSV();
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

    @Test
    void rateByMarkSuccess() {
        log.info("{ rateByMarkSuccess } START");

        UUID presentationId = UUID.randomUUID();
        makePresentationWithId(provider, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.MARK, "bed");
        Result resultRate = provider.rateByMark(args);

        assertTrue(Status.success == resultRate.getStatus());

        log.info("{ rateByMarkSuccess } END");
    }

    @Test
    void getPresentationMarksSuccess() {
        log.info("{ getPresentationMarksSuccess } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, "1415aa57-83e2-4076-a317-75463ea17e6b");
        Result result = provider.getPresentationMarks(args);

        assertTrue(Status.success == result.getStatus());
        log.info("{ getPresentationMarksSuccess } END");
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
    void getPresentationMarksFail() {
        log.info("{ getPresentationMarksSuccess } START");

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(UUID.randomUUID()));
        Result result = provider.getPresentationMarks(args);

        assertTrue(Status.error == result.getStatus());
        log.info("{ getPresentationMarksSuccess } END");
    }

//    @Test
//    void getCollectionsListSuccess() {
//        log.debug("getCollectionSuccess");
//
//        HashMap args = new HashMap();
//        provider.createPresentation(args);
//        Optional<List> optionalPresentationList = provider.getCollection(CollectionType.presentation, Presentation.class);
//        assertNotNull(optionalPresentationList.get());
//    }
//
//    @Test
//    void getCollectionsListFail() {
//        System.out.println("getCollection(CollectionType.presentation)Fail");
//
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