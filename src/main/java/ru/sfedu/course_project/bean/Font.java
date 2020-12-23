package ru.sfedu.course_project.bean;

import com.opencsv.bean.CsvBindByName;

import java.io.Serializable;
import java.util.Objects;

public class Font implements Serializable {
    @CsvBindByName
    private String family;

    @CsvBindByName
    private String size;

    @CsvBindByName
    private String letterSpacing;

    @CsvBindByName
    private String lineSpacing;

    @CsvBindByName
    private FontCase fontCase;

    public Font () { }


    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(String letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public String getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(String lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public FontCase getFontCase() {
        return fontCase;
    }

    public void setFontCase(FontCase fontCase) {
        this.fontCase = fontCase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return Objects.equals(family, font.family) &&
                Objects.equals(size, font.size) &&
                Objects.equals(letterSpacing, font.letterSpacing) &&
                Objects.equals(lineSpacing, font.lineSpacing) &&
                fontCase == font.fontCase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, size, letterSpacing, lineSpacing, fontCase);
    }

    @Override
    public String toString() {
        return "Font{" +
                "family='" + family + '\'' +
                ", size='" + size + '\'' +
                ", letterSpacing='" + letterSpacing + '\'' +
                ", lineSpacing='" + lineSpacing + '\'' +
                ", fontCase=" + fontCase +
                '}';
    }
}

