package ru.sfedu.course_project.tools;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ArgsValidator {
    public ArgsValidator () {
    }

    public static Object validate (HashMap args, List required) throws EmptyArgumentError {
        Stream errors = required.stream().filter(el -> args.get(el) == null);
        if (errors.count() > 0) {
            throw new EmptyArgumentError(errors);
        } else {
            return true;
        }
    }
}
