package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.course_project.enums.Role;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Comment extends Feedback implements Serializable {
    public Comment () {}

    @CsvBindByName
    private String text;

    @CsvBindByName
    private String datetime;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDatetime () {
        return datetime;
    }

    public void setDatetime (String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", datetime='" + datetime + '\'' +
                ", id=" + getId() +
                ", presentationId=" + getPresentationId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(text, comment.text) &&
                Objects.equals(datetime, comment.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, datetime);
    }
}
