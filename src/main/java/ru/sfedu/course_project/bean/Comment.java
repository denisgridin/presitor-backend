package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.course_project.enums.Role;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Comment extends Feedback implements Serializable {
    @CsvBindByName
    private String text;

    public Comment (UUID id, Role role, String text) {
        super(id, role);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(text, comment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + getId() + ";" +
                "text=" + text + ";" +
                "role=" + this.getRole() + "}";
    }
}
