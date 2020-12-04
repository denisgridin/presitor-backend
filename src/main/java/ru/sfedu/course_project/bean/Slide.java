package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.converters.ListIdsConverter;
import ru.sfedu.course_project.converters.UUIDConverter;

import java.io.Serializable;
import java.util.*;

public class Slide implements Serializable {
    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private int index;

    @CsvCustomBindByName(column = "presentationId", converter = UUIDConverter.class)
    private UUID presentationId;

    public Slide () {}

    public Slide (HashMap args) {
        validateArguments(args);
    }

    public static Logger log = LogManager.getLogger(Slide.class);

    private void validateArguments (HashMap arguments) {
        try {
            log.info("[validateArguments] Arguments: " + arguments.entrySet());
            Map defaults = Constants.DEFAULT_SLIDE;
            log.info("[validateArguments] Default slide options: " + defaults.entrySet());
            this.setId((UUID) arguments.getOrDefault("id", defaults.get("id")));
            this.setName((String) arguments.getOrDefault("name", defaults.get("name")));
            this.setIndex((Integer) arguments.get("index"));
            this.setPresentationId(UUID.fromString((String) arguments.get("presentationId")));
        } catch (RuntimeException e) {
            log.error(e);
            log.error("[validateArguments] Unable to validate Slide arguments");
        }
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

    public UUID getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(UUID presentationId) {
        this.presentationId = presentationId;
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
