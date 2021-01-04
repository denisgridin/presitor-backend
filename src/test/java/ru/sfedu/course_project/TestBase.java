package ru.sfedu.course_project;

import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.api.DataProviderCSV;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBase {
    public Result makeRandomPresentation (DataProvider provider) {
        String id = String.valueOf(UUID.randomUUID());
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, id);
        Result result = provider.createPresentation(args);
        return result;
    }
    public Result makePresentationWithId (DataProvider provider, UUID id) {
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        Result result = provider.createPresentation(args);
        return result;
    }

    public Result makeSlideWithId (DataProvider provider, UUID id, UUID presentationId) {
        HashMap args = new HashMap();
        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        Result result = provider.createPresentationSlide(args);
        return result;
    }

    public HashMap getUpdatedShape (UUID id, UUID slideId, UUID presentationId) {
        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        args.put(ConstantsField.FIGURE, String.valueOf(Figure.square));

        Shape shape = new Shape();

        shape.setSlideId(slideId);
        shape.setPresentationId(presentationId);
        ElementType elementType = ElementType.shape;
        shape.setElementType(elementType);
        Figure figure = Figure.rectangle;
        shape.setFigure(figure);


        Style style = new Style();
        style.setBorderStyle(BorderStyle.dashed);
        style.setBorderColor("#403221");
        style.setFillColor("blue");
        style.setBorderWidth("2px");
        style.setBorderColor("yellow");
        style.setBorderRadius("3px");
        style.setBoxShadow("1px 2px 4px 2px blue");
        style.setOpacity("23");
        shape.setStyle(style);

        Layout layout = new Layout();
        layout.setHeight(120);
        layout.setWidth(420);
        layout.setRotation(13);
        layout.setX(132);
        layout.setY(13);
        shape.setLayout(layout);

        shape.setId(id);

        String name = "Test name";
        shape.setName(name);

        String text = "Тестовый текст";
        shape.setText(text);

        args.put(ConstantsField.TEXT, text);

        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(elementType));
        args.put(ConstantsField.FIGURE, String.valueOf(figure));

        args.put(ConstantsField.STYLE, style);
        args.put(ConstantsField.BORDER_STYLE ,String.valueOf(style.getBorderStyle()));
        args.put(ConstantsField.BORDER_COLOR ,style.getBorderColor());
        args.put(ConstantsField.FILL_COLOR ,style.getFillColor());
        args.put(ConstantsField.BORDER_RADIUS ,style.getBorderRadius());
        args.put(ConstantsField.BORDER_WIDTH ,style.getBorderWidth());
        args.put(ConstantsField.BOX_SHADOW ,style.getBoxShadow());
        args.put(ConstantsField.OPACITY ,style.getOpacity());

        args.put(ConstantsField.X, String.valueOf(layout.getX()));
        args.put(ConstantsField.Y, String.valueOf(layout.getY()));
        args.put(ConstantsField.ROTATION, String.valueOf(layout.getRotation()));
        args.put(ConstantsField.WIDTH, String.valueOf(layout.getWidth()));
        args.put(ConstantsField.HEIGHT, String.valueOf(layout.getHeight()));

        args.put(ConstantsField.ID, String.valueOf(id));
        args.put(ConstantsField.NAME, name);

        HashMap data = new HashMap();
        data.put("args", args);
        data.put("shape", shape);
        return data;
    }

    public void makeRectangleWithId (DataProvider provider, UUID id, UUID slideId, UUID presentationId) {
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.shape));
        args.put(ConstantsField.FIGURE, String.valueOf(Figure.rectangle));
        args.put(ConstantsField.ID, String.valueOf(id));


        Result resultCreateShape = new Creator().create(Shape.class, args);
        Result result = provider.addElementInSlide(args);
        assertTrue(Status.success == result.getStatus());

        if (Status.success == resultCreateShape.getStatus()) {
            Shape shape = (Shape) resultCreateShape.getReturnValue();
            assertEquals(shape.toString(), result.getReturnValue().toString());
        }
    }

    public void makeContentWithId (DataProvider provider, UUID id, UUID slideId, UUID presentationId, HashMap args) {
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);


        Result resultCreateContent = new Creator().create(Content.class, args);
        Result result = provider.addElementInSlide(args);
        assertTrue(Status.success == result.getStatus());

        if (Status.success == resultCreateContent.getStatus()) {
            Content content = (Content) resultCreateContent.getReturnValue();
            assertEquals(content.toString(), result.getReturnValue().toString());
        }
    }

    public void makeCustomContent (DataProvider provider, UUID id, UUID slideId, UUID presentationId) {
        makePresentationWithId(provider, presentationId);
        makeSlideWithId(provider, slideId, presentationId);

        String fontFamily = "Product Sans";
        String fontSize = "1.2rem";
        String letterSpacing = "2px";
        String lineSpacing = "12px";
        FontCase fontCase = FontCase.uppercase;
        String name = "Тестовое имя";
        String text = "Тестовый текст";

        HashMap args = new HashMap();
        args.put(ConstantsField.PRESENTATION_ID, String.valueOf(presentationId));
        args.put(ConstantsField.SLIDE_ID, String.valueOf(slideId));
        args.put(ConstantsField.ID, String.valueOf(id));

        args.put(ConstantsField.FONT_CASE, String.valueOf(fontCase));
        args.put(ConstantsField.FONT_SIZE, fontSize);
        args.put(ConstantsField.FONT_FAMILY, fontFamily);
        args.put(ConstantsField.LINE_SPACING, lineSpacing);
        args.put(ConstantsField.LETTER_SPACING, letterSpacing);
        args.put(ConstantsField.NAME, name);

        Layout layout = new Layout();
        layout.setHeight(120);
        layout.setWidth(420);
        layout.setRotation(13);
        layout.setX(132);
        layout.setY(13);

        args.put(ConstantsField.X, String.valueOf(layout.getX()));
        args.put(ConstantsField.Y, String.valueOf(layout.getY()));
        args.put(ConstantsField.ROTATION, String.valueOf(layout.getRotation()));
        args.put(ConstantsField.WIDTH, String.valueOf(layout.getWidth()));
        args.put(ConstantsField.HEIGHT, String.valueOf(layout.getHeight()));

        args.put(ConstantsField.TEXT, text);

        args.put(ConstantsField.ELEMENT_TYPE, String.valueOf(ElementType.content));

        Result result = provider.addElementInSlide(args);

        assertTrue(Status.success == result.getStatus());
    }
}