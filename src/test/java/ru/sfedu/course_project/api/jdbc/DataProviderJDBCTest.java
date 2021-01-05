package ru.sfedu.course_project.api.jdbc;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.sfedu.course_project.TestBase;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderJDBC;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataProviderJDBCTest extends TestBase {
    private static Logger log = LogManager.getLogger(DataProviderJDBCTest.class);

    DataProvider provider = new DataProviderJDBC();

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
        HashMap args = new HashMap();
        Result result = provider.createPresentation(new HashMap());
        assertTrue(result.getStatus() == Status.error);
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
    void removePresentationByIdSuccess () throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
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
    void removePresentationByIdFail () throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        HashMap args = new HashMap();
        args.put("id", String.valueOf(UUID.randomUUID()));
        Result result = provider.removePresentationById(args);
        assertTrue(result.getStatus() == Status.error);
    }

    @Test
    void editPresentationOptionsSuccess () throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
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
    void editPresentationOptionsFail () {
        try {
            HashMap args = new HashMap();
            args.put("id", String.valueOf(UUID.randomUUID()));

            Result resultRemovePresentation = provider.editPresentationOptions(args);

            assertEquals(Status.error, resultRemovePresentation.getStatus());
        } catch (RuntimeException | IOException  e) {
            log.error(e);
        }
    }
}