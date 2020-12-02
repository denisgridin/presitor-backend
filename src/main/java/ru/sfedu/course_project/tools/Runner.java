package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Main;
import ru.sfedu.course_project.api.DataProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Runner {
    private DataProvider provider;

    public static Logger log = LogManager.getLogger(Runner.class);

    public Runner(DataProvider provider) {
        this.provider = provider;
    }

    public void run (String method, HashMap arguments) {
        log.debug("Attempt to run method: " + method);
        try {
            switch (method) {
                case "createPresentation": {
                    this.provider.createPresentation(arguments);
                    break;
                }
                case "getPresentationById": {
                    this.provider.getPresentationById(arguments);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected method: " + method);
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error("Runner Exception: unable to run method: " + method);
        }
    }
}
