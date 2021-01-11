package ru.sfedu.course_project.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.enums.Status;

import java.io.Serializable;
import java.util.Optional;

public class Result implements Serializable {
    private static Logger log = LogManager.getLogger(Result.class);
    private Status status;
    private Object returnValue;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Result () {}
    public Result (Status status, Object returnValue) {
        this.setStatus(status);
        this.setReturnValue(returnValue);
        log.info(this.toString());
    }

    @Override
    public String toString() {
        return String.format("{ status: %s, value: %s }", getStatus().toString(), getReturnValue().toString());
    }
}
