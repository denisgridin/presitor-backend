package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.bean.Element;
import ru.sfedu.course_project.converters.FigureConverter;
import ru.sfedu.course_project.converters.RoleConverter;
import ru.sfedu.course_project.converters.StyleConverter;

import java.util.Objects;

public class Shape extends Element {

    @CsvCustomBindByName(column = "style", converter = StyleConverter.class)
    private Style style;

    @CsvBindByName
    private String text;

    @CsvCustomBindByName(column = "figure", converter = FigureConverter.class)
    private Figure figure;

    public Shape() { }

    public Style getStyle() {
        return style;
    }

    public String getText() {
        return text;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setText(String text) {
        this.text = text;
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
                Objects.equals(text, shape.text) &&
                figure == shape.figure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), style, text, figure);
    }

    @Override
    public String toString() {
        return "Shape{" +
                "style=" + style +
                ", text='" + text + '\'' +
                ", figure=" + figure +
                '}';
    }
}
