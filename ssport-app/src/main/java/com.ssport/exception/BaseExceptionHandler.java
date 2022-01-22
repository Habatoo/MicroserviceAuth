package com.ssport.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BaseExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseExceptionHandler.class);

    private static final String DATABASE_ERROR_MSG = "Error while performing database operation: {0}";

    @ExceptionHandler(SportAppException.class)
    public ResponseEntity<Object> handlePrymeTimeException(final SportAppException ex) {
        LOGGER.error("Handling PrymeTimeException with error code: {} and status: {}", ex.getCode(),
                ex.getCode().getHttpStatus(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();
        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ex.getCode(), ex.getDescription()));
        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, ex.getCode().getHttpStatus());
    }

    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<?> handleOauth2Exception(final OAuth2Exception ex) {
        LOGGER.error("Authentication request failed: " + ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();
        List<SportAppErrorResponse> errors = new ArrayList<>();

        if (ex instanceof CustomOauthException) {
            CustomOauthException customOauthException = (CustomOauthException) ex;
            errors.add(new SportAppErrorResponse(customOauthException, customOauthException.getCode()));
        } else {
            errors.add(new SportAppErrorResponse(ex));
        }

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleRequestParamsValidationException(final ConstraintViolationException ex) {
        LOGGER.error("Handling ValidationException with message: {} caused by: {}", ex, ex);

        List<SportAppErrorResponse> errors = ex.getConstraintViolations().stream()
                .map(e -> new SportAppErrorResponse(ex, ErrorCode.VALIDATION_ERROR))
                .collect(Collectors.toList());
        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(final DataAccessException ex) {
        LOGGER.error("Handling DataAccessException with message: {} caused by: {}", ex.getMessage(), ex.getCause(), ex);

        String msg = MessageFormat.format(DATABASE_ERROR_MSG, ex.getMessage());
        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();
        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.UNPROCESSABLE_ENTITY, msg));
        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(final AccessDeniedException ex) {
        LOGGER.error("Handling AccessDeniedException with message: {}", ex.getMessage(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.ACCESS_DENIED, ex.getMessage()));

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundException(final UsernameNotFoundException ex) {
        LOGGER.error("Handling UsernameNotFoundException with message: {}", ex.getMessage(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.USERNAME_OR_PASSWORD_ERROR, ex.getMessage()));

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(final BadCredentialsException ex) {
        LOGGER.error("Handling BadCredentialsException with message: {}", ex.getMessage(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.USERNAME_OR_PASSWORD_ERROR, ex.getMessage()));

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleAccessDeniedException(final MethodArgumentTypeMismatchException ex) {
        LOGGER.error("Handling MethodArgumentTypeMismatchException with message: {} caused by: {}", ex.getMessage(), ex.getCause(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.VALIDATION_ERROR, ex.getMessage()));

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        LOGGER.error("Handling MethodArgumentNotValidException with message: {} caused by: {}", ex.getMessage(), ex.getCause(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new SportAppErrorResponse(ErrorCode.VALIDATION_ERROR, error.getField() + " " + error.getDefaultMessage()));
        }

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<?> handleClientUnauthorizedException(final HttpClientErrorException.Unauthorized ex) {
        LOGGER.error("Handling HttpClientErrorException.Unauthorized with message: {} caused by: {}", ex.getMessage(), ex.getCause(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ErrorCode.AUTHORIZATION_ERROR, ex.getMessage()));

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        LOGGER.error("Handling HttpMessageNotReadableException.Unauthorized with message: {}", ex.getMessage(), ex);

        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();

        List<SportAppErrorResponse> errors = new ArrayList<>();
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
            invalidFormatException.getPath()
                    .forEach(reference -> errors.add(new SportAppErrorResponse(ErrorCode.VALIDATION_ERROR, invalidFormatException.getOriginalMessage() + " at " + reference.getFieldName())));
        }

        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception ex) {
        LOGGER.error("Handling unexpected exception with message: {} caused by: {}", ex.getMessage(), ex.getCause(), ex);

        SportAppErrorResponse resp = new SportAppErrorResponse(ex);
        Map<String, List<SportAppErrorResponse>> errorResponse = new HashMap<>();
        List<SportAppErrorResponse> errors = new ArrayList<>();
        errors.add(new SportAppErrorResponse(ex));
        errorResponse.put("errors", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
