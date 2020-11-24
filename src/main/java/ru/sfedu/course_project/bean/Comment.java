package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Comment extends Feedback implements Serializable {
    @CsvBindByName
    private String text;

    @CsvBindByName
    private List<Comment> replies;

    public Comment (long id, long userId, String text, List replies) {
        super(id, userId);

        this.text = text;
        this.replies = replies;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(text, comment.text) &&
                Objects.equals(replies, comment.replies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, replies);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", replies=" + replies +
                '}';
    }
}
