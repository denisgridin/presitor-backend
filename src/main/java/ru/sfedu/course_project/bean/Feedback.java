package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.course_project.converters.RoleConverter;
import ru.sfedu.course_project.converters.UUIDConverter;
import ru.sfedu.course_project.enums.Role;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Feedback {
    @CsvCustomBindByName(column = "id", converter = RoleConverter.class)
    private Role role;

    @CsvCustomBindByName(column = "id", converter = UUIDConverter.class)
    private UUID id;

    @CsvCustomBindByName(column = "presentationId", converter = UUIDConverter.class)
    private UUID presentationId;


    public Feedback() {}

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(UUID presentationId) {
        this.presentationId = presentationId;
    }
}
