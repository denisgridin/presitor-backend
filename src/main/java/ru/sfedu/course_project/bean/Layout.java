package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public class Layout implements Serializable {
    @CsvBindByName
    private int x;

    @CsvBindByName
    private int y;

    @CsvBindByName
    private int width;

    @CsvBindByName
    private int height;

    @CsvBindByName
    private int rotation;

    public Layout (int x, int y, int width, int height, int rotation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layout layout = (Layout) o;
        return x == layout.x &&
                y == layout.y &&
                width == layout.width &&
                height == layout.height &&
                rotation == layout.rotation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height, rotation);
    }

    @Override
    public String toString() {
        return "Layout{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", rotation=" + rotation +
                '}';
    }
}
