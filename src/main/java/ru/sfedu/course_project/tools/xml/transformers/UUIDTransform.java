package ru.sfedu.course_project.tools.xml.transformers;

import org.simpleframework.xml.transform.Transform;

import java.util.UUID;

public class UUIDTransform implements Transform<UUID> {
    @Override
    public UUID read(String value) throws Exception {
        return UUID.fromString(value);
    }
    @Override
    public String write(UUID value) throws Exception {
        return value.toString();
    }
}