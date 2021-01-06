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
import ru.sfedu.course_project.api.DataProviderXML;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
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

        if (Status.success == result.getStatus()) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            args.put(ConstantsField.ID, String.valueOf(presentationId));
            Result resultGetPresentation = provider.getPresentationById(args);
            log.debug("Find presentation: " + presentationId);
            assertSame(resultGetPresentation.getStatus(), Status.success);

            Presentation foundPresentation = (Presentation) resultGetPresentation.getReturnValue();
            log.debug("Found: presentation: " + foundPresentation.getId());
            assertEquals(presentationId, foundPresentation.getId());
        } else {
            fail();
        }
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
        DataProvider provider = new DataProviderXML();
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
}