package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.Converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Slide implements Serializable {
    @CsvBindByName
    private long id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private int index;

    @CsvCustomBindByName(column = "elements", converter = Converter.class)
    private List<Element> elements;

    public Slide (long id, String name, int index, List<Element> elements) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.elements = elements;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slide slide = (Slide) o;
        return id == slide.id &&
                index == slide.index &&
                Objects.equals(name, slide.name) &&
                Objects.equals(elements, slide.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, index, elements);
    }

    @Override
    public String toString() {
        return "Slide{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", elements=" + elements +
                '}';
    }
}
