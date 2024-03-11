package org.example.vkintership.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonException() {
        return new ResponseEntity<>("Server error, please, try later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
