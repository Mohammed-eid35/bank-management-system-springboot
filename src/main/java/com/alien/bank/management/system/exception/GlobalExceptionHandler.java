package com.alien.bank.management.system.exception;

import com.alien.bank.management.system.model.ResponseModel;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .badRequest()
                .body(ResponseModel
                        .builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .errors(errors)
                        .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(
                        ResponseModel
                                .builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .success(false)
                                .errors(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ResponseModel
                                .builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .success(false)
                                .errors(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseModel
                        .builder()
                        .status(HttpStatus.NOT_FOUND)
                        .success(false)
                        .errors(ex.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleEntityExistsException(EntityExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ResponseModel
                                .builder()
                                .status(HttpStatus.CONFLICT)
                                .success(false)
                                .errors(ex.getMessage())
                                .build()
                );
    }


    // Authentication
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<ResponseModel> handelBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ResponseModel
                                .builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .success(false)
                                .errors(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ResponseModel
                                .builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .success(false)
                                .errors("Authentication credentials not found")
                                .build()
                );
    }

    // Transaction
    @ExceptionHandler(LowBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleLowBalanceException(LowBalanceException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel
                        .builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .errors(ex.getMessage())
                        .build()
                );
    }
}