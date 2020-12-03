package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.ListIdsConverter;
import ru.sfedu.course_project.converters.UUIDConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Slide implements Serializable {
    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private int index;

    @CsvBindAndSplitByName(column = "slides", elementType = List.class, splitOn = ",", writeDelimiter = ";", converter = ListIdsConverter.class)
    private List<UUID> elements;

    public Slide (UUID id, String name, int index, List<UUID> elements) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.elements = elements;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<UUID> getElements() {
        return elements;
    }

    public void setElements(ArrayList<UUID> elements) {
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
