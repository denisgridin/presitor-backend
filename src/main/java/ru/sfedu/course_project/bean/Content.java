package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.FontConverter;
import ru.sfedu.course_project.converters.StyleConverter;

import java.io.Serializable;

public class Content extends Element implements Serializable {

    @CsvCustomBindByName(column = "font", converter = FontConverter.class)
    private Font font;

    @CsvBindByName
    private String text;

    public Content () {}

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
    public String toString() {
        return "Content{" +
                "font=" + font +
                ", text='" + text + '\'' +
                '}';
    }
}
