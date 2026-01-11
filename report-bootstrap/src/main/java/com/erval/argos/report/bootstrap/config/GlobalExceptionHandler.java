package com.erval.argos.report.bootstrap.config;

import com.erval.argos.report.bootstrap.dto.StandardResultDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResultDto<String>> handleUnexpectedException(Exception ex) {
        StandardResultDto<String> errorResponse = StandardResultDto
                .error("An unexpected error occurred: " + ex.getMessage(), 500);
        return ResponseEntity.status(500).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResultDto<String>> handleIllegalArg(IllegalArgumentException ex) {
        StandardResultDto<String> resp = StandardResultDto.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardResultDto<String>> handleIllegalState(IllegalStateException ex) {
        StandardResultDto<String> resp = StandardResultDto.error(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

}
