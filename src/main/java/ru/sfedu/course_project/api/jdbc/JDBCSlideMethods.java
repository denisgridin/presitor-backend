package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.SQLQuery;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.Method;
import ru.sfedu.course_project.enums.QueryMember;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.jdbc.QueryBuilder;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.sfedu.course_project.enums.CollectionType.slide;


public class JDBCSlideMethods {
    private static final Logger log = LogManager.getLogger(JDBCSlideMethods.class);
    public JDBCSlideMethods(){}


    public static int getSlideIndex (HashMap args) {
        try {
            Result resultGet = getPresentationSlides(args);
            if (Status.success == resultGet.getStatus()) {
                Optional optional = (Optional) resultGet.getReturnValue();
                ArrayList slides = (ArrayList) optional.get();
                log.debug(ConstantsInfo.SLIDES + slides);
                log.info(ConstantsInfo.SLIDES + slides.size());
                return slides.size();
            } else return 0;
        } catch (RuntimeException e) {
            log.error(e);
            return 0;
        }
    }

    public static Result createPresentationSlide (HashMap arguments) {
        try {

            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }


            log.debug(ConstantsInfo.SLIDE_CREATE);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            String index = (String) arguments.getOrDefault(ConstantsField.INDEX, String.valueOf(getSlideIndex(arguments)));

            arguments.put(ConstantsField.INDEX, Integer.valueOf(index));
            Optional<Slide> optionalSlide = (Optional<Slide>) new Creator().create(Slide.class, arguments).getReturnValue();

            if (!optionalSlide.isPresent()) {
                log.error(ConstantsError.SLIDE_CREATE);
                return new Result(Status.error, ConstantsError.SLIDE_CREATE);
            }

            String query = QueryBuilder.build(Method.create, QueryMember.slide, optionalSlide.get(), null);
            log.debug(ConstantsInfo.QUERY + query);

            if (query.isEmpty()) {
                log.error(ConstantsError.QUERY_EMPTY);
                return new Result(Status.error, ConstantsError.SLIDE_CREATE);
            }
            statement.execute(query);
            JDBCCommonMethods.closeConnection();
            log.info(ConstantsSuccess.SLIDE_CREATE);
            return new Result(Status.success, optionalSlide.get().getId());
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDE_CREATE);
            return new Result(Status.error, ConstantsError.SLIDE_CREATE);
        }
    }


    public static Result getSlideById (HashMap arguments) {
        try {
            log.debug(ConstantsInfo.SLIDE_GET);
            Statement statement = JDBCCommonMethods.setConnection();
            if (null == statement) {
                return new Result(Status.error, ConstantsError.CONNECTION_ERROR);
            }

            log.debug(ConstantsInfo.SQL_BUILD);

            String query = QueryBuilder.build(Method.get, QueryMember.slide, null, arguments);

            log.debug(String.format(ConstantsInfo.SQL_QUERY, query));

            if (query.isEmpty()) {
                return new Result(Status.error, ConstantsError.SQL_ERROR);
            }

            ResultSet resultSet = statement.executeQuery(query);
            log.info(ConstantsInfo.SQL_RESULT + resultSet);

            Result resultParseSet = JDBCCommonMethods.getInstanceFromResultSet(resultSet, QueryMember.slide);
            JDBCCommonMethods.closeConnection();

            if (resultParseSet.getStatus() == Status.success) {

                boolean withElements = Boolean.parseBoolean((String) arguments.getOrDefault(ConstantsField.WITH_ELEMENTS, "false"));

                Slide slide = (Slide) resultParseSet.getReturnValue();
                if (withElements) {
                    slide = setSlideElements(slide);
                }
                return new Result(Status.success, Optional.of(slide));
            } else {
                return resultParseSet;
            }
        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDE_GET);
            return new Result(Status.error, ConstantsError.SLIDE_GET);
        }
    }


    public static Result getPresentationSlides (HashMap args) {
        try {

            Statement statement = JDBCCommonMethods.setConnection();

            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
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



            String condition = String.format(SQLQuery.CONDITION_PRESENTATION_ID, args.get(ConstantsField.PRESENTATION_ID));

            String query = String.format(SQLQuery.RECORD_GET_WITH_CONDITION, QueryMember.slide, condition);
            log.debug(ConstantsInfo.QUERY + query);


            ResultSet resultSet = statement.executeQuery(query);
            Result result = JDBCCommonMethods.getListFromResultSet(resultSet, QueryMember.slide);

            if (Status.error == result.getStatus()) {
                return result;
            }

            ArrayList presentationSlides = (ArrayList) result.getReturnValue();

            boolean withElements = Boolean.parseBoolean((String) args.get(ConstantsField.WITH_ELEMENTS));
            log.debug("WithElements: " + withElements);
            if (withElements) {
                Result resultSetElements = setElementsBySlide(presentationSlides, UUID.fromString((String) args.get(ConstantsField.PRESENTATION_ID)));
                if (Status.error == resultSetElements.getStatus()) {
                    return resultSetElements;
                }

                Optional optional = (Optional) resultSetElements.getReturnValue();
                presentationSlides = (ArrayList<Slide>) optional.get();
                log.debug(ConstantsInfo.SLIDES + presentationSlides);
            }

            JDBCCommonMethods.closeConnection();

            return new Result(Status.success, Optional.of(presentationSlides));

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            log.error(ConstantsError.SLIDES_GET);
            return new Result(Status.error, ConstantsError.SLIDES_GET);
        }
    }

    public static Result setElementsBySlide (ArrayList slides, UUID presentationId) {
        try {
            ArrayList<Slide> updatedSlides = (ArrayList<Slide>) slides.stream().map(el -> setSlideElements((Slide) el)).collect(Collectors.toList());
            log.debug(ConstantsInfo.SLIDE + slide);
            return new Result(Status.success, Optional.of(updatedSlides));
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ELEMENTS_GET);
            return new Result(Status.error, ConstantsError.ELEMENTS_GET);
        }
    }

    public static Slide setSlideElements (Slide slide) {
        try {
            log.info(ConstantsInfo.ELEMENTS_GET + slide.getId());
            HashMap args = new HashMap();
            args.put(ConstantsField.PRESENTATION_ID, String.valueOf(slide.getPresentationId()));
            args.put(ConstantsField.SLIDE_ID, String.valueOf(slide.getId()));
            Result resultGetElements = JDBCElementMethods.getSlideElements(args);

            Optional optional = (Optional) resultGetElements.getReturnValue();

            log.debug(ConstantsInfo.STATUS + resultGetElements.getStatus());
            log.debug(ConstantsInfo.ELEMENTS + optional.get());
            if (Status.success == resultGetElements.getStatus()) {
                ArrayList elements = (ArrayList) optional.get();
                slide.setElements(elements);
            }
            return slide;
        } catch (RuntimeException e) {
            log.error(e);
            return slide;
        }
    }


    public static Result removePresentationSlideById(HashMap arguments) {
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);

            log.info(ConstantsInfo.ARGUMENTS_VALIDATE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            HashMap getPresParams = new HashMap();
            getPresParams.put(ConstantsField.ID, arguments.get(ConstantsField.PRESENTATION_ID));

            log.info(ConstantsInfo.PRESENTATION_CHECK);
            Result resultGetPresentation = JDBCPresentationMethods.getPresentationById(getPresParams);

            if (Status.error == resultGetPresentation.getStatus()){
                log.info(ConstantsError.PRESENTATION_NOT_FOUND);
                return resultGetPresentation;
            }

            log.debug(ConstantsInfo.SLIDE_REMOVE);
            String condition = String.format("id = '%s'", arguments.get(ConstantsField.ID));
            String query = String.format(SQLQuery.RECORD_REMOVE, QueryMember.slide, condition);

            log.debug(ConstantsInfo.QUERY + query);
            int removedRows = statement.executeUpdate(query);
            JDBCCommonMethods.closeConnection();

            if (removedRows > 0) {
                return new Result(Status.success, ConstantsSuccess.SLIDES_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.SLIDE_NOT_FOUND_IN_PRESENTATION);
            }

        } catch (RuntimeException | SQLException | IOException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.PRESENTATION_REMOVE);
        }
    }

    public static Result editPresentationSlideById(HashMap arguments) {
        try {
            Statement statement = JDBCCommonMethods.setConnection();
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);

            log.info(ConstantsInfo.ARGUMENTS_VALIDATE);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            log.info(ConstantsInfo.SLIDES_GET);
            Result resultGetSlide = getSlideById(arguments);

            if (Status.error == resultGetSlide.getStatus()){
                log.info(ConstantsInfo.SLIDE_NOT_FOUND);
                return resultGetSlide;
            }

            Optional optional = (Optional) resultGetSlide.getReturnValue();
            Slide slide = (Slide) optional.get();

            String query = QueryBuilder.build(Method.update, QueryMember.slide, slide, arguments);
            log.debug(ConstantsInfo.QUERY + query);
            int resultRows = statement.executeUpdate(query);
            log.debug(ConstantsInfo.UPDATED_ROWS + resultRows);
            JDBCCommonMethods.closeConnection();
            if (resultRows > 0) {
                return new Result(Status.success, ConstantsSuccess.PRESENTATION_UPDATE);
            } else {
                return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
            }
        } catch (RuntimeException | SQLException | IOException e){
            e.printStackTrace();
            log.error(e);
            return new Result(Status.error, ConstantsError.PRESENTATION_UPDATE);
        }
    }
}
