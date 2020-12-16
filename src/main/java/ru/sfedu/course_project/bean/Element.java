package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.UUIDConverter;

import java.util.Objects;
import java.util.UUID;

public abstract class Element {
    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private ElementType elementType;

    @CsvBindByName
    private Layout layout;

    public Element() { }

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

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return id == element.id &&
                Objects.equals(name, element.name) &&
                Objects.equals(elementType, element.elementType) &&
                Objects.equals(layout, element.layout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, elementType, layout);
    }
}
