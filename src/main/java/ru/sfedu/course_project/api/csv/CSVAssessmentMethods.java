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

import static ru.sfedu.course_project.enums.CollectionType.*;
import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class CSVAssessmentMethods {
    private static final Logger log = LogManager.getLogger(CSVAssessmentMethods.class);

    public static Result rateByMark (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.MARK);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());

            log.debug(ConstantsInfo.ASSESSMENTS_SET);
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
            Optional resultValue = (Optional) resultCreate.getReturnValue();
            if (!resultValue.isPresent()) {
                return new Result(Status.success, ConstantsError.ASSESSMENT_ADD_ERROR);
            }
            Assessment assessment = (Assessment) resultValue.get();
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
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());

            ArrayList<Assessment> assessments = (ArrayList<Assessment>) CSVCommonMethods.getCollection(CollectionType.assessment, Assessment.class).orElse(new ArrayList());

            UUID presentationId = UUID.fromString((String) arguments.get(ConstantsField.PRESENTATION_ID));
            ArrayList<Assessment> presentationAssessments = (ArrayList<Assessment>) assessments.stream().filter(el -> el.getPresentationId().equals(presentationId)).collect(Collectors.toList());

            HashMap marks = new HashMap();

            marks.put(String.valueOf(Mark.awful), 0);
            marks.put(String.valueOf(Mark.bad), 0);
            marks.put(String.valueOf(Mark.normal), 0);
            marks.put(String.valueOf(Mark.good), 0);
            marks.put(String.valueOf(Mark.excellent), 0);

            log.debug(marks);

            presentationAssessments.stream().forEach(el -> {
                int currentCount = (int) marks.get(String.valueOf(el.getMark()));
                log.debug(String.format(ConstantsInfo.MARK_VALUE, el.getMark(), currentCount + 1));
                marks.replace(String.valueOf(el.getMark()), currentCount + 1);
            });
            log.info(ConstantsInfo.MARKS + marks);
            return new Result(Status.success, Optional.of(marks));

        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_GET_ERROR);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }

    public static Result removePresentationMarkById (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());

            Optional<Presentation> optionalMark = CSVCommonMethods.getInstanceExistenceByField(assessment, Assessment.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalMark.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.ID);
            }
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());

            UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
            Status status = CSVCommonMethods.removeRecordById(assessment, Assessment.class, id);

            if (status == Status.success) {
                return new Result(Status.success, ConstantsSuccess.ASSESSMENT_REMOVE);
            } else {
                return new Result(Status.error, ConstantsError.ASSESSMENT_REMOVE);
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_REMOVE);
            return new Result(Status.error, ConstantsError.ASSESSMENT_REMOVE);
        }
    }

    public static Result getMarkById (HashMap arguments) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(arguments, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) arguments.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());

            Optional<Presentation> optionalMark = CSVCommonMethods.getInstanceExistenceByField(assessment, Assessment.class, ConstantsField.ID, (String) arguments.get(ConstantsField.ID));
            if (!optionalMark.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.ID);
            } else {
                return new Result(Status.success, optionalMark);
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_GET_ERROR);
            return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
        }
    }

    public static Result editPresentationMark (HashMap args) {
        try {
            ArrayList fields = new ArrayList();
            fields.add(ConstantsField.PRESENTATION_ID);
            fields.add(ConstantsField.ID);
            Result isArgsValid = new ArgsValidator().validate(args, fields);
            if (Status.error == isArgsValid.getStatus()) {
                return isArgsValid;
            }

            UUID id = UUID.fromString((String) args.get(ConstantsField.ID));

            Optional<Presentation> optionalPresentation = CSVCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
            if (!optionalPresentation.isPresent()) {
                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
            }
            log.info(ConstantsInfo.PRESENTATION + optionalPresentation.get());



            log.debug(ConstantsInfo.ASSESSMENTS_GET);
            Optional<List> optionalList = CSVCommonMethods.getCollection(assessment, Assessment.class);
            if (optionalList.isPresent()) {
                ArrayList list = (ArrayList) optionalList.get();
                List<Assessment> updatedList = (List<Assessment>) list.stream().map(el -> updateAssessmentRecord((Assessment) el, args, id)).collect(Collectors.toList());
                Status result = CSVCommonMethods.writeCollection(updatedList, Assessment.class, assessment);
                if (Status.success == result) {
                    log.info(ConstantsSuccess.ASSESSMENT_UPDATE + id);
                    return new Result(Status.success, ConstantsSuccess.ASSESSMENT_UPDATE + id);
                } else {
                    return new Result(Status.error, ConstantsError.ASSESSMENT_UPDATE + id);
                }
            } else {
                return new Result(Status.error, ConstantsError.ASSESSMENT_GET_ERROR);
            }
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.ASSESSMENT_UPDATE);
            return new Result(Status.error, ConstantsError.ASSESSMENT_UPDATE);
        }
    }

    public static Assessment updateAssessmentRecord (Assessment assessment, HashMap arguments, UUID id) {
        if (assessment.getId().equals(id)) {
            String mark = (String) arguments.getOrDefault(ConstantsField.MARK, assessment.getMark());

            log.debug(ConstantsInfo.FIELD_EDIT + ConstantsField.MARK + mark);
            assessment.setMark(Mark.valueOf(mark));
        } return assessment;
    }
}
