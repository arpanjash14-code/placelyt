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

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleJobNotFound(
            JobNotFoundException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSkillNotFound(
            SkillNotFoundException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudentProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleStudentProfileNotFound(
            StudentProfileNotFoundException exception) {

        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateApplicationException.class)
public ResponseEntity<Map<String, String>> handleDuplicateApplication(
        DuplicateApplicationException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}

@ExceptionHandler(ApplicationNotFoundException.class)
public ResponseEntity<Map<String, String>> handleApplicationNotFound(
        ApplicationNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(JobNotOpenException.class)
public ResponseEntity<Map<String, String>> handleJobNotOpen(
        JobNotOpenException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(InvalidApplicationStatusTransitionException.class)
public ResponseEntity<Map<String, String>> handleInvalidApplicationStatusTransition(
        InvalidApplicationStatusTransitionException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(ApplicationOwnershipException.class)
public ResponseEntity<Map<String, String>> handleApplicationOwnership(
        ApplicationOwnershipException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
}

@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<Map<String, String>> handleUserNotFound(
        UserNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(EducationNotFoundException.class)
public ResponseEntity<Map<String, String>> handleEducationNotFound(
        EducationNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(EducationOwnershipException.class)
public ResponseEntity<Map<String, String>> handleEducationOwnership(
        EducationOwnershipException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
}

@ExceptionHandler(ExperienceNotFoundException.class)
public ResponseEntity<Map<String, String>> handleExperienceNotFound(
        ExperienceNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(ExperienceOwnershipException.class)
public ResponseEntity<Map<String, String>> handleExperienceOwnership(
        ExperienceOwnershipException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
}

@ExceptionHandler(DuplicateSkillException.class)
public ResponseEntity<Map<String, String>> handleDuplicateSkill(
        DuplicateSkillException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}

@ExceptionHandler(DuplicateStudentProfileException.class)
public ResponseEntity<Map<String, String>> handleDuplicateStudentProfile(
        DuplicateStudentProfileException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}

@ExceptionHandler(UserSkillNotFoundException.class)
public ResponseEntity<Map<String, String>> handleUserSkillNotFound(
        UserSkillNotFoundException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(UserSkillOwnershipException.class)
public ResponseEntity<Map<String, String>> handleUserSkillOwnership(
        UserSkillOwnershipException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
}

@ExceptionHandler(InvalidExperienceException.class)
public ResponseEntity<Map<String, String>> handleInvalidExperience(
        InvalidExperienceException exception) {

    Map<String, String> error = new HashMap<>();
    error.put("error", exception.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
}