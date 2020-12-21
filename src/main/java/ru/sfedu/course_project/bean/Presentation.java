package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.converters.ListIdsConverter;
import ru.sfedu.course_project.converters.UUIDConverter;
import ru.sfedu.course_project.tools.BaseClass;

import java.io.Serializable;
import java.util.*;

public class Presentation implements Serializable, BaseClass {
    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String fillColor;

    @CsvBindByName
    private String fontFamily;

//    @CsvBindAndSplitByName(column = "slides", elementType = List.class, converter = ListIdsConverter.class)
    @CsvIgnore
    private ArrayList<Slide> slides;

//    @CsvBindAndSplitByName(column = "comments", elementType = List.class, converter = ListIdsConverter.class)
    @CsvIgnore
    private ArrayList<Comment> comments;

//    @CsvBindAndSplitByName(column = "marks", elementType = List.class, converter = ListIdsConverter.class)
    @CsvIgnore
    private ArrayList<Assessment> marks;

    private Boolean slidesConnected = false;
    private Boolean commentsConnected = false;

    private Boolean getSlidesConnected () { return slidesConnected; }
    private Boolean getCommentsConnected () { return commentsConnected; }

    private void setSlidesConnected (Boolean flag) { this.slidesConnected = flag; }
    private void setCommentsConnected (Boolean flag) { this.commentsConnected = flag; }

    public Presentation () {}

    public static Logger log = LogManager.getLogger(Presentation.class);

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

    public ArrayList<Slide> getSlides() {
        return slides;
    }

    public void setSlides(ArrayList slides) {
        this.setSlidesConnected(true);
        this.slides = slides;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList comments) {
        this.setCommentsConnected(true);
        this.comments = comments;
    }

    public ArrayList<Assessment> getMarks() {
        return marks;
    }

    public void setMarks(ArrayList marks) {
        this.marks = marks;
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
        String formatReturn = "Presentation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fillColor='" + fillColor + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                ( slidesConnected ? ", slides=" + getSlides() : "" ) +
                ( commentsConnected ? ", comments=" + getComments() : "" ) +
                '}';
        return formatReturn;
    }
}
