package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.api.xml.XMLElementMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Shape;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;
import ru.sfedu.course_project.bean.Element;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static ru.sfedu.course_project.enums.CollectionType.presentation;
import static ru.sfedu.course_project.enums.CollectionType.slide;

public class JDBCElementMethods {

    public JDBCElementMethods() { }

    private static final Logger log = LogManager.getLogger(JDBCElementMethods.class);

    public static Result addElementInSlide (HashMap args) {
        try {


            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ELEMENT_TYPE);
            Result isArgsValid = new ArgsValidator().validate(args, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }


            Result resultCheck = checkPresentationAndSlideExistance(args);
            if (Status.error == resultCheck.getStatus()) {
                return resultCheck;
            }

            HashMap returnValue = (HashMap) resultCheck.getReturnValue();

            ArrayList fields = (ArrayList) returnValue.get("fields");

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));
            log.debug("Attempt to add: " + elementType);

            switch (elementType) {
                case shape: {
                    log.info("Shape creating");
                    ArrayList shapeFields = new ArrayList();
                    shapeFields.add(ConstantsField.FIGURE);
                    Result isShapeArgsValid = new ArgsValidator().validate(args, fields);
                    if (Status.error == isShapeArgsValid.getStatus()) {
                        return isShapeArgsValid;
                    }
                    return createShape(args);
                }
//                case content: {
//                    log.info("Content creating");
//                    ArrayList contentFields = new ArrayList();
//                    contentFields.add(ConstantsField.TEXT);
//                    Result isContentArgsValid = new ArgsValidator().validate(args, fields);
//                    if (Status.error == isContentArgsValid.getStatus()) {
//                        return isContentArgsValid;
//                    }
//                    return createContent(args);
//                }
                default: {
                    return new Result(Status.error, ConstantsError.FIGURE_UNDEFINED);
                }
            }


        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.FIGURE_CREATE);
            return new Result(Status.error, ConstantsError.FIGURE_CREATE);
        }
    }

    public static Result getSlideElements (HashMap args) {
        try {
            log.debug("Check presentation and slide exist");
            Result resultCheck = checkPresentationAndSlideExistance(args);
            if (Status.error == resultCheck.getStatus()) {
                return resultCheck;
            }

            HashMap resultValue = (HashMap) resultCheck.getReturnValue();

            ArrayList fields = (ArrayList) resultValue.get("fields");


            Slide slide = (Slide) resultValue.get("slide");


            log.debug("Get slide shapes");
            Result resultGetShapes = getSlideShapes(slide);
            if (Status.error == resultGetShapes.getStatus()) {
                return resultGetShapes;
            }

            ArrayList<Shape> shapes = (ArrayList<Shape>) resultGetShapes.getReturnValue();
            log.debug("Slide shapes: " + shapes);

            ArrayList<Element> elements = new ArrayList<>();
            log.debug("Slide elements: " + elements);
            elements.addAll(shapes);

            return new Result(Status.success, elements);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Result getSlideShapes (Slide slide) {
        try {
            log.info("Get slide shapes");

            Statement statement = JDBCCommonMethods.setConnection();

            UUID slideId = slide.getId();
            String conditionPresentation = String.format(SQLQuery.CONDITION_PRESENTATION_ID, slide.getPresentationId());
            String conditionSlide = String.format(SQLQuery.CONDITION_SLIDE_ID, slideId);

            String condition = String.format("%s and %s", conditionPresentation, conditionSlide);
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.shape, condition);
            log.info("Query string: " + query);

            ResultSet resultSet = statement.executeQuery(query);

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.shape);

            JDBCCommonMethods.closeConnection();
            return result;
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }


    public static Result createShape (HashMap args) {
        try {
            Result resultCreateShape = new Creator().create(Shape.class, args);
            if (Status.error == resultCreateShape.getStatus()) {
                return resultCreateShape;
            }

            Shape shape = (Shape) resultCreateShape.getReturnValue();
            log.info("Create new shape: " + shape);

            return addShapeInSlide(shape);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_CREATE);
            return new Result(Status.error, ConstantsError.SHAPE_CREATE);
        }
    }

    private static Result addShapeInSlide (Shape shape) {
        try {

            Statement statement = JDBCCommonMethods.setConnection();

            log.info("add shape in slide");

            String query = QueryBuilder.build(Method.create, QueryMember.shape, shape, null);
            log.debug("Query string: " + query);

            int insertResult = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();
            log.info("Execute result: " + insertResult);
            if (insertResult > 0) {
                return new Result(Status.success, shape);
            } else {
                return new Result(Status.error, ConstantsError.SHAPE_CREATE);
            }
        } catch (RuntimeException | IOException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_CREATE);
            return new Result(Status.error, ConstantsError.SHAPE_CREATE);
        }
    }

    private static Result checkPresentationAndSlideExistance (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.SLIDE_ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            HashMap paramsGetPres = new HashMap();
            paramsGetPres.put(ConstantsField.ID, args.get(ConstantsField.PRESENTATION_ID));
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(paramsGetPres);

            if (Status.error == resultGetPresentation.getStatus()) {
                return resultGetPresentation;
            }

            HashMap paramsGetSlide = new HashMap();
            paramsGetSlide.put(ConstantsField.PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));
            paramsGetSlide.put(ConstantsField.ID, args.get(ConstantsField.SLIDE_ID));
            Result resultGetSlide = JDBCSlideMethods.getSlideById(paramsGetSlide);

            if (Status.error == resultGetSlide.getStatus()) {
                return resultGetSlide;
            }

            HashMap params = new HashMap();
            params.put("fields", fields);
            params.put("presentation", resultGetPresentation.getReturnValue());
            params.put("slide", resultGetSlide.getReturnValue());

            return new Result(Status.success, params);
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

}
