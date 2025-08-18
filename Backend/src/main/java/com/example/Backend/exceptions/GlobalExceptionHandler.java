package com.example.Backend.exceptions;


import com.example.Backend.dtos.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {

        ResponseData<?> responseData = ResponseData.builder()
                .message(ex.getMessage())
                .status(500)
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(500).body("An unexpected error occurred: " + ex.getMessage());
    }
}
