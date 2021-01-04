package ru.sfedu.course_project;

import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.utils.ConstantsField;

import javax.swing.border.Border;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static final String SOURCE = ConstantsField.NAME;
    public static final String CSV_PATH="csv_path";
    public static final String UUID_REGEXP="uuid_regexp";
    public static final String DATA_PATH="dataPath";
    public static final String DATABASE_PATH="database_path";
    public static final String DATABASE_USER="database_user";
    public static final String DATABASE_PASSWORD="database_password";
    public static final String DATABASE_PROTOCOL="database_protocol";

    public final static Map FIELD_REGEXP = Stream.of(new Object[][] {
            { ConstantsField.X, "x=\\d*" },
            { ConstantsField.Y, "y=\\d*" },
            { ConstantsField.WIDTH, "width=\\d*" },
            { ConstantsField.HEIGHT, "height=\\d*" },
            { ConstantsField.ROTATION, "rotation=\\d{1,}"},

            { ConstantsField.FILL_COLOR, "fillColor='\\w{1,}'" },
            { ConstantsField.BOX_SHADOW, "boxShadow=[^,]{1,}" },
            { ConstantsField.OPACITY, "opacity=\\d*" },
            { ConstantsField.BORDER_COLOR, "borderColor='\\w{1,}'" },
            { ConstantsField.BORDER_RADIUS, "borderRadius='\\w{1,}'" },
            { ConstantsField.BORDER_WIDTH, "borderWidth='\\w{1,}'" },
            { ConstantsField.BORDER_STYLE, "borderStyle=\\w{1,}" },

            { ConstantsField.FONT_FAMILY, "fontFamily=[^,]{1,}" },
            { ConstantsField.FONT_SIZE, "fontSize=[^,]{1,}" },
            { ConstantsField.LETTER_SPACING, "letterSpacing=[^,]{1,}" },
            { ConstantsField.LINE_SPACING, "lineSpacing=[^,]{1,}" },
            { ConstantsField.FONT_CASE, "fontCase=\\w{1,}" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));



    public final static Map DEFAULT_PRESENTATION = Stream.of(new Object[][] {
            { ConstantsField.ID, String.valueOf(UUID.randomUUID()) },
            { ConstantsField.NAME, "New presentation" },
            { ConstantsField.FILL_COLOR, "#ffffff" },
            { ConstantsField.FONT_FAMILY, "Roboto" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));


    public final static Map DEFAULT_SLIDE = Stream.of(new Object[][] {
            { ConstantsField.ID, String.valueOf(UUID.randomUUID()) },
            { ConstantsField.NAME, "Slide" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));


    public final static Map DEFAULT_ELEMENT = Stream.of(new Object[][] {
            { ConstantsField.ID, String.valueOf(UUID.randomUUID()) },
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));


    public final static Map DEFAULT_SHAPE = Stream.of(new Object[][] {
            { ConstantsField.NAME, "Shape" },
            { ConstantsField.TEXT, "" }
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));

    public final static Map DEFAULT_CONTENT = Stream.of(new Object[][] {
            { ConstantsField.NAME, "Content" },
            { ConstantsField.TEXT, "Content" }
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

        layout.setHeight( Integer.valueOf((String) args.getOrDefault(ConstantsField.HEIGHT, "100")));
        layout.setWidth( Integer.valueOf((String) args.getOrDefault(ConstantsField.WIDTH, "100")));
        layout.setRotation( Integer.valueOf((String) args.getOrDefault(ConstantsField.ROTATION, "0")));

        layout.setX(Integer.valueOf((String) args.getOrDefault(ConstantsField.X, "0")));
        layout.setY(Integer.valueOf((String) args.getOrDefault(ConstantsField.Y, "0")));
        return layout;
    };

    public final static Font DEFAULT_FONT (HashMap args) {
        Font font = new Font();

        font.setFontFamily((String) args.getOrDefault(ConstantsField.FONT_FAMILY,"Roboto"));
        font.setFontCase(FontCase.valueOf((String) args.getOrDefault(ConstantsField.FONT_CASE, String.valueOf(FontCase.normal))));
        font.setLetterSpacing((String) args.getOrDefault(ConstantsField.LETTER_SPACING,""));
        font.setLineSpacing((String) args.getOrDefault(ConstantsField.LINE_SPACING,""));
        font.setFontSize((String) args.getOrDefault(ConstantsField.FONT_SIZE,"14px"));

        return font;
    }

    public final static Style DEFAULT_STYLE (HashMap args) {
        Style style = new Style();

        style.setBorderColor((String) args.getOrDefault(ConstantsField.BORDER_COLOR,"transparent"));
        style.setBorderRadius((String) args.getOrDefault(ConstantsField.BORDER_RADIUS,"0"));
        style.setBorderStyle(BorderStyle.valueOf(String.valueOf(args.getOrDefault(ConstantsField.BORDER_STYLE, BorderStyle.none))));
        style.setBorderWidth((String) args.getOrDefault(ConstantsField.BORDER_WIDTH,"0"));
        style.setFillColor((String) args.getOrDefault(ConstantsField.FILL_COLOR,"transparent"));
        style.setOpacity((String) args.getOrDefault(ConstantsField.OPACITY,"1"));
        style.setBoxShadow((String) args.getOrDefault(ConstantsField.BOX_SHADOW,""));

        return style;
    }
}
