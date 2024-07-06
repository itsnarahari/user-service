package com.user.exception;

import io.jsonwebtoken.lang.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationException(ValidationException exception, WebRequest request){
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = exception.getErrors().stream().map(MixedError::getError).collect(Collectors.toList());
        body.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler
    public ResponseEntity<?> exception(Exception exception, WebRequest request){
        exception.printStackTrace();
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", List.of(exception.getMessage()));
        ErrorDetails details = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(details);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException exception, WebRequest request){
        exception.printStackTrace();
        ErrorDetails details = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(details);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorDetails> applicationException(ApplicationException exception, WebRequest request) {

        ErrorDetails details = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false), exception.getStatusCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList();
        if(!Collections.isEmpty(ex.getBindingResult().getFieldErrors())){
            for (FieldError allError: ex.getBindingResult().getFieldErrors()){
                errors.add(String.format("%s: %s", allError.getField(),allError.getDefaultMessage()));
            }
        }
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", List.of("ex.getMessage()+\" For \"+ex.getParameterName()"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

