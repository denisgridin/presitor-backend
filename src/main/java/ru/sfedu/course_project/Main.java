package ru.sfedu.course_project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.api.DataProviderXML;
import ru.sfedu.course_project.enums.DataType;
import ru.sfedu.course_project.tools.Runner;

import java.io.IOException;
import java.util.*;

public class Main {
    public static Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws RuntimeException {
        try {
            List<String> arguments = Arrays.asList(args);
            HashMap<String, String> params = parseParameters(arguments);
            if (params == null) {
                return;
            }
            log.warn("arguments " + params);
            String datatype = params.get("datatype");
            log.info("Command line parameters: " + params.entrySet());
            DataProvider provider = createDataProvider(datatype);
            Runner runner = new Runner(provider);
            runner.run(params.get("method"), params);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("Error in main function");
        }
    }

    private static HashMap<String, String> parseParameters (List args) {
        try {
            HashMap params = new HashMap();
            args.stream().forEach(el -> {
                List <String> items = Arrays.asList(el.toString().split("="));
                params.put(items.get(0), items.get(1));
            });
            if (params.get("role") != null && params.get("method") != null && params.get("datatype") != null) {
                return params;
            } else {
                log.error("One or more command line parameters was not provided: role, method, datatype!"); // TODO return error exception
                return null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Unable to get command line parameters"); // TODO return error exception
            return null;
        }
    }

    private static DataProvider createDataProvider (String type) {
        try {
            DataType dataType = DataType.valueOf(type);
            log.info(String.format("Attempt to create %s data provider", dataType));
            switch (dataType) {
                case csv: {
                    DataProvider provider = new DataProviderCSV();
                    log.info(String.format("Data provider was created: %s", dataType));
                    return provider;
                }
                case xml: {
                    DataProvider provider = new DataProviderXML();
                    log.info(String.format("Data provider was created: %s", dataType));
                    return provider;
                }
                default: {
                    return null;
                }
            }
        } catch (IllegalArgumentException e) {
            log.error(e);
            log.error("Unable to create %s data provider");
            return null;
        }
    }

//    public static void createPresentation (DataProvider provider) throws IOException {
//        Layout layout = new Layout(10, 20, 200, 100, 0);
//        Style style = new Style("#808080", "none", 100, "transparent", "0px", "0px", BorderStyle.none);
//        Font font = new Font("Roboto", "1.2rem", "none", "none", FontCase.uppercase);
//        Content content = new Content(2, "text content", ElementType.content, layout, font, "Text контента");
//        Element shape = new Shape(1, "Shape", ElementType.shape, layout, style, content, Figure.rectangle);
//        Element shape2 = new Shape(2, "Shape 2", ElementType.shape, layout, style, null, null);
//
//        List<Element> elements = new ArrayList();
//        elements.add(shape);
//        elements.add(shape2);
//        Slide slide = new Slide(1,"First slide", 0, elements);
//        Slide slide2 = new Slide(2,"Second slide", 1, elements);
//
//        List<Slide> slides = new ArrayList();
//        slides.add(slide);
//        slides.add(slide2);
//
//        Feedback feedback = new Comment(UUID.randomUUID(), Role.editor, "Текст комментария");
//        Assessment assessment = new Assessment(UUID.randomUUID(), Role.guest, Mark.good);
//        log.debug(feedback);
//        List<Feedback> feedbacks = new ArrayList();
//        feedbacks.add(feedback);
//        feedbacks.add(assessment);
//
//        HashMap arguments = new HashMap();
//        long id = 1;
//        arguments.put("id", id);
//        arguments.put("name", "Presentation");
//        arguments.put("slides", slides);
//        arguments.put("feedbacks", feedbacks);
//        arguments.put("fillColor", "#333333");
//        arguments.put("fontFamily", "Roboto");
//        log.debug(arguments);
//        provider.createPresentation(arguments);
//    }
}
