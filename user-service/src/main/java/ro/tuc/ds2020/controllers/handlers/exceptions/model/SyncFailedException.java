package ro.tuc.ds2020.controllers.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class SyncFailedException extends CustomException {
    private static final String MESSAGE = "Could not synchronize with device-service!";
    private static final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

    public SyncFailedException(String resource) {
        super(MESSAGE, httpStatus, resource, new ArrayList<>());
    }
}
