package ru.sfedu.course_project;

import ru.sfedu.course_project.bean.*;

import javax.swing.border.Border;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static String SOURCE = "NAME";
    public static String CSV_PATH="csv_path";
    public static String UUID_REGEXP="uuid_regexp";
    public static String DATA_PATH="dataPath";

    public final static Map FIELD_REGEXP = Stream.of(new Object[][] {
            { "x", "x=\\d*" },
            { "y", "y=\\d*" },
            { "width", "width=\\d*" },
            { "height", "height=\\d*" },
            { "rotation", "rotation=\\d*" },

            { "fillColor", "fillColor=\\d*" },
            { "boxShadow", "boxShadow=\\d*" },
            { "opacity", "opacity=\\d*" },
            { "borderColor", "borderColor=\\d*" },
            { "borderRadius", "borderRadius=\\d*" },
            { "borderWidth", "borderWidth=\\d*" },
            { "borderStyle", "borderStyle=\\d*" },

            { "family", "family=\\d*" },
            { "size", "size=\\d*" },
            { "letterSpacing", "letterSpacing=\\d*" },
            { "lineSpacing", "lineSpacing=\\d*" },
            { "case", "case=\\d*" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));



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


    public final static Map DEFAULT_ELEMENT = Stream.of(new Object[][] {
            { "id", String.valueOf(UUID.randomUUID()) },
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));


    public final static Map DEFAULT_SHAPE = Stream.of(new Object[][] {
            { "name", "Shape" },
            { "text", "" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));

    public final static Map DEFAULT_CONTENT = Stream.of(new Object[][] {
            { "name", "Content" },
            { "text", "Content" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));


    public final static List<String> PUBLIC_METHODS = Arrays.asList(new String[]{
            "getPresentationById",
            "getPresentations",
            "commentPresentation"
    });

    public final static String MSG_SUCCESS_RESULT = "[%s] Result: %s";
    public final static String MSG_ERROR_DATA_SOURCE = "Unable to get %s data source";

    public final static Layout DEFAULT_LAYOUT (HashMap args) {
        Layout layout = new Layout();

        layout.setHeight( Integer.valueOf((String) args.getOrDefault("height", "100")));
        layout.setWidth( Integer.valueOf((String) args.getOrDefault("width", "100")));
        layout.setRotation( Integer.valueOf((String) args.getOrDefault("rotation", "0")));

        layout.setX(Integer.valueOf((String) args.getOrDefault("x", "0")));
        layout.setY(Integer.valueOf((String) args.getOrDefault("y", "0")));
        return layout;
    };

    public final static Font DEFAULT_FONT (HashMap args) {
        Font font = new Font();

        font.setFamily((String) args.getOrDefault("family","Roboto"));
        font.setFontCase(FontCase.normal);
        font.setLetterSpacing((String) args.getOrDefault("letterSpacing",""));
        font.setLineSpacing((String) args.getOrDefault("lineSpacing",""));
        font.setSize((String) args.getOrDefault("size","14px"));

        return font;
    }

    public final static Style DEFAULT_STYLE (HashMap args) {
        Style style = new Style();

        style.setBorderColor((String) args.getOrDefault("borderColor","transparent"));
        style.setBorderRadius((String) args.getOrDefault("borderRadius","0"));
        style.setBorderStyle(BorderStyle.none);
        style.setBorderWidth((String) args.getOrDefault("borderWidth","0"));
        style.setFillColor((String) args.getOrDefault("fillColor","transparent"));
        style.setOpacity((String) args.getOrDefault("opacity","1"));
        style.setBoxShadow((String) args.getOrDefault("boxShadow",""));

        return style;
    }
}
