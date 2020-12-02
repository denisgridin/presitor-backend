package ru.sfedu.course_project.tools;

import java.util.stream.Stream;

public class EmptyArgumentError extends Throwable {
    EmptyArgumentError (Stream errors) {
        String message = String.format("EmptyArgumentError: missing required arguments\n%s", errors.toString());
    }
}
