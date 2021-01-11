package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.*;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


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
            log.debug(ConstantsInfo.ARGUMENTS_ADD + elementType);

            switch (elementType) {
                case shape: {
                    log.info(ConstantsInfo.SHAPE_CREATE);
                    ArrayList shapeFields = new ArrayList();
                    shapeFields.add(ConstantsField.FIGURE);
                    Result isShapeArgsValid = new ArgsValidator().validate(args, fields);
                    if (Status.error == isShapeArgsValid.getStatus()) {
                        return isShapeArgsValid;
                    }
                    return createShape(args);
                }
                case content: {
                    log.info(ConstantsInfo.CONTENT_CREATE);
                    ArrayList contentFields = new ArrayList();
                    contentFields.add(ConstantsField.TEXT);
                    Result isContentArgsValid = new ArgsValidator().validate(args, fields);
                    if (Status.error == isContentArgsValid.getStatus()) {
                        return isContentArgsValid;
                    }
                    return createContent(args);
                }
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
            log.debug(ConstantsInfo.PRESENTATION_CHECK);
            Result resultCheck = checkPresentationAndSlideExistance(args);
            if (Status.error == resultCheck.getStatus()) {
                return resultCheck;
            }

            HashMap resultValue = (HashMap) resultCheck.getReturnValue();

            ArrayList fields = (ArrayList) resultValue.get("fields");


            Slide slide = (Slide) resultValue.get("slide");


            log.debug(ConstantsInfo.SHAPES_GET);
            Result resultGetShapes = getSlideShapes(slide);
            if (Status.error == resultGetShapes.getStatus()) {
                return resultGetShapes;
            }

            Optional optional = (Optional) resultGetShapes.getReturnValue();
            ArrayList<Shape> shapes = (ArrayList<Shape>) optional.get();
            log.debug(ConstantsInfo.SHAPES + shapes);




            log.debug(ConstantsInfo.CONTENTS_GET);
            Result resultGetContents = getSlideContents(slide);
            if (Status.error == resultGetContents.getStatus()) {
                return resultGetContents;
            }
            ArrayList<Content> contents = (ArrayList<Content>) resultGetContents.getReturnValue();
            log.debug(ConstantsInfo.CONTENTS + shapes);



            ArrayList<Element> elements = new ArrayList<>();
            elements.addAll(shapes);
            elements.addAll(contents);
            log.debug(ConstantsInfo.ELEMENTS + elements);

            return new Result(Status.success, Optional.of(elements));

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Result getSlideElementById (HashMap args) {
        try {
            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ELEMENT_TYPE);
            elementFields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(args, elementFields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.debug(ConstantsInfo.PRESENTATION_CHECK);
            Result resultCheck = checkPresentationAndSlideExistance(args);
            if (Status.error == resultCheck.getStatus()) {
                return resultCheck;
            }

            HashMap resultValue = (HashMap) resultCheck.getReturnValue();

            ArrayList fields = (ArrayList) resultValue.get("fields");


            Slide slide = (Slide) resultValue.get("slide");

            ElementType elementType = ElementType.valueOf((String) args.get(ConstantsField.ELEMENT_TYPE));

            switch (elementType) {
                case shape: {
                    log.debug(ConstantsInfo.SHAPE_GET);
                    return getSlideShape(args);
                }
                case content: {
                    log.debug(ConstantsInfo.CONTENT_GET);
                    return getSlideContent(args);
                }
                default: {
                    return new Result(Status.error, ConstantsError.ELEMENTS_GET);
                }
            }

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Result getSlideContent (HashMap args) {
        try {
            log.info(ConstantsInfo.CONTENT_GET);

            Statement statement = JDBCCommonMethods.setConnection();

            String conditionPresentation = String.format(SQLQuery.CONDITION_PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));
            String conditionSlide = String.format(SQLQuery.CONDITION_SLIDE_ID, args.get(ConstantsField.SLIDE_ID));
            String conditionId = String.format(SQLQuery.CONDITION_ITEM_ID, args.get(ConstantsField.ID));

            String condition = String.format("%s and %s and %s", conditionPresentation, conditionSlide, conditionId);
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.content, condition);
            log.info(ConstantsInfo.QUERY + query);

            ResultSet resultSet = statement.executeQuery(query);

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.content);
            JDBCCommonMethods.closeConnection();
            if (Status.error == result.getStatus()) {
                return result;
            }


            Optional optional = (Optional) result.getReturnValue();
            ArrayList resultValue = (ArrayList) optional.get();
            Content content = (Content) resultValue.get(0);
            return new Result(Status.success, Optional.of(content));
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.CONTENT_GET);
        }
    }

    public static Result getSlideShape (HashMap args) {
        try {
            log.info(ConstantsInfo.SHAPE_GET);

            Statement statement = JDBCCommonMethods.setConnection();

            String conditionPresentation = String.format(SQLQuery.CONDITION_PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));
            String conditionSlide = String.format(SQLQuery.CONDITION_SLIDE_ID, args.get(ConstantsField.SLIDE_ID));
            String conditionId = String.format(SQLQuery.CONDITION_ITEM_ID, args.get(ConstantsField.ID));

            String condition = String.format("%s and %s and %s", conditionPresentation, conditionSlide, conditionId);
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.shape, condition);
            log.info(ConstantsInfo.QUERY + query);

            ResultSet resultSet = statement.executeQuery(query);

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.shape);
            JDBCCommonMethods.closeConnection();
            if (Status.error == result.getStatus()) {
                return result;
            }
            ArrayList resultValue = (ArrayList) result.getReturnValue();
            Shape shape = (Shape) resultValue.get(0);
            return new Result(Status.success, Optional.of(shape));
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.SHAPE_GET);
        }
    }

    public static Result getSlideContents (Slide slide) {
        try {
            log.info(ConstantsInfo.CONTENTS_GET);

            Statement statement = JDBCCommonMethods.setConnection();

            UUID slideId = slide.getId();
            String conditionPresentation = String.format(SQLQuery.CONDITION_PRESENTATION_ID, slide.getPresentationId());
            String conditionSlide = String.format(SQLQuery.CONDITION_SLIDE_ID, slideId);

            String condition = String.format("%s and %s", conditionPresentation, conditionSlide);
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.content, condition);
            log.info(ConstantsInfo.QUERY + query);

            ResultSet resultSet = statement.executeQuery(query);

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.content);

            JDBCCommonMethods.closeConnection();
            return result;
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result getSlideShapes (Slide slide) {
        try {
            log.info(ConstantsInfo.SHAPES_GET);

            Statement statement = JDBCCommonMethods.setConnection();

            UUID slideId = slide.getId();
            String conditionPresentation = String.format(SQLQuery.CONDITION_PRESENTATION_ID, slide.getPresentationId());
            String conditionSlide = String.format(SQLQuery.CONDITION_SLIDE_ID, slideId);

            String condition = String.format("%s and %s", conditionPresentation, conditionSlide);
            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.shape, condition);
            log.info(ConstantsInfo.QUERY + query);

            ResultSet resultSet = statement.executeQuery(query);

            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.shape);

            if (result.getStatus() == Status.success) {
                return new Result(Status.success, Optional.of(result.getReturnValue()));
            } else {
                return result;
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }

    public static Result createContent (HashMap args) {
        try {
            Result resultCreateContent = new Creator().create(Content.class, args);
            if (Status.error == resultCreateContent.getStatus()) {
                return resultCreateContent;
            }

            Content content = (Content) resultCreateContent.getReturnValue();
            log.info(ConstantsInfo.CONTENT_CREATE + content);

            return addContentInSlide(content);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.CONTENT_CREATE);
            return new Result(Status.error, ConstantsError.CONTENT_CREATE);
        }
    }


    public static Result createShape (HashMap args) {
        try {
            Result resultCreateShape = new Creator().create(Shape.class, args);
            if (Status.error == resultCreateShape.getStatus()) {
                return resultCreateShape;
            }

            Shape shape = (Shape) resultCreateShape.getReturnValue();
            log.info(ConstantsInfo.SHAPE_CREATE + shape);

            return addShapeInSlide(shape);
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.SHAPE_CREATE);
            return new Result(Status.error, ConstantsError.SHAPE_CREATE);
        }
    }

    private static Result addContentInSlide (Content content) {
        try {

            Statement statement = JDBCCommonMethods.setConnection();

            log.info(ConstantsInfo.CONTENT_ADD);

            String query = QueryBuilder.build(Method.create, QueryMember.content, content, null);
            log.debug(ConstantsInfo.QUERY + query);

            int insertResult = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsInfo.EXECUTE_RESULT + insertResult);
            if (insertResult > 0) {
                return new Result(Status.success, Optional.of(content));
            } else {
                return new Result(Status.error, ConstantsError.CONTENT_CREATE);
            }
        } catch (RuntimeException | IOException | SQLException e) {
            log.error(e);
            log.error(ConstantsError.CONTENT_CREATE);
            return new Result(Status.error, ConstantsError.CONTENT_CREATE);
        }
    }

    private static Result addShapeInSlide (Shape shape) {
        try {

            Statement statement = JDBCCommonMethods.setConnection();

            log.info(ConstantsInfo.SHAPE_ADD);

            String query = QueryBuilder.build(Method.create, QueryMember.shape, shape, null);
            log.debug(ConstantsInfo.QUERY + query);

            int insertResult = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsInfo.EXECUTE_RESULT + insertResult);
            if (insertResult > 0) {
                return new Result(Status.success, Optional.of(shape));
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

            Optional optionalPresentation = (Optional) resultGetPresentation.getReturnValue();
            Optional optionalSlide = (Optional) resultGetSlide.getReturnValue();

            HashMap params = new HashMap();
            params.put("fields", fields);
            params.put("presentation", optionalPresentation.get());
            params.put("slide", optionalSlide.get());

            return new Result(Status.success, params);
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, e);
        }
    }


    public static Result removeSlideElement (HashMap args) {
        try {

            ArrayList elementFields = new ArrayList();
            elementFields.add(ConstantsField.ELEMENT_TYPE);
            elementFields.add(ConstantsField.ID);
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
            log.debug(ConstantsInfo.REMOVE_ATTEMPT + elementType);

            QueryMember queryMember = QueryMember.valueOf(String.valueOf(elementType));

            String query = QueryBuilder.build(Method.remove, queryMember, null, args);
            log.debug(ConstantsInfo.QUERY + query);

            if (query.isEmpty()) {
                log.error(ConstantsError.SQL_ERROR);
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            log.info(ConstantsInfo.REMOVE_ATTEMPT);

            Statement statement = JDBCCommonMethods.setConnection();

            int resultRows = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();

            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.ELEMENT_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.ELEMENT_REMOVE);
            }

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENT_REMOVE);
            return new Result(Status.error, ConstantsError.ELEMENT_REMOVE);
        }
    }

    public static Result editSlideElement (HashMap args) {
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
            log.debug(ConstantsInfo.EDIT_ATTEMPT + elementType);
            QueryMember queryMember = QueryMember.valueOf(String.valueOf(elementType));

            Result resultGetElement = getSlideElementById(args);
            if (Status.error == resultGetElement.getStatus()) {
                return resultGetElement;
            }

            Optional optional = (Optional) resultGetElement.getReturnValue();

            if (!optional.isPresent()) {
                return new Result(Status.error, ConstantsError.ELEMENT_NOT_FOUND);
            }

//            String query = QueryBuilder.build(Method.update, queryMember, optional.get(), args);
//            log.debug(ConstantsInfo.QUERY + query);
//
//            if (query.isEmpty()) {
//                log.error(ConstantsError.SQL_ERROR);
//                return new Result(Status.error, ConstantsError.SQL_ERROR);
//            }

            log.info(ConstantsInfo.EDIT_ATTEMPT);

            Connection connection = JDBCCommonMethods.getConnection();
            PreparedStatement statement = null;

            String query = "";
            if (queryMember == QueryMember.shape) {
                Shape shape = (Shape) optional.get();
                query = SQLQuery.PREPARED_SHAPE_UPDATE;
                log.debug(ConstantsInfo.QUERY + query);
                log.debug("Updating shape: " + shape);

                Layout layout = QueryBuilder.updateLayout(shape.getLayout(), args);
                Style style = QueryBuilder.updateStyle(shape.getStyle(), args);

                String layoutValue = layout.toString();
                String styleValue = style.toString();
                String name = (String) args.getOrDefault(ConstantsField.NAME, shape.getName());
                String presentationId = String.valueOf(shape.getPresentationId());
                String slideId = String.valueOf(shape.getSlideId());
                String id = String.valueOf(shape.getId());
                String text = (String) args.getOrDefault(ConstantsField.TEXT, shape.getText());
                String figure = String.valueOf(shape.getFigure());

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, String.valueOf(elementType));
                preparedStatement.setString(2, String.valueOf(figure));
                preparedStatement.setString(3, String.valueOf(id));
                preparedStatement.setString(4, layoutValue);
                preparedStatement.setString(5, name);
                preparedStatement.setString(6, String.valueOf(presentationId));
                preparedStatement.setString(7, String.valueOf(slideId));
                preparedStatement.setString(8, String.valueOf(styleValue));
                preparedStatement.setString(9, text);
                preparedStatement.setString(10, String.valueOf(id));
                statement = preparedStatement;
            } else if (queryMember == QueryMember.content) {
                Content content = (Content) optional.get();
                log.debug("Updating content: " + content);

                query = SQLQuery.PREPARED_CONTENT_UPDATE;
                Layout layout = QueryBuilder.updateLayout(content.getLayout(), args);
                String layoutValue = layout.toString();

                Font font = QueryBuilder.updateFont(content.getFont(), args);
                String fontValue = font.toString();

                String name = (String) args.getOrDefault(args.get(ConstantsField.NAME), content.getName());
                String presentationId = String.valueOf(content.getPresentationId());
                String slideId = String.valueOf(content.getSlideId());
                String id = String.valueOf(content.getId());
                String text = (String) args.getOrDefault(args.get(ConstantsField.TEXT), content.getText());

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, String.valueOf(elementType));
                preparedStatement.setString(2, layoutValue);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, String.valueOf(presentationId));
                preparedStatement.setString(5, String.valueOf(slideId));
                preparedStatement.setString(6, text);
                preparedStatement.setString(7, String.valueOf(id));
                preparedStatement.setString(8, fontValue);
                statement = preparedStatement;
            }

            if (query.isEmpty()) {
                log.error(ConstantsError.SQL_ERROR);
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            int resultRows = statement.executeUpdate();
            JDBCCommonMethods.closeConnection();

            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.ELEMENT_EDIT);
            } else {
                return new Result(Status.error, ConstantsError.ELEMENT_EDIT);
            }

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENT_EDIT);
            return new Result(Status.error, ConstantsError.ELEMENT_EDIT);
        }

    }

}
