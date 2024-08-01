package com.danzir.springboot.oauth2.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    protected ResponseEntity<Map> handleConflict(WebClientResponseException ex) {
        Map respBody = new HashMap(){{
            put("message", ex.getMessage());
            put("status", ex.getStatusText());
            put("status code", ex.getStatusCode().value());
        }};
        return ResponseEntity.status(ex.getStatusCode()).body(respBody);
    }

}
