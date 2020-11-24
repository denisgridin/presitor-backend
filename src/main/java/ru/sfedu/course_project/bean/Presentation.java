package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
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
    private Font font;

    @CsvBindByName
    private List<Feedback> feedbacks;

    public Presentation () {
    }

    public Presentation (String name) {

    }

    public Presentation (long id, String name, List<Slide> slides, String fillColor, Font font, List<Feedback> feedbacks) {
        this.id = id;
        this.name = name;
        this.slides = slides;
        this.fillColor = fillColor;
        this.font = font;
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

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
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
                Objects.equals(font, that.font) &&
                Objects.equals(feedbacks, that.feedbacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, slides, fillColor, font, feedbacks);
    }

    @Override
    public String toString() {
        return "Presentation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slides=" + slides +
                ", fillColor='" + fillColor + '\'' +
                ", font=" + font +
                ", feedbacks=[" + feedbacks +
                "]}";
    }
}
