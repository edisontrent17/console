package com.acme.data360agent.web;

import com.acme.data360agent.data360.ConnectApiException;
import com.acme.data360agent.support.SensitiveData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", SensitiveData.redactText(e.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("error", SensitiveData.redactText(e.getMessage())));
    }

    @ExceptionHandler({CompletionException.class, ExecutionException.class})
    ResponseEntity<Map<String, Object>> async(Exception e) {
        var cause = rootCause(e);
        if (cause instanceof IllegalArgumentException illegalArgumentException) {
            return badRequest(illegalArgumentException);
        }
        return server(e);
    }

    @ExceptionHandler(ConnectApiException.class)
    ResponseEntity<Map<String, Object>> connectApi(ConnectApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", SensitiveData.redactText(e.getMessage()),
                "statusCode", e.statusCode(),
                "correlationId", UUID.randomUUID().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> server(Exception e) {
        var cause = rootCause(e);
        if (cause instanceof IllegalArgumentException illegalArgumentException) {
            return badRequest(illegalArgumentException);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error.",
                "correlationId", UUID.randomUUID().toString()
        ));
    }

    private Throwable rootCause(Throwable throwable) {
        var current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
