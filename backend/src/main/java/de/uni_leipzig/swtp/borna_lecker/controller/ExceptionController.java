package de.uni_leipzig.swtp.borna_lecker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import de.uni_leipzig.swtp.borna_lecker.exceptions.IDNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity<Object> handleIDnotFoundException(IDNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleSecurityException(SecurityException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(Exception e) {
        return new ResponseEntity<>("Ein Fehler ist aufgetreten \n" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
