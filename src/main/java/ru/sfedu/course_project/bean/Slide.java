package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import ru.sfedu.course_project.converters.UUIDConverter;
import ru.sfedu.course_project.utils.ConstantsField;


import java.io.Serializable;
import java.util.*;

@Root
public class Slide implements Serializable {
    @Attribute
    @CsvCustomBindByName(column = ConstantsField.ID, converter = UUIDConverter.class)
    private UUID id;

    @Attribute
    @CsvBindByName
    private String name;

    @Attribute
    @CsvBindByName
    private int index;

    @Attribute
    @CsvCustomBindByName(column = ConstantsField.PRESENTATION_ID, converter = UUIDConverter.class)
    private UUID presentationId;

    @CsvIgnore
    private boolean elementsSet = false;

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
        this.elementsSet = true;
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

        String elementsView = elementsSet ? ", elements=" + elements : "";

        return "Slide{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", presentationId=" + presentationId +
                elementsView +
                '}';
    }
}
