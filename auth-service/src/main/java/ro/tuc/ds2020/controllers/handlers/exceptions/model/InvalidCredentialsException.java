package ro.tuc.ds2020.controllers.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class InvalidCredentialsException extends CustomException {
    private static final String MESSAGE = "Invalid username or password!";
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidCredentialsException() {
        super(MESSAGE, httpStatus, "Credentials", new ArrayList<>());
    }
}