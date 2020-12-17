package ru.sfedu.course_project;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static String SOURCE = "NAME";
    public static String CSV_PATH="csv_path";
    public static String UUID_REGEXP="uuid_regexp";
    public static String DATA_PATH="dataPath";
    public final static Map DEFAULT_PRESENTATION = Stream.of(new Object[][] {
            { "id", String.valueOf(UUID.randomUUID()) },
            { "name", "New presentation" },
            { "fillColor", "#ffffff" },
            { "fontFamily", "Roboto" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
    public final static Map DEFAULT_SLIDE = Stream.of(new Object[][] {
            { "id", String.valueOf(UUID.randomUUID()) },
            { "name", "Slide" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
    public final static List<String> PUBLIC_METHODS = Arrays.asList(new String[]{
            "getPresentationById",
            "getPresentations"
    });

    public final static String MSG_SUCCESS_RESULT = "[%s] Result: %s";
    public final static String MSG_ERROR_DATA_SOURCE = "Unable to get %s data source";
}
