package ru.sfedu.course_project;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static String SOURCE = "NAME";
    public static String CSV_PATH="csv_path";
    public static String UUID_REGEXP="uuid_regexp";
    public final static Map DEFAULT_PRESENTATION = Stream.of(new Object[][] {
            { "id", UUID.randomUUID() },
            { "name", "New presentation" },
            { "slides", new ArrayList() },
            { "feedbacks", new ArrayList() },
            { "fillColor", "#ffffff" },
            { "fontFamily", "Roboto" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));;
    public final static List<String> PUBLIC_METHODS = Arrays.asList(new String[]{ "getPresentationById" });
}
