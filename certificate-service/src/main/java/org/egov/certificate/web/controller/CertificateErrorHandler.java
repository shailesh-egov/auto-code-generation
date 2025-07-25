package org.egov.certificate.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.certificate.util.ErrorConstants;
import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class CertificateErrorHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request, CustomException ex) {
        log.error("CustomException occurred: {}", ex.getMessage(), ex);
        
        // Try to parse the error code as integer, fallback to default if it fails
        int errorCode;
        try {
            errorCode = Integer.parseInt(ex.getCode());
        } catch (NumberFormatException e) {
            log.warn("CustomException code '{}' is not a valid integer, using default", ex.getCode());
            errorCode = Integer.parseInt(ErrorConstants.CUSTOM_ERROR_CODE);
        }
        
        Error error = Error.builder()
                .code(errorCode)
                .message(ex.getMessage())
                .description("Request processing failed")
                .build();
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseInfo(createErrorResponseInfo())
                .error(error)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(HttpServletRequest request, 
                                                                   MethodArgumentNotValidException ex) {
        log.error("Validation exception occurred: {}", ex.getMessage(), ex);
        
        List<String> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(errorItem -> {
            String fieldName = ((FieldError) errorItem).getField();
            String errorMessage = errorItem.getDefaultMessage();
            fieldErrors.add(fieldName + ": " + errorMessage);
        });

        String combinedMessage = String.join(", ", fieldErrors);
        
        Error error = Error.builder()
                .code(Integer.parseInt(ErrorConstants.VALIDATION_ERROR_CODE))
                .message(combinedMessage)
                .description(ErrorConstants.VALIDATION_ERROR_DESCRIPTION)
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseInfo(createErrorResponseInfo())
                .error(error)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(HttpServletRequest request, BindException ex) {
        log.error("Bind exception occurred: {}", ex.getMessage(), ex);
        
        List<String> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(errorItem -> {
            String fieldName = errorItem instanceof FieldError ? ((FieldError) errorItem).getField() : errorItem.getObjectName();
            String errorMessage = errorItem.getDefaultMessage();
            fieldErrors.add(fieldName + ": " + errorMessage);
        });

        String combinedMessage = String.join(", ", fieldErrors);
        
        Error error = Error.builder()
                .code(Integer.parseInt(ErrorConstants.BINDING_ERROR_CODE))
                .message(combinedMessage)
                .description(ErrorConstants.BINDING_ERROR_DESCRIPTION)
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseInfo(createErrorResponseInfo())
                .error(error)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(HttpServletRequest request, Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        Error error = Error.builder()
                .code(Integer.parseInt(ErrorConstants.INTERNAL_SERVER_ERROR_CODE))
                .message(ErrorConstants.INTERNAL_SERVER_ERROR_MESSAGE)
                .description(ex.getMessage())
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .responseInfo(createErrorResponseInfo())
                .error(error)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseInfo createErrorResponseInfo() {
        return ResponseInfo.builder()
                .apiId("certificate-service")
                .ver("1.0")
                .ts(System.currentTimeMillis())
                .status("failed")
                .build();
    }
}