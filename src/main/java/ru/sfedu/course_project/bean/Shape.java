package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public class Shape extends Element implements Serializable {
    public Shape(long id, String name, ElementType elementType, Layout layout, Style style, Content content, Figure figure) {
        super(id, name, elementType, layout);
        this.style = style;
        this.content = content;
        this.figure = figure;
    }

    @CsvBindByName
    private Style style;

    @CsvBindByName
    private Content content;

    @CsvBindByName
    private Figure figure;

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Shape shape = (Shape) o;
        return Objects.equals(style, shape.style) &&
                Objects.equals(content, shape.content) &&
                figure == shape.figure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), style, content, figure);
    }

    @Override
    public String toString() {
        return "Shape{" +
                "style=" + style +
                ", content=" + content +
                ", figure=" + figure +
                '}';
    }
}
