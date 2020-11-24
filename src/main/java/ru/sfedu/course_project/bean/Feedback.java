package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public abstract class Feedback implements Serializable {
    @CsvBindByName
    private long userId;

    @CsvBindByName
    private long id;

    public Feedback (long id, long userId) {
        this.id = id;
        this.userId = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return userId == feedback.userId &&
                id == feedback.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "userId=" + userId +
                ", id=" + id +
                '}';
    }
}
