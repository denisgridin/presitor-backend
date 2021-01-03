package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.converters.ListIdsConverter;
import ru.sfedu.course_project.converters.UUIDConverter;
import ru.sfedu.course_project.utils.ConstantsField;


import java.io.Serializable;
import java.util.*;

public class Slide implements Serializable {
    @CsvCustomBindByName(column = ConstantsField.ID, converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private int index;

    @CsvCustomBindByName(column = ConstantsField.PRESENTATION_ID, converter = UUIDConverter.class)
    private UUID presentationId;

//    @CsvBindAndSplitByName(column = "elements", elementType = List.class, converter = ListIdsConverter.class)
    @CsvIgnore
    private ArrayList<Element> elements;

    public Slide () {}

    public static Logger log = LogManager.getLogger(Slide.class);

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

    public UUID getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(UUID presentationId) {
        this.presentationId = presentationId;
    }

    public ArrayList<Element> getElements() {
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
        return index == slide.index &&
                Objects.equals(id, slide.id) &&
                Objects.equals(name, slide.name) &&
                Objects.equals(presentationId, slide.presentationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, index, presentationId);
    }

    @Override
    public String toString() {
        return "Slide{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", presentationId=" + presentationId +
                '}';
    }
}
