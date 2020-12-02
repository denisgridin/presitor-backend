package ru.sfedu.course_project.tools;

import ru.sfedu.course_project.api.DataProvider;

import java.util.HashMap;

public class Runner {
    private DataProvider provider;

    Runner (DataProvider provider) {
        this.provider = provider;
    }

    public void run (String method, HashMap arguments) {
        switch (method) {
            case "createPresentation": {
                this.provider.createPresentation(arguments);
                break;
            } default:
                throw new IllegalStateException("Unexpected method: " + method);
        }
    }
}
