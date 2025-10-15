package com.jono.restapi.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        final Map<String, List<String>> errors = ex.getBindingResult()
                                                   .getFieldErrors()
                                                   .stream()
                                                   .collect(Collectors.groupingBy(
                                                           FieldError::getField,
                                                           LinkedHashMap::new,
                                                           Collectors.mapping(
                                                                   fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                                                                   Collectors.toList()
                                                           )
                                                   ));

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }*/

}
