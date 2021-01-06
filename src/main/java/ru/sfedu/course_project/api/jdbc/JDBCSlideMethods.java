package ru.sfedu.course_project.api.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.api.xml.XMLCommonMethods;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConstantsField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static ru.sfedu.course_project.enums.CollectionType.presentation;

public class JDBCSlideMethods {
    private static final Logger log = LogManager.getLogger(JDBCSlideMethods.class);
    public JDBCSlideMethods(){}

//    public Result getPresentationSlides (HashMap args) {
//        try {
//
//            ArrayList fields = new ArrayList();
//            fields.add(ConstantsField.PRESENTATION_ID);
//            fields.add(ConstantsField.SLIDE_ID);
//            Result isArgsValid = new ArgsValidator().validate(args, fields);
//            if (Status.error == isArgsValid.getStatus()) {
//                return isArgsValid;
//            }
//
//            Optional<Presentation> optionalPresentation = JDBCCommonMethods.getInstanceExistenceByField(presentation, Presentation.class, ConstantsField.ID, (String) args.get(ConstantsField.PRESENTATION_ID));
//            if (!optionalPresentation.isPresent()) {
//                return new Result(Status.error, ConstantsError.INSTANCE_NOT_FOUND + ConstantsField.PRESENTATION_ID);
//            }
//
//            log.info("Presentation found: " +  optionalPresentation.get());
//        } catch (RuntimeException e) {
//            log.error();
//        }
//    }
}
