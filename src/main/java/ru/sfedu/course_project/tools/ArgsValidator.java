package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Main;
import ru.sfedu.course_project.enums.Instance;
import ru.sfedu.course_project.enums.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgsValidator {
    public ArgsValidator () { }

    public static Logger log = LogManager.getLogger(ArgsValidator.class);

    public Result validate (HashMap args, List fields) {
        log.debug("[validate] Validate: " + fields);
        ArrayList badFields = (ArrayList) fields.stream().filter(field -> args.get(field) == null).collect(Collectors.toList());
        log.debug("[validate] Null fields: " + badFields);
        log.debug("[validate] Null fields: " + badFields);
        if (badFields.size() > 0) {
            return new Result(Status.error, "Some of fields is not provided: " + badFields);
        } else {
            return new Result(Status.success, "");
        }
    }
}
