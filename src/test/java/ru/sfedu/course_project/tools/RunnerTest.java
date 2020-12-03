package ru.sfedu.course_project.tools;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {
    @Test
    void validateRoleSuccessEditor () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
        args.put("role", "editor");
        args.put("method", "getPresentationById");
        assertTrue(runner.validateRole(args));
    }

    @Test
    void validateRoleSuccessGuest () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
        args.put("role", "guest");
        args.put("method", "getPresentationById");
        assertTrue(runner.validateRole(args));
    }

    @Test
    void validateRoleFailEditor () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
        args.put("role", "admin");
        args.put("method", "getPresentationById");
        assertFalse(runner.validateRole(args));
    }

    @Test
    void validateRoleFailGuest () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
        args.put("role", "guest");
        args.put("method", "createPresentation");
        assertFalse(runner.validateRole(args));
    }

    @Test
    void validateRoleFailNoRoleError () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
//        args.put("role", "guest"); if role is not provided
        args.put("method", "createPresentation");
        assertFalse(runner.validateRole(args));
    }

    @Test
    void validateRoleFailUndefinedArguments () {
        Runner runner = new Runner();
        HashMap args = new HashMap();
//        args.put("role", "admin"); // if role is not defined
//        args.put("method", "createPresentation"); //
        assertFalse(runner.validateRole(args));
    }
}