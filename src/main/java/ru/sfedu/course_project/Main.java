package ru.sfedu.course_project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.DataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
//        DataProviderCSV provider = new DataProviderCSV();
//        getPresentation(provider, 3);
//        createPresentation(provider);
//        log.info("jar executed");
        List<String> arguments = Arrays.asList(args);
        HashMap<String, String> params = parseParameters(arguments);
        log.warn("arguments " + params);
        String datatype = params.get("datatype");
        createDataProvider(datatype);
    }

    private static HashMap<String, String> parseParameters (List args) {
        HashMap<String, String> params = new HashMap<>();
        args.stream().forEach(el -> {
            List <String> items = Arrays.asList(el.toString().split("="));
            params.put(items.get(0), items.get(1));
        });
        return params;
    }

    private static void createDataProvider (String type) {
        try {
            DataType dataType = DataType.valueOf(type);
            log.info(String.format("Attempt to create %s data provider", dataType));
            DataProvider provider = new DataProvider(dataType);
        } catch (IllegalArgumentException e) {
            log.error(e);
        }
    }

    public static Presentation getPresentation (DataProviderCSV provider, long id) throws IOException {
        Presentation presentation = provider.getPresentationById(id);
        return presentation;
    }

    public static void createPresentation (DataProviderCSV provider) throws IOException {
        Layout layout = new Layout(10, 20, 200, 100, 0);
        Style style = new Style("#808080", "none", 100, "transparent", "0px", "0px", BorderStyle.none);
        Font font = new Font("Roboto", "1.2rem", "none", "none", FontCase.uppercase);
        Content content = new Content(font, "Текст контента");
        Element shape = new Shape(1, "Shape", ElementType.shape, layout, style, content, Figure.rectangle);

        List<Element> elements = new ArrayList();
        elements.add(shape);
        Slide slide = new Slide(1,"First slide", 0, elements);

        List<Slide> slides = new ArrayList();
        slides.add(slide);

        Feedback reply = new Comment(2, 1, "Ответ на комментарий", null);
        List replies = new ArrayList();
        replies.add(reply);
        Feedback feedback = new Comment(1, 1, "Текст комментария", replies);
        List<Feedback> feedbacks = new ArrayList();
        feedbacks.add(feedback);

        provider.createPresentation(3, "Presentation_3", slides, "yellow", font, feedbacks);
    }
}
