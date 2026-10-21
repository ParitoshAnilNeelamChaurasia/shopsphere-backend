package com.shopsphere.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return response(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            BadCredentialsException.class
    })
    ResponseEntity<?> badRequest(Exception ex) {

        return response(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> internal(Exception ex) {

        // IMPORTANT:
        // Print the real exception and complete stack trace
        // in the IntelliJ console so we can find the actual problem.
        ex.printStackTrace();

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                null
        );
    }

    private ResponseEntity<?> response(
            HttpStatus status,
            String message,
            Object details
    ) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("message", message);

        if (details != null) {
            body.put("details", details);
        }

        return ResponseEntity
                .status(status)
                .body(body);
    }
}