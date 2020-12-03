package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.api.DataProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class Runner {
    private DataProvider provider;

    public static Logger log = LogManager.getLogger(Runner.class);

    public Runner(DataProvider provider) {
        this.provider = provider;
    }

    public Runner() { }

    public Object run (String method, HashMap arguments) {
        log.debug("Attempt to run method: " + method);
        try {
            if (validateRole(arguments)) {
                switch (method) {
                    case "createPresentation": {
                        return this.provider.createPresentation(arguments);
                    }
                    case "getPresentationById": {
                        return  this.provider.getPresentationById(arguments);
                    }
                    default:
                        throw new IllegalStateException("Unexpected method: " + method);
                }
            } else {
                return null; // TODO return error exception
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error("Runner Exception: unable to run method: " + method);
        }
        return null;
    }

    public Boolean validateRole (HashMap arguments) {
        Boolean result = false;
        if (arguments.get("role") == null) {
            log.error("[validateRole] Role is not provided"); // TODO return error exception
        } else {
            String role = String.valueOf(arguments.get("role"));
            log.debug("Current role: " + role);
            if (role.equals("guest")) {
                result = Constants.PUBLIC_METHODS.contains(arguments.get("method"));
                if (!result) {
                    log.error("[validateRole] You have no access for this method: " + arguments.get("method")); // TODO return error exception
                }
            } else {
                result = role.equals("editor");
                if (!result) {
                    log.error("[validateRole] Unexpected role: " + role); // TODO return error exception
                }
            };
        }
        return result;
    }
}
