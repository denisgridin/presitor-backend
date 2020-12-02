package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.FeedbackConverter;
import ru.sfedu.course_project.converters.FontConverter;
import ru.sfedu.course_project.converters.ListConverter;
import ru.sfedu.course_project.tools.ArgsValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Presentation implements Serializable {
    @CsvBindByName
    private long id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private List<Slide> slides;

    @CsvBindByName
    private String fillColor;

    @CsvBindByName
    private String fontFamily;

    @CsvBindAndSplitByName(column = "feedbacks", elementType = List.class, splitOn = ",", writeDelimiter = ";", converter = FeedbackConverter.class)
    private List<Feedback> feedbacks;

    public String[] requiredArgs = { "id" };

    public Presentation () {}

    public Presentation (HashMap args){
        this.id = (long) args.get("id");
        this.name = (String) args.get("name");
        this.slides = (List<Slide>) args.get("slides");;
        this.fillColor = (String) args.get("fillColor");;
        this.fontFamily = (String) args.get("fontFamily");;
        this.feedbacks = (List<Feedback>) args.get("feedbacks");;
    }

    public Presentation (long id, String name, List<Slide> slides, String fillColor, String fontFamily, List<Feedback> feedbacks) {
        this.id = id;
        this.name = name;
        this.slides = slides;
        this.fillColor = fillColor;
        this.fontFamily = fontFamily;
        this.feedbacks = feedbacks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
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

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Presentation that = (Presentation) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(slides, that.slides) &&
                Objects.equals(fillColor, that.fillColor) &&
                Objects.equals(fontFamily, that.fontFamily) &&
                Objects.equals(feedbacks, that.feedbacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, slides, fillColor, fontFamily, feedbacks);
    }

    @Override
    public String toString() {
        return "Presentation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slides=[" + slides +
                "], fillColor='" + fillColor + '\'' +
                ", fontFamily=" + fontFamily +
                ", feedbacks=[" + feedbacks +
                "]}";
    }
}
