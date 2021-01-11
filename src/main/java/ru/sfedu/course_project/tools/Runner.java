package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.api.DataProvider;
import ru.sfedu.course_project.enums.ApiEndpoint;
import ru.sfedu.course_project.enums.Status;

import java.io.IOException;
import java.util.HashMap;

import static ru.sfedu.course_project.enums.ApiEndpoint.*;

public class Runner {
    private DataProvider provider;

    public static Logger log = LogManager.getLogger(Runner.class);

    public Runner(DataProvider provider) {
        this.provider = provider;
    }

    public Runner() { }

    public Result run (String method, HashMap arguments) {
        log.debug("Attempt to run method: " + method);
        try {
            if (validateRole(arguments)) {
                try {
                    ApiEndpoint endpoint = valueOf(method);
                    switch (endpoint) {
                        case createPresentation: {
                            return provider.createPresentation(arguments);
                        }
                        case getPresentations: {
                            return provider.getPresentations();
                        }
                        case getPresentationById: {
                            return provider.getPresentationById(arguments);
                        }
                        case removePresentationById: {
                            return provider.removePresentationById(arguments);
                        }
                        case editPresentationOptions: {
                            return provider.editPresentationOptions(arguments);
                        }
                        case getPresentationSlides: {
                            return provider.getPresentationSlides(arguments);
                        }
                        case createPresentationSlide: {
                            return provider.createPresentationSlide(arguments);
                        }
                        case removePresentationSlideById: {
                            return provider.removePresentationSlideById(arguments);
                        }
                        case editPresentationSlideById: {
                            return provider.editPresentationSlideById(arguments);
                        }
                        case getSlideById: {
                            return provider.getSlideById(arguments);
                        }
                        case getPresentationComments: {
                            return provider.getPresentationComments(arguments);
                        }
                        case commentPresentation: {
                            return provider.commentPresentation(arguments);
                        }
                        case editPresentationComment: {
                            return provider.editPresentationComment(arguments);
                        }
                        case removePresentationCommentById: {
                            return provider.removePresentationCommentById(arguments);
                        }
                        case addElementInSlide: {
                            return provider.addElementInSlide(arguments);
                        }
                        case removeSlideElement: {
                            return provider.removeSlideElement(arguments);
                        }
                        case editSlideElement: {
                            return provider.editSlideElement(arguments);
                        }
                        case getSlideElementById: {
                            return provider.getSlideElementById(arguments);
                        }
                        case getSlideElements: {
                            return provider.getSlideElements(arguments);
                        }
                        case rateByMark: {
                            return provider.rateByMark(arguments);
                        }
                        case getPresentationMarks: {
                            return provider.getPresentationMarks(arguments);
                        }
                        default:
                            return new Result(Status.error, ConstantsError.UNEXPECTED_METHOD + method);
                    }
                } catch (RuntimeException e) {
                    log.error(e);
                    log.error(ConstantsError.UNEXPECTED_METHOD);
                    return new Result(Status.error, ConstantsError.UNEXPECTED_METHOD);
                }
            } else {
                return new Result(Status.error, ConstantsError.ACCESS_DENIED);
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error("Runner Exception: unable to run method: " + method);
            return new Result(Status.error, ConstantsError.UNEXPECTED_METHOD);
        }
    }

    public Boolean validateRole (HashMap arguments) {
        Boolean result = false;
        if (null == arguments.get("role")) {
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
