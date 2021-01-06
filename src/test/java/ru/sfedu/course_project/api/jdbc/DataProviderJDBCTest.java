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

        assertSame(getPresentationResult.getStatus(), Status.success);
        assertSame(result.getStatus(), Status.success);
        assertTrue(result.getReturnValue() instanceof UUID);
    }

    @Test
    void createPresentationFail () throws IOException {
        HashMap args = new HashMap();
        Result result = provider.createPresentation(new HashMap());
        assertSame(result.getStatus(), Status.error);
    }

    @Test
    void getPresentationByIdSuccess () throws IOException {
        Result result = makeRandomPresentation(provider);

        if (Status.success == result.getStatus()) {
            HashMap args = new HashMap();
            UUID presentationId = (UUID) result.getReturnValue();
            args.put(ConstantsField.ID, String.valueOf(presentationId));
            Result resultGetPresentation = provider.getPresentationById(args);

            assertSame(resultGetPresentation.getStatus(), Status.success);

            Presentation foundPresentation = (Presentation) resultGetPresentation.getReturnValue();
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
}