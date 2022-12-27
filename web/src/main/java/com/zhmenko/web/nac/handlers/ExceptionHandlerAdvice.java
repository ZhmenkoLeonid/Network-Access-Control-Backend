package com.zhmenko.web.nac.handlers;

import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.web.nac.exceptions.BadRequestException;
import com.zhmenko.web.nac.exceptions.connect.ConnectException;
import com.zhmenko.web.nac.exceptions.illegal_state.IllegalStateException;
import com.zhmenko.web.nac.exceptions.not_found.NotFoundException;
import com.zhmenko.web.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler({NotFoundException.class, UserNotExistException.class})
    public ResponseEntity<Error> handleException(RuntimeException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Error> handleException(BadRequestException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<Error> handleException(IllegalStateException e) {
        return new ResponseEntity<>(new Error(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ConnectException.class})
    public ResponseEntity<Error> handleException(ConnectException e) {
        return new ResponseEntity<>(new Error(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleException(BadCredentialsException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleException(AccessDeniedException e) {
        return new ResponseEntity<>(new Error(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Error> handleException(Exception e) {
        String msg;
        if (e instanceof ConstraintViolationException) {
            msg = ((ConstraintViolationException) e).getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        } else msg = "Validation Failed";

        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST.value(), msg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Error> handleException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                new Error(HttpStatus.BAD_REQUEST.value(), e.getFieldErrors()
                        .stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .collect(Collectors.joining("; "))),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleOtherExceptions(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
