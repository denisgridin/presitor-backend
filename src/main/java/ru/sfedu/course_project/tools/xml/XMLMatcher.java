package ru.sfedu.course_project.tools.xml;

import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;
import ru.sfedu.course_project.tools.xml.transformers.UUIDTransform;

import java.util.UUID;

public class XMLMatcher implements Matcher {

    @Override
    public Transform match(Class type) throws Exception {
        if (type.equals(UUID.class))
            return new UUIDTransform();
        return null;
    }
}
