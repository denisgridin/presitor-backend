package ru.sfedu.course_project.converters.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.bean.Presentation;

import java.util.UUID;

public class PresentationConverter implements Converter<Presentation> {
    private static final Logger log = LogManager.getLogger(XMLCommonMethods.class);

    @Override
    public Presentation read(InputNode node) throws Exception {
        try {
            log.debug(node.toString());
            String id = String.valueOf(node.getAttribute("id"));
            String name = String.valueOf(node.getAttribute("name"));
            String fillColor = String.valueOf(node.getAttribute("fillColor"));
            String fontFamily = String.valueOf(node.getAttribute("fontFamily"));
            Presentation presentation = new Presentation();

            presentation.setName(name);
            presentation.setId(UUID.fromString(id));
            presentation.setFontFamily(fontFamily);
            presentation.setFillColor(fillColor);

            return presentation;
        } catch (Exception e) {
            log.error(e);
            log.error("Unable to convert Presentation");
            return null;
        }
    }

    @Override
    public void write(OutputNode outputNode, Presentation presentation) throws Exception {
        try {
            String id = String.valueOf(presentation.getId());
            String name = presentation.getName();
            String fillColor = presentation.getFontFamily();
            String fontFamily = presentation.getFillColor();

            outputNode.setAttribute("name", name);
            outputNode.setAttribute("id", id);
            outputNode.setAttribute("fillColor", fillColor);
            outputNode.setAttribute("fontFamily", fontFamily);

        } catch (Exception e) {
            log.error(e);
        }
    }
}
