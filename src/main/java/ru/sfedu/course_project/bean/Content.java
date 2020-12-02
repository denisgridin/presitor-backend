package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public class Content extends Element implements Serializable {

    @CsvBindByName
    private Font font;

    @CsvBindByName
    private String text;

    public Content (long id, String name, ElementType elementType, Layout layout, Font font, String text) {
        super(id, name, elementType, layout);
        this.font = font;
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return Objects.equals(font, content.font) &&
                Objects.equals(text, content.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, text);
    }

    @Override
    public String toString() {
        return "Content{" +
                "font=" + font +
                ", text='" + text + '\'' +
                '}';
    }
}
