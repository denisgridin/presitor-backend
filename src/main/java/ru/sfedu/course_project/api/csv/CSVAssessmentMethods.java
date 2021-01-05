package ru.sfedu.course_project.api.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.Assessment;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Mark;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.*;
import java.util.stream.Collectors;


import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class CSVAssessmentMethods {
    private static final Logger log = LogManager.getLogger(CSVAssessmentMethods.class);

    public static Result rateByMark (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info("Presentation found: " +  optionalPresentation.get());

            log.debug("Start to add assessment in data source");
            return addPresentationMark(arguments);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_ADD_ERROR);
            return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
        }
    }

    public static Result addPresentationMark(HashMap arguments) {
        try {
            Result resultCreate = new Creator().create(Assessment.class, arguments);

            if (Status.error == resultCreate.getStatus()) {
                return resultCreate;
            }

            log.info(ConstantsInfo.ASSESSMENTS_GET);
            ArrayList<Assessment> assessments = (ArrayList<Assessment>) CSVCommonMethods.getCollection(CollectionType.assessment, Assessment.class).orElse(new ArrayList());
            Assessment assessment = (Assessment) resultCreate.getReturnValue();
            assessments.add(assessment);

            Status statusWrite = CSVCommonMethods.writeCollection(assessments, Assessment.class, CollectionType.assessment);

            if (Status.success == statusWrite) {
                log.info(ConstantsSuccess.ASSESSMENT_CREATE);
                return new Result(Status.success, ConstantsSuccess.ASSESSMENT_CREATE);
            } else {
                log.info(ConstantsError.ASSESSMENT_ADD_ERROR);
                return new Result(Status.success, ConstantsError.ASSESSMENT_ADD_ERROR);
            }

        } catch (RuntimeException e) {
            log.error(e);
            e.printStackTrace();
            return new Result(Status.error, ConstantsError.ASSESSMENT_ADD_ERROR);
        }
    }

    public static Result getPresentationMarks (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info("Presentation found: " +  optionalPresentation.get());

            ArrayList<Assessment> assessments = (ArrayList<Assessment>) CSVCommonMethods.getCollection(CollectionType.assessment, Assessment.class).orElse(new ArrayList());

            UUID presentationId = UUID.fromString((String) arguments.get(ConstantsField.PRESENTATION_ID));
            ArrayList<Assessment> presentationAssessments = (ArrayList<Assessment>) assessments.stream().filter(el -> el.getPresentationId().equals(presentationId)).collect(Collectors.toList());

            HashMap marks = new HashMap();

            marks.put(String.valueOf(Mark.awful), 0);
            marks.put(String.valueOf(Mark.bed), 0);
            marks.put(String.valueOf(Mark.normal), 0);
            marks.put(String.valueOf(Mark.good), 0);
            marks.put(String.valueOf(Mark.excellent), 0);

            log.debug(marks);

            presentationAssessments.stream().forEach(el -> {
                int currentCount = (int) marks.get(String.valueOf(el.getMark()));
                log.debug(String.format("Mark %s: %s", el.getMark(), currentCount + 1));
                marks.replace(String.valueOf(el.getMark()), currentCount + 1);
            });
            log.info("Presentation marks: " + marks);
            return new Result(Status.success, marks);

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_GET_ERROR);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }
}
