package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.converters.ListIdsConverter;
import ru.sfedu.course_project.converters.UUIDConverter;

import java.io.Serializable;
import java.util.*;

public class Presentation implements Serializable {
    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

//    @CsvBindAndSplitByName(column = "slides", elementType = List.class, splitOn = ",", writeDelimiter = ";", converter = ListIdsConverter.class)
//    private List<Slide> slides;

    @CsvBindByName
    private String fillColor;

    @CsvBindByName
    private String fontFamily;

//    @CsvBindAndSplitByName(column = "feedbacks", elementType = List.class, splitOn = ",", writeDelimiter = ";", converter = ListIdsConverter.class)
//    private List<Feedback> feedbacks;

    public String[] requiredArgs = { "id" };

    public Presentation () {}

    public Presentation (HashMap args){
        this.validateArguments(args);
    }

    public static Logger log = LogManager.getLogger(Presentation.class);

    private void validateArguments(HashMap args) {
        Map defaults = Constants.DEFAULT_PRESENTATION;
        try {
            this.setId((UUID) args.getOrDefault("id", defaults.get("id")));
            this.setName((String) args.getOrDefault("name", defaults.get("name")));
            this.setFillColor((String) args.getOrDefault("fillColor", defaults.get("fillColor")));
            this.setFontFamily((String) args.getOrDefault("fontFamily", defaults.get("fontFamily")));
            log.debug("Arguments was successfully validated");
        } catch (RuntimeException e) {
            log.error("Unable to validate Presentation fields");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Presentation that = (Presentation) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(fillColor, that.fillColor) &&
                Objects.equals(fontFamily, that.fontFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fillColor, fontFamily);
    }

    @Override
    public String toString() {
        return "Presentation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fillColor='" + fillColor + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                '}';
    }
}
