package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.course_project.enums.Role;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Feedback implements Serializable {
    @CsvBindByName
    private Role role;

    @CsvBindByName
    private UUID id;

    public Feedback() {}

    public Feedback (UUID id, Role role) {
        this.id = id;
        this.role = role;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return role == feedback.role &&
                id == feedback.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "role=" + role +
                ", id=" + id +
                '}';
    }
}
