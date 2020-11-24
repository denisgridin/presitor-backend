package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public class Style implements Serializable {
    @CsvBindByName
    private String fillColor;

    @CsvBindByName
    private String boxShadow;

    @CsvBindByName
    private int opacity;

    @CsvBindByName
    private String borderColor;

    @CsvBindByName
    private String borderRadius;

    @CsvBindByName
    private String borderWidth;

    @CsvBindByName
    private BorderStyle borderStyle;

    public Style(String fillColor, String boxShadow, int opacity, String borderColor, String borderRadius, String borderWidth, BorderStyle borderStyle) {
        this.fillColor = fillColor;
        this.boxShadow = boxShadow;
        this.opacity = opacity;
        this.borderColor = borderColor;
        this.borderRadius = borderRadius;
        this.borderWidth = borderWidth;
        this.borderStyle = borderStyle;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getBoxShadow() {
        return boxShadow;
    }

    public void setBoxShadow(String boxShadow) {
        this.boxShadow = boxShadow;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(String borderRadius) {
        this.borderRadius = borderRadius;
    }

    public String getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(String borderWidth) {
        this.borderWidth = borderWidth;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Style style = (Style) o;
        return opacity == style.opacity &&
                Objects.equals(fillColor, style.fillColor) &&
                Objects.equals(boxShadow, style.boxShadow) &&
                Objects.equals(borderColor, style.borderColor) &&
                Objects.equals(borderRadius, style.borderRadius) &&
                Objects.equals(borderWidth, style.borderWidth) &&
                borderStyle == style.borderStyle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fillColor, boxShadow, opacity, borderColor, borderRadius, borderWidth, borderStyle);
    }

    @Override
    public String toString() {
        return "Style{" +
                "fillColor='" + fillColor + '\'' +
                ", boxShadow='" + boxShadow + '\'' +
                ", opacity=" + opacity +
                ", borderColor='" + borderColor + '\'' +
                ", borderRadius='" + borderRadius + '\'' +
                ", borderWidth='" + borderWidth + '\'' +
                ", borderStyle=" + borderStyle +
                '}';
    }
}
