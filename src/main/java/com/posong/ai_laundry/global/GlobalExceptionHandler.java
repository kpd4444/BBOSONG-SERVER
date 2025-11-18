package com.posong.ai_laundry.global;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustom(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.error(code.getStatus(), code.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {

        // 필요하면 에러 로그 찍어도 됨
        // e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)   // 401
                .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                ));
    }
}