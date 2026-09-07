package com.placelyt.placelyt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserSkillException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUserSkill(
            DuplicateUserSkillException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicatePreferenceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicatePreference(
            DuplicatePreferenceException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(
            DuplicateEmailException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateCompanyException.class)
public ResponseEntity<Map<String, String>> handleDuplicateCompany(
        DuplicateCompanyException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}

@ExceptionHandler(CompanyNotFoundException.class)
public ResponseEntity<Map<String, String>> handleCompanyNotFound(
        CompanyNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
}