package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.course_project.enums.Mark;
import ru.sfedu.course_project.enums.Role;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Assessment extends Feedback implements Serializable {
    @CsvBindByName
    private Mark mark;

    public Assessment () {
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Assessment that = (Assessment) o;
        return mark == that.mark;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mark);
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + getId() +
                ";mark=" + mark +
                '}';
    }
}
